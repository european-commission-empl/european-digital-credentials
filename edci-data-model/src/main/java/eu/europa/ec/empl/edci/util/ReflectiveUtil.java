package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.IReferenced;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.exception.ReflectiveException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("ReflectiveUtil")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReflectiveUtil {


    private static final Logger logger = LogManager.getLogger(ReflectiveUtil.class);

    @Autowired
    public Validator validator;

    public Set<Class> getAllClassesAnnotatedWith(Class<? extends Annotation> annotation, String basePackage) throws ClassNotFoundException {
        Set<Class> results = new HashSet<>();
        ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider(true, new StandardServletEnvironment());
        candidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        for (BeanDefinition beanDefinition : candidateComponentProvider.findCandidateComponents(basePackage)) {
            Class clazz = Class.forName(beanDefinition.getBeanClassName());
            if (clazz.isAnnotationPresent(annotation)) {
                results.add(clazz);
            }
        }
        return results;
    }

    public Set<Field> getAllFieldsAnnotatedWith(Class clazz, Class<? extends Annotation> annotation) {
        return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toSet());
    }

    public boolean isCollectionInstance(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }

    public boolean isCollectionInstance(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    public boolean isListInstance(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    public Object getOrInstanciateListItem(List list, Integer index, Field listField) throws ReflectiveOperationException {
        Object object = null;
        if (list.size() - 1 >= index) {
            object = list.get(index);
        } else {
            Class itemsType = getCollectionTypeClass(listField);
            object = itemsType.newInstance();
            list.add(object);
        }
        return object;
    }

    public boolean setField(Field field, Object target, Object value) {
        ReflectionUtils.setField(field, target, value);
        return true;
    }

    public boolean setField(String targetField, Object target, Object value) {
        Field field = findField(target.getClass(), targetField);
        ReflectionUtils.setField(field, target, value);
        return true;
    }

    public boolean setFieldBySetter(String fieldName, Object object, Object cellValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Method setter = getSetterMethod(object.getClass(), fieldName, cellValue.getClass());

        Object value = cellValue;
        //check for superClass Method
        if (this.getValidator().isEmpty(setter)) {
            setter = getSetterMethod(object.getClass(), fieldName, cellValue.getClass().getSuperclass());
        }

        //Check for an available constructor with the value class type (Mostly URI/URL fields)
        if (this.getValidator().isEmpty(setter)) {
            try {
                Field field = this.findField(object.getClass(), fieldName);
                Constructor constructor = field.getType().getConstructor(cellValue.getClass());
                value = constructor.newInstance(cellValue);
                setter = getSetterMethod(object.getClass(), fieldName, value.getClass());
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //check for a parseXXXX field in the object
        if (this.getValidator().isEmpty(setter)) {
            try {
                if (this.getValidator().notEmpty(value))
                    setter = getParserMethod(object.getClass(), fieldName, Object.class);
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //check for parse method
        if (this.getValidator().isEmpty(setter)) {
            try {
                Field field = this.findField(object.getClass(), fieldName);
                value = tryCreationMethod(field.getType(), "parse", cellValue);
                if (this.getValidator().notEmpty(value))
                    setter = getSetterMethod(object.getClass(), fieldName, value.getClass());
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //fix for decimal values
        if (this.getValidator().isEmpty(setter)) {
            try {
                value = new DecimalFormat("#.##").format(cellValue);
                if (this.getValidator().notEmpty(value))
                    setter = getSetterMethod(object.getClass(), fieldName, value.getClass());
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //check for valueOf method
        if (this.getValidator().isEmpty(setter)) {
            try {
                Field field = this.findField(object.getClass(), fieldName);
                value = tryCreationMethod(field.getType(), "valueOf", cellValue);
                if (this.getValidator().notEmpty(value))
                    setter = getSetterMethod(object.getClass(), fieldName, value.getClass());
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //fix for Boolean fields with different is/no is naming
        if (this.getValidator().isEmpty(setter) && fieldName.startsWith("is")) {
            try {
                if (setFieldBySetter(fieldName.substring(2), object, cellValue)) return true;
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        //Retry after trim
        if (this.getValidator().isEmpty(setter) && cellValue instanceof String) {
            try {
                String cellValueStr = (String) cellValue;
                if (cellValueStr.startsWith(" ") || cellValueStr.endsWith(" ")) {
                    return this.setFieldBySetter(fieldName, object, cellValueStr.trim());
                }
            } catch (Exception e) {
                logger.trace(e);
            }
        }

        if (this.getValidator().isEmpty(setter))
            throw new ReflectiveException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_NOSETTER, fieldName,
                    object.getClass().getSimpleName(),
                    cellValue.getClass().getSimpleName(),
                    cellValue.toString());

        setter.invoke(object, value);

        return true;
    }

    private Object tryCreationMethod(Class clazz, String fieldName, Object value) throws IllegalAccessException, InvocationTargetException {
        Object object = null;
        Method parseMethod = ReflectionUtils.findMethod(clazz, fieldName, value.getClass());
        if (this.getValidator().isEmpty(parseMethod) && ClassUtils.isPrimitiveWrapper(value.getClass())) {
            parseMethod = ReflectionUtils.findMethod(clazz, fieldName, ClassUtils.wrapperToPrimitive(value.getClass()));
        }
        if (this.getValidator().isEmpty(parseMethod)) {
            parseMethod = ReflectionUtils.findMethod(clazz, fieldName, null);
        }
        if (this.getValidator().notEmpty(parseMethod) && parseMethod.getParameterTypes().length == 1 && parseMethod.getReturnType() != void.class) {
            if (parseMethod.getParameterTypes()[0].isAssignableFrom(value.getClass()) || parseMethod.getParameterTypes()[0].isAssignableFrom(ClassUtils.wrapperToPrimitive(value.getClass()))) {
                if (Modifier.isStatic(parseMethod.getModifiers())) {
                    object = parseMethod.invoke(null, value);
                }
            }
        }
        return object;
    }


    private Method getSetterMethod(Class clazz, String fieldName, Class valueClass) throws NoSuchMethodException {
        Method method = ReflectionUtils.findMethod(clazz, getSetterName(fieldName), valueClass);
        if (this.getValidator().isEmpty(method))
            method = ReflectionUtils.findMethod(clazz, getSetterName(fieldName.trim()), valueClass);
        return method;
    }

    private String getSetterName(String field) {
        return "set" + StringUtils.capitalize(field);
    }

    private Method getParserMethod(Class clazz, String fieldName, Class valueClass) throws NoSuchMethodException {
        Method method = ReflectionUtils.findMethod(clazz, getParserName(fieldName), valueClass);
        if (this.getValidator().isEmpty(method))
            method = ReflectionUtils.findMethod(clazz, getParserName(fieldName.trim()), valueClass);
        return method;
    }

    private String getParserName(String field) {
        return "parse" + StringUtils.capitalize(field);
    }

    /**
     * Gets the Class for the items inside a list (ie List of Note will return Note class)
     *
     * @param listField the list field
     * @return the class of the items in the list
     */
    public Class getCollectionTypeClass(Field listField) {
        Type type = listField.getGenericType();
        Class<?> _class = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] types = pType.getActualTypeArguments();

            if (types.length == 1) {
                _class = (Class<?>) types[0];
                logger.trace("scanning generic type " + _class.getName());
            } else {
                throw new ReflectiveException(EDCIMessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR);
            }
        } else {
            throw new ReflectiveException(EDCIMessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR);
        }
        return _class;
    }

    private Object castToChild(Class destClass, Object instance, @Nullable Object parentInstance) throws ReflectiveOperationException {
        if (!destClass.getSuperclass().equals(instance.getClass()))
            throw new ReflectiveException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_NOTASUBCLASS, destClass.getSimpleName(), instance.getClass().getSimpleName());
        Object childInstance = destClass.newInstance();
        ReflectionUtils.shallowCopyFieldState(instance, childInstance);
        if (this.getValidator().notEmpty(parentInstance)) {
            Field parentField = ReflectionUtils.findField(parentInstance.getClass(), null, childInstance.getClass().getSuperclass());
            this.setFieldBySetter(parentField.getName(), parentInstance, childInstance);
        }
        return childInstance;
    }

    public Object getOrInstanceField(Field targetField, Object instance, @Nullable Object parentInstance) throws ReflectiveOperationException {
        if (this.getValidator().isEmpty(parentInstance) || classContainsField(instance.getClass(), targetField.getName())) {
            return getOrInstanceField(targetField, instance);
        } else if (classContainsField(targetField.getDeclaringClass(), targetField.getName())) {
            //Trying to set first field of a child class, casting object to child
            Object childInstance = castToChild(targetField.getDeclaringClass(), instance, parentInstance);
            logger.trace("Casted To child Object [{}] -> [{}]", () -> instance.getClass().getName(), () -> childInstance.getClass().getName());
            return getOrInstanceField(targetField, childInstance);
        }

        throw new ReflectiveException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_GETFIELD, targetField.getName(), instance.getClass().getSimpleName());
    }

    /**
     * Safe method to get a field from an instance (be it a collection or a "simple" object), if null, a new innerInstance is created
     *
     * @param targetField the field to be extracted
     * @param instance    the instance from the field should be extracted
     * @return the object, or a new instance if not found
     * @throws ReflectiveOperationException when a Reflection error occurs
     */
    public Object getOrInstanceAnyField(Field targetField, Object instance, Object... params) throws
            ReflectiveOperationException {

        Object returnObject = null;

        if (isCollectionInstance(targetField)) {
            returnObject = getOrInstanceCollectionField(targetField, instance);
        } else {
            returnObject = getOrInstanceField(targetField, instance, params);
        }

        return returnObject;

    }

    /**
     * Returns true if a class has a constructor without parameters
     *
     * @param clazz
     * @return
     */
    public boolean hasParameterlessPublicConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Safe method to get a field from an instance (be it a collection or a "simple" object), if null, a new innerInstance is created
     *
     * @param field    the field to be extracted
     * @param instance the instance from the field should be extracted
     * @return the object, or a new instance if not found
     * @throws ReflectiveOperationException when a Reflection error occurs
     */
    public Object getOrInstanceAnyField(String field, Object instance) throws
            ReflectiveOperationException {

        Object returnObject = null;

        Field targetField = findField(instance.getClass(), field);
        if (field == null) {
            return null;
        }

        if (isCollectionInstance(targetField)) {
            returnObject = getOrInstanceCollectionField(targetField, instance);
        } else {
            returnObject = getOrInstanceField(targetField, instance);
        }

        return returnObject;

    }

    /**
     * Safe method to get a field from an instance, if null, a new innerInstance is created
     *
     * @param targetField the field to be extracted
     * @param instance    the instance from the field should be extracted
     * @return the object, or a new instance if not found
     * @throws ReflectiveOperationException when a Reflection error occurs
     */
    private Object getOrInstanceField(Field targetField, Object instance, Object... params) throws
            ReflectiveOperationException {
        ReflectionUtils.makeAccessible(targetField);
        Object object = null;

        object = ReflectionUtils.getField(targetField, instance);

        if (object == null) {
            if (targetField.getType() == ZonedDateTime.class) {
                object = ZonedDateTime.now();
            } else if (params != null && params.length > 0) {
                object = targetField.getType().getConstructor(Arrays.stream(params).map(Object::getClass).collect(Collectors.toList()).toArray(new Class[]{})).newInstance(params);
            } else {
                object = targetField.getType().newInstance();
            }
            ReflectionUtils.setField(targetField, instance, object);
        }
        return object;
    }

    public Object getField(Field targetField, IReferenced instance) {
        ReflectionUtils.makeAccessible(targetField);
        Object object = ReflectionUtils.getField(targetField, instance.getReferenced());
        return object;
    }

    public Object getField(Field targetField, Object instance) {
        ReflectionUtils.makeAccessible(targetField);
        Object object;

        if (instance instanceof IReferenced) {
            object = ReflectionUtils.getField(targetField, ((IReferenced) instance).getReferenced());
        } else {
            object = ReflectionUtils.getField(targetField, instance);
        }

        return object;
    }

    public Object getField(String targetField, Object instance) {
        Field field = findField(instance.getClass(), targetField);
        if (field == null) {
            return null;
        }
        ReflectionUtils.makeAccessible(field);
        Object object = ReflectionUtils.getField(field, instance);
        return object;
    }

    /**
     * Safe method to get a field from an instance, if null, a new innerInstance is created
     *
     * @param targetField    the field to be extracted
     * @param parentInstance the instance from the field should be extracted
     * @return the object, or a new instance if not found
     * @throws ReflectiveOperationException when a Reflection error occurs
     */
    public List<Object> getOrInstanceListField(Field targetField, Object parentInstance) throws
            ReflectiveOperationException {
        ReflectionUtils.makeAccessible(targetField);
        List<Object> object = (List<Object>) ReflectionUtils.getField(targetField, parentInstance);
        if (object == null) {
            object = new ArrayList<>();
            ReflectionUtils.setField(targetField, parentInstance, object);
        }

        return object;
    }

    public Collection<Object> getOrInstanceCollectionField(Field targetField, Object parentInstance) {
        ReflectionUtils.makeAccessible(targetField);
        Collection<Object> collection = (Collection<Object>) ReflectionUtils.getField(targetField, parentInstance);
        if (collection == null) {
            collection = new ArrayList<>();
            try {
                ReflectionUtils.setField(targetField, parentInstance, collection);
            } catch (Exception e) {
                collection = new HashSet<>();
                ReflectionUtils.setField(targetField, parentInstance, collection);
            }
        }
        return collection;
    }


    /**
     * Searches for an object type inside a list, returns the first found or a new instance.
     * RELIES ON THAT ONLY ONE OBJECT PER TYPE CAN BE CREATED IN A ROW.
     *
     * @param objects the objects already created in that row
     * @param _class  the object type to search  for
     * @return the object reference from the list, or a new instance (which is also added to the list)
     * @throws ReflectiveOperationException when a Reflection error occurs
     */
    public Object findOrCreateInstanceOf(List<Object> objects, Class _class) throws
            ReflectiveOperationException {
        Object instance = findInstanceOf(objects, _class.getName());
        if (this.getValidator().isEmpty(instance)) {
            logger.trace("Creating instance of class [{}]", () -> _class.getName());
            instance = _class.newInstance();
            objects.add(instance);
        } else {
            instance = ((Optional) instance).get();
        }
        return instance;
    }

    /**
     * Find an instance of an object in a list
     *
     * @param objects   the lists of objects
     * @param className the className of the instance that needs to be found
     * @return the object reference
     */
    public Object findInstanceOf(List<Object> objects, String className) {
        return objects.stream().filter(object -> object.getClass().getName().equals(className)).findFirst();
    }

    public Object findOrCreateInstanceOf(String className, List<Object> objects) throws ReflectiveOperationException {
        Class<?> _class;
        _class = Class.forName(className);
        Object instance;
        instance = this.findOrCreateInstanceOf(new ArrayList<Object>(objects), _class);
        return instance;
    }

    public boolean isChildField(Class clazz, Field field) {
        return field.getDeclaringClass().getSuperclass().equals(clazz);
    }

    public List<Field> getFields(Object object) {
        return Arrays.asList(object.getClass().getDeclaredFields());
    }

    public List<Field> getListFields(Object object) {
        return this.getFields(object).stream().filter(field -> this.isCollectionInstance(field)).collect(Collectors.toList());
    }

    public Field findField(Object instance, Class fieldType) {
        return ReflectionUtils.findField(instance.getClass(), null, fieldType);
    }

    public Field findField(Class clazz, String fieldName) {
        Field field = null;
        field = ReflectionUtils.findField(clazz, fieldName);
        //Check for normal field (class + parent) with trim
        if (this.getValidator().isEmpty(field)) field = ReflectionUtils.findField(clazz, fieldName.trim());
        //Check for Uncapitalized field
        if (this.getValidator().isEmpty(field))
            field = ReflectionUtils.findField(clazz, StringUtils.uncapitalize(fieldName));
        //Check for Capitalized field
        if (this.getValidator().isEmpty(field))
            field = ReflectionUtils.findField(clazz, StringUtils.capitalize(fieldName));
        //Check for field in child classes
        if (this.getValidator().isEmpty(field)) {
            try {
                Class childClass = getChildClass(clazz, fieldName);
                if (this.getValidator().notEmpty(childClass)) {
                    field = ReflectionUtils.findField(childClass, fieldName);
                }
            } catch (ClassNotFoundException e) {
                logger.error(e);
            }
        }
        //Check for  field in boolean for fields that are isXXXX
        if (this.getValidator().isEmpty(field)) field = ReflectionUtils.findField(clazz, "is".concat(fieldName.trim()));
        //Check for sub fields
        if (this.getValidator().isEmpty(field)) {
            if (fieldName.contains(".")) {
                String[] fields = fieldName.split("\\.");
                Field subField = null;
                Class childClass = clazz;
                for(int i = 0; i < fields.length; ++i) {
                    subField = ReflectionUtils.findField(childClass, fields[i]);

                    if(this.getValidator().isEmpty(subField)) {
                        break;
                    }

                    childClass = subField.getType();
                }
                field = subField;
            }
        }
        if (this.getValidator().isEmpty(field))
            throw new ReflectiveException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_FIELDNOTFOUND, fieldName, clazz.getSimpleName());
        return field;
    }

    public Class getChildClass(Class clazz, String fieldName) throws ClassNotFoundException {
        Optional<Class> child = null;
        List<Class> classes = getChildClasses(clazz);
        child = classes.stream().filter(item -> classContainsField(item, fieldName)).findFirst();
        return child.isPresent() ? child.get() : null;
    }

    public boolean classContainsField(Class clazz, String fieldName) {
        boolean found = false;

        if (Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(fieldName))) {
            found = true;
        }

        if (ReflectionUtils.findField(clazz, fieldName) != null) {
            found = true;
        }


        return found;

    }

    public List<Class> getChildClasses(Class clazz) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));
        Set<BeanDefinition> components = provider.findCandidateComponents("eu/europa/ec/empl/edci/datamodel/model");
        List<Class> classes = new ArrayList<Class>();
        for (BeanDefinition component : components) {
            classes.add(Class.forName(component.getBeanClassName()));
        }
        return classes;
    }

    public boolean isPrimitiveList(Field field) {
        return this.getCollectionTypeClass(field).equals(String.class);
    }

    //TODO -> RESTORE WHEN NAMEABLE IS BACK
    public <T> Set<T> getUniqueInnerObjectsOfType(Class<T> clazz, Object rootAsset) {

        return new HashSet<>(getInnerObjectsOfType(clazz, rootAsset));
    }

    public <T> List<T> getInnerObjectsOfType(Class<T> clazz, Object rootAsset) {
        //TODO: unify getInnerObjectsOfType and findAllObjectsRecInIdentifiables
        return new ResourcesUtil().findAllObjectsInRecursivily(rootAsset, clazz, Identifiable.class);
    }

    public <T> List<T> getInnerObjectsOfType(Class<T> clazz, Object rootAsset, ResourcesUtil.FieldFilter<T> filter) {
        //TODO: unify getInnerObjectsOfType and findAllObjectsRecInIdentifiables
        return new ResourcesUtil().findAllObjectsInRecursivily(rootAsset, clazz, filter, Identifiable.class);
    }

    public <T> Map<Method, Set<Object>> getUniqueInnerMethodsOfType(Class<T> clazz, Object rootAsset) {
        //TODO: unify getInnerObjectsOfType and findAllObjectsRecInIdentifiables
        return new ResourcesUtil().findAllMethodsInRecursivily(rootAsset, clazz, Identifiable.class);
    }

    public Map<String, List<Object>> getTypesHashMap(Collection collection) {
        Map<String, List<Object>> orderedObjects = new HashMap<String, List<Object>>();
        for (Object object : collection) {
            if (!orderedObjects.containsKey(object.getClass().getName())) {
                orderedObjects.put(object.getClass().getName(), new ArrayList<Object>());
            }
            orderedObjects.get(object.getClass().getName()).add(object);
        }
        return orderedObjects;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    protected <T> void doWithInnerObjectsOfType(Class<T> clazz, Object
            rootAsset, Consumer<T> action, String basePackage) {
        this.getInnerObjectsOfType(clazz, rootAsset).stream().forEach(action);
    }

    //Get last instance of a PropertyPath. PropertyPath has format field.fieldlist[0] (ie credentialSubject.alternativeName[0]). //ToDo -> move to ValidationUtil?
    public Object getLastInstanceFromPropertyPath(List<String> parameterPath, Object parentInstance) {
        Object lastInstance = parentInstance;

        for (String parameter : parameterPath) {
            String regex = ".*\\[\\d+\\]";
            //Check for list parameter
            if (Pattern.matches(regex, parameter)) {
                List<String> args = Arrays.asList(parameter.split("[\\[\\]]"));

                if (args.size() != 2)
                    throw new FileBaseDataException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_INVALID_PROPERTYPATH, parameter);

                Field field = findField(lastInstance.getClass(), args.get(0));
                try {
                    List<Object> list = getOrInstanceListField(field, lastInstance);
                    lastInstance = getOrInstanciateListItem(list, Integer.valueOf(args.get(1)), field);
                } catch (ReflectiveOperationException e) {
                    throw new FileBaseDataException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_PROPERTYPATH_NOTALIST, parameter);
                }
                //Single object parameter
            } else {
                Field field = findField(lastInstance.getClass(), parameter);
                try {
                    lastInstance = getOrInstanceField(field, lastInstance);
                } catch (ReflectiveOperationException e) {
                    throw new FileBaseDataException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_INVALID_PROPERTYPATH, parameter);
                }
            }
        }

        return lastInstance;
    }


}
