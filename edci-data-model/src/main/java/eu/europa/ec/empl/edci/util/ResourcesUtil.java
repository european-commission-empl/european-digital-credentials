package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIConflictException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Util class to do loop checks, search for objects/methods/collections inside a root object recursively or not and gets inside objects, you can search for other objects,
 * methods or collections.
 */
@Component("ResourcesUtil")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ResourcesUtil {

    public static final Logger logger = LogManager.getLogger(ResourcesUtil.class);

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param <T>        The Root base class
     * @param root       The root node
     * @param getRelElem Function that will be called to get the related object of the Root element
     * @param fieldName  Name of the relation beeing checked (Only used to inform the user)
     */
    public <T> void checkLoopLine(T root, Function<T, T> getRelElem, String fieldName) {
        checkLoopTree(new HashSet<>(), root, (r) -> new ArrayList<T>() {{
            add(getRelElem.apply(r));
        }}, fieldName);
    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param <T>         The Root base class
     * @param root        The root node
     * @param getRelElems Function that will be called to get the related objects of the Root element
     * @param fieldName   Name of the relation beeing checked (Only used to inform the user)
     */
    public <T> void checkLoopTree(T root, Function<T, Collection<T>> getRelElems, String fieldName) {
        checkLoopTree(new HashSet<>(), root, getRelElems, fieldName);
    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param <T>         the type parameter
     * @param orgPkList   the list of primary keys
     * @param elem        the root element
     * @param getRelElems Function that will be called to get the related objects of the Root element
     * @param fieldName   the field name
     */
    protected <T> void checkLoopTree(Set<Integer> orgPkList, T elem, Function<T, Collection<T>> getRelElems, String fieldName) {

        if (elem == null) {
            return;
        }

        if (!orgPkList.add(elem.hashCode())) {
            logger.error("A loop was found for with the relation " + fieldName + "-> hashCode(" +
                    orgPkList.stream().map(String::valueOf).collect(Collectors.joining(",")) + "," + elem.hashCode() + ")");
            throw new EDCIConflictException("exception.client.error.msg.builder.conflict", fieldName);
        }

        if (getRelElems.apply(elem) != null) {
            for (T orgAux : getRelElems.apply(elem)) {
                checkLoopTree(orgPkList, orgAux, getRelElems, fieldName);
            }
        }

        orgPkList.remove(elem.hashCode());

    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param <S>  the type parameter
     * @param elem The root node
     */
    //TODO -> RESTORE WHEN IDENTIFIABLES ARE AVAILABLE
    public <S extends Identifiable> void checkContentClassLoopTree(S elem) {
        checkContentClassLoopTree(new LinkedHashMap<>(), elem);
    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param <S>       the type parameter
     * @param orgPkList the map of primary keys <id, name>
     * @param elem      the element
     */
    protected <S extends Identifiable> void checkContentClassLoopTree(LinkedHashMap<String, String> orgPkList, S elem) {

        if (elem == null || elem.getId() == null) {
            return;
        }

        if (orgPkList.containsKey(elem.getId().toString())) {

            List<String> loopElems = new ArrayList<>();
            boolean found = false;
            for (String id : orgPkList.keySet()) {
                if (found) {
                    loopElems.add(orgPkList.get(id));
                } else if (id.equals(elem.getId().toString())) {
                    found = true;
                    loopElems.add(elem.getId().toString());
                }
            }

            throw new EDCIConflictException("exception.client.error.msg.builder.conflict.full.loop", "'" + elem.getId().toString() + "'",
                    loopElems.stream().collect(Collectors.joining(" -> ", "'", "'")) + " -> '" + elem.getId().toString() + "')");

        } else if (elem.getId() != null) {
            orgPkList.put(elem.getId().toString(), elem.getName());
        }

        List<Identifiable> relElems = findAllObjectsIn(elem, Identifiable.class);

        if (relElems != null && !relElems.isEmpty()) {
            for (Identifiable orgAux : relElems) {
                checkContentClassLoopTree(orgPkList, orgAux);
            }
        }

        orgPkList.remove(elem.getId().toString());

    }

    /**
     * Gets collection from a root object based on a given method of the object, retrieved objects must match the given class type.<br>
     * <strong>WARNING</strong> only methods without parameters are allowed, methods with parameters will throw {@link IllegalArgumentException}
     *
     * @param <T>  the type parameter
     * @param root the root
     * @param type the type of the objects
     * @param m    the method
     * @return the collection
     */
    public <T> List<T> getCollectionFromMethod(Object root, Class<T> type, Method m) {

        List<T> elems = new ArrayList<>();

        try {

            Collection col = (Collection) m.invoke(root);


            if (col != null && !col.isEmpty()) {

                if (col.stream().anyMatch(c -> type.isInstance(c))) {
                    elems.addAll(col);
                }
            }

        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        }

        return elems;

    }

    /**
     * Gets object from a root object based on a given method of the object.<br>
     * <strong>WARNING</strong> only methods without parameters are allowed, methods with parameters will throw {@link IllegalArgumentException}
     *
     * @param <T>  the type parameter
     * @param root the root
     * @param m    the method
     * @return the object
     */
    public <T> T getObjectFromMethod(Object root, Method m) {

        T ident = null;

        try {

            ident = (T) m.invoke(root);

        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        }

        return ident;

    }

    /**
     * Searches inside the root object for any references to another objects of the class passed by parameter
     *
     * @param <T>  Type of objects that will be returned
     * @param root Root object
     * @param type Class of the objects that will be searched inside root
     * @return A list of Objects instances of T that are found inside the object root
     */
    public <T> List<T> findAllObjectsIn(Object root, Class<T> type) {

        List<T> elems = new ArrayList<>();

        List<Method> mListCol = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && Collection.class.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        List<Method> mListElem = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && m.getAnnotation(XmlTransient.class) == null
                        && type.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());


        for (Method m : mListCol) {
            elems.addAll(getCollectionFromMethod(root, type, m));
        }

        for (Method m : mListElem) {
            T obj = getObjectFromMethod(root, m);
            if (obj != null) {
                elems.add(obj);
            }
        }

        return elems;
    }

    /**
     * Searches inside the root object for any references to another objects of the class passed by parameter.
     * If path elements are not empty, a deeper search will be done in those objects.
     * An extra filter can be provided.
     *
     * @param <T>         the type parameter
     * @param root        the root
     * @param searchElems the search elements
     * @param filter      the filter
     * @param pathElems   the path elements
     * @return the list
     */
    public <T> List<T> findAllObjectsInRecursively(Object root, Class<T> searchElems, FieldFilter<T> filter, Class... pathElems) {
        List<T> identifiableObj = new ArrayList<>();
        findAllObjectsInRecursively(root, identifiableObj, searchElems, filter, pathElems);
        return identifiableObj;
    }

    /**
     * Searches inside the root object for any references to another objects of the class passed by parameter.
     * If path elements are not empty, a deeper search will be done in those objects
     *
     * @param <T>         the type parameter
     * @param root        the root
     * @param searchElems the search elements
     * @param pathElems   the path elements
     * @return the list
     */
    public <T> List<T> findAllObjectsInRecursively(Object root, Class<T> searchElems, Class... pathElems) {
        List<T> identifiableObj = new ArrayList<>();
        findAllObjectsInRecursively(root, identifiableObj, searchElems, null, pathElems);
        return identifiableObj;
    }

    /**
     * Searches inside the root object for any references to another objects of the class passed by parameter.
     * If path elements are not empty, a deeper search will be done in those objects <br>
     * An extra filter can be provided.
     * <Strong>WARNING: The result list is the provided one 'auxField', not the return</Strong>
     *
     * @param <T>         the type parameter
     * @param <R>         the type parameter
     * @param root        the root
     * @param auxField    the list of fields
     * @param searchElems the search elements
     * @param filter      the filter
     * @param pathElems   the path elements
     * @return the list
     */
    protected <T, R> List<R> findAllObjectsInRecursively(Object root, List<T> auxField, Class<T> searchElems, FieldFilter<T> filter, Class<R>... pathElems) {

        List<R> identifiableObj = new ArrayList<>();

        for (Class<R> pathElem : pathElems) {
            identifiableObj.addAll(findAllObjectsIn(root, pathElem));
        }

        for (R aux : identifiableObj) {
            identifiableObj = findAllObjectsInRecursively(aux, auxField, searchElems, filter, pathElems);
        }

        List<T> foundElems = findAllObjectsIn(root, searchElems);

        for (T elem : foundElems) {
            if (filter == null) {
                auxField.add(elem);
            } else if (filter.filter(elem)) {
                auxField.add(elem);
            }
        }

        return identifiableObj;

    }

    public interface FieldFilter<T> {
        boolean filter(T element);
    }

    /**
     * Find all methods inside an object root, this methods must return collections or the type provided and have no parameters.
     *
     * @param <T>  the type parameter
     * @param root the root
     * @param type the type
     * @return the list of methods
     */
    public <T> List<Method> findAllMethodsIn(Object root, Class<T> type) {

        List<Method> elems = new ArrayList<>();

        List<Method> mListCol = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && Collection.class.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        List<Method> mListElem = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && type.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        elems.addAll(mListCol);
        elems.addAll(mListElem);

        return elems;
    }

    /**
     * Find all methods inside an object root, this methods must return collections or the type provided and have no parameters.
     * If path elements are not empty, a deeper search will be done in those objects
     *
     * @param <T>         the type parameter
     * @param root        the root
     * @param searchElems the search elements
     * @param pathElems   the path elements
     * @return the map
     */
    public <T> Map<Method, Set<Object>> findAllMethodsInRecursively(Object root, Class<T> searchElems, Class... pathElems) {
        Map<Method, Set<Object>> identifiableObj = new HashMap<>();
        findAllMethodsInRecursively(root, identifiableObj, searchElems, pathElems);
        return identifiableObj;
    }

    /**
     * Find all methods inside an object root, this methods must return collections or the type provided and have no parameters.
     * If path elements are not empty, a deeper search will be done in those objects <br>
     * <Strong>WARNING: The result list is the provided one 'auxField', not the return</Strong>
     *
     * @param <T>         the type parameter
     * @param <R>         the type parameter
     * @param root        the root
     * @param auxField    the map of methods
     * @param searchElems the search elements
     * @param pathElems   the path elements
     * @return the list
     */
    protected <T, R> List<R> findAllMethodsInRecursively(Object root, Map<Method, Set<Object>> auxField, Class<T> searchElems, Class<R>... pathElems) {

        List<R> identifiableObj = new ArrayList<>();

        for (Class<R> pathElem : pathElems) {
            identifiableObj.addAll(findAllObjectsIn(root, pathElem));
        }

        for (R aux : identifiableObj) {
            identifiableObj = findAllMethodsInRecursively(aux, auxField, searchElems, pathElems);
        }

        List<Method> foundElems = findAllMethodsIn(root, searchElems);

        for (Method elem : foundElems) {
            if (!auxField.containsKey(elem)) {
                auxField.put(elem, new HashSet<>());
            }
            auxField.get(elem).add(root);
        }

        return identifiableObj;

    }

}
