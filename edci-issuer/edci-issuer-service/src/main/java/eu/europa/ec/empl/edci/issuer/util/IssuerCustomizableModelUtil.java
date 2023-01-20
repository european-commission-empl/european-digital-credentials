package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableCLField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableRelation;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.customization.CustomizableFieldDTO;
import eu.europa.ec.empl.edci.issuer.common.model.customization.CustomizableInstanceFieldDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IssuerCustomizableModelUtil {

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private Validator validator;

    private static final Logger logger = LogManager.getLogger(IssuerCustomizableModelUtil.class);

    /**
     * Given a Set of CustomizableFieldDTO, returns another Set of CustomizableFieldDTO containing the fields that are relate to fields included in the first list
     *
     * @param originalCustomizableFieldDTOs the CustomizableFieldDTOS that are to be checked
     * @param entityInstance                The Instance where the CustomizableFieldDTOS reside
     * @return the CustomizableFieldDTOS that are related to one of the originals and missing
     */
    public Set<CustomizableFieldDTO> getMissingRelatesToCustomizableFields(Set<CustomizableFieldDTO> originalCustomizableFieldDTOs, Object entityInstance) {
        return originalCustomizableFieldDTOs.stream().map(originalCustomizableFieldDTO -> {
            //Map Original CustomizableFieldDTOs back to CustomizableFields
            return this.getCustomizableFieldFromFieldPath(originalCustomizableFieldDTO.getFieldPath(), entityInstance);
        }).filter(customizableField -> {
            //filter CustomizableFields that relateTo and are not present in originalCustomizableFieldDTOs list
            return !customizableField.relatesTo().isEmpty() && originalCustomizableFieldDTOs.stream().noneMatch(customizableFieldDTO -> {
                return customizableField.relatesTo().equals(customizableFieldDTO.getFieldPath());
            });
        }).map(customizableField -> {
            //Map missing relatedTo CustomizableField back to CustomizableFieldDTOs
            CustomizableField relatedCustomizableField = this.getCustomizableFieldFromFieldPath(customizableField.relatesTo(), entityInstance);
            return new CustomizableFieldDTO(relatedCustomizableField);
        }).collect(Collectors.toSet());
    }

    /**
     * Gets the Value of the Identifier field of a @CustomizableEntity
     *
     * @param instance the @CustomizableEntity instance
     * @return the IdentifierField value
     */
    public Object getCustomizableEntityIdentifierField(Object instance) {
        CustomizableEntity customizableEntity = this.getCustomizableEntityAnnotation(instance);
        try {
            Field pkField = this.getReflectiveUtil().findField(instance.getClass(), customizableEntity.identifierField());
            return this.getReflectiveUtil().getField(pkField, instance);
        } catch (Exception e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_IDENTIFIER_FIELD_NOT_FOUND).setCause(e)
                    .addDescription("Identifier Field " + customizableEntity.identifierField() + " of custom entity " + instance.getClass().getName() + " defined as a customizable entity could not be found");
        }
    }

    /**
     * get the String value of an identifier field of a @CustomizableEntity, sanitized without PlaceHolders
     *
     * @param instance The @CustomizableEntity instance
     * @return the sanitized value of the identifier field
     */
    public String getSanitizedStringCustomizableEntityIdentifierField(Object instance) {
        String stringValue = String.valueOf(this.getCustomizableEntityIdentifierField(instance));
        return stringValue.replaceAll("[^A-Za-z0-9-]+", "");
    }

    /**
     * get the String value of an identifier field of a @CustomizableEntityDTO, sanitized without PlaceHolders
     *
     * @param instance The @CustomizableEntity instance
     * @return the sanitized value of the identifier field
     */
    public String getSanitizedStringCustomizableIdentifiedIdentifierField(Object instance) {
        String stringValue = String.valueOf(this.getCustomizableIdentifiedIdentifierField(instance));
        return stringValue.replaceAll("[^A-Za-z0-9-]+", "");
    }

    /**
     * Gets the CustomizableEntity annotation from an instance
     *
     * @param instance the @CustomizableEntity instance
     * @return the CustomizableEntity annotation
     */
    public CustomizableEntity getCustomizableEntityAnnotation(Object instance) {
        if (!instance.getClass().isAnnotationPresent(CustomizableEntity.class)) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_NO_CUSTOMIZABLE_ENTITY, instance.getClass().getName());
        } else {
            return instance.getClass().getAnnotation(CustomizableEntity.class);
        }
    }

    public Object getCustomizableIdentifiedIdentifierField(Object instance) {
        CustomizableEntityDTO customizableEntity = this.getCustomizableIdentifiedAnnotation(instance);
        try {
            Field pkField = this.getReflectiveUtil().findField(instance.getClass(), customizableEntity.identifierField());
            return this.getReflectiveUtil().getField(pkField, instance);
        } catch (Exception e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_IDENTIFIER_FIELD_NOT_FOUND).setCause(e)
                    .addDescription("Identifier Field " + customizableEntity.identifierField() + " of custom entity " + instance.getClass().getName() + " defined as a customizable entity could not be found");
        }
    }

    /**
     * Gets the CustomizableIdentified annotation from an instance
     *
     * @param instance the @CustomizableIdentified instance
     * @return the CustomizableIdentified annotation
     */
    public CustomizableEntityDTO getCustomizableIdentifiedAnnotation(Object instance) {
        if (!instance.getClass().isAnnotationPresent(CustomizableEntityDTO.class)) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_NO_CUSTOMIZABLE_ENTITY, instance.getClass().getName());
        } else {
            return instance.getClass().getAnnotation(CustomizableEntityDTO.class);
        }
    }

    /**
     * finds a Field based on the FieldPath value of the @CustomizableField annotation
     *
     * @param fieldPath the fieldpath to be searched for
     * @param instance  the instance where the field resides
     * @return the field or null if does not exist
     */
    public Field findFieldByFieldPath(String fieldPath, Object instance) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CustomizableField.class) && field.getAnnotation(CustomizableField.class).fieldPath().equals(fieldPath))
                .findFirst()
                .orElse(null);
    }


    public CustomizableField getCustomizableFieldFromFieldPath(String fieldPath, Object entityInstance) {
        Field field = this.findFieldByFieldPath(fieldPath, entityInstance);
        if (field == null)
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_FIELD_NOT_FOUND, fieldPath, entityInstance.getClass().getName());
        return field.getAnnotation(CustomizableField.class);
    }

    /**
     * Finds a field based on the relPath value of the @CustomizableRelation annotation
     *
     * @param relPath  the relpath to be searched for
     * @param instance the instance where the rield resides
     * @return the field or null if does not exist
     */
    public Field findFieldByRelPath(String relPath, Object instance) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CustomizableRelation.class) && field.getAnnotation(CustomizableRelation.class).relPath().equals(relPath))
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates the Label for a CustomizableInstanceRelation, based on a Parent entity, the particular object of the @CustomizableRelation collection field, and the dmPath
     *
     * @param entity           the Entity where the @CustomizableRelation annotated field resides
     * @param relationInstance the Object inside the Collection annotated with @CustomizableRelation
     * @param dmPath           the dmPath corresponding to the relationInstance object
     * @return the generate label for form/xls
     */
    public String generateCustomizableInstanceRelationLabel(Object entity, Object relationInstance, String dmPath) {
        StringBuilder label = new StringBuilder();
        if (Identifiable.class.isAssignableFrom(entity.getClass())) {
            label.append(((Identifiable) entity).getIdentifiableName());
        }

        if (dmPath.contains(CustomizableEntity.dmPathIdBracketOpen)) {
            if (!label.toString().isEmpty()) {
                label.append(EDCIConstants.StringPool.STRING_SPACE)
                        .append(XLS.Recipients.LABEL_SEPARATOR)
                        .append(XLS.Recipients.LABEL_SEPARATOR)
                        .append(EDCIConstants.StringPool.STRING_SPACE);
            }
            if (Identifiable.class.isAssignableFrom(relationInstance.getClass())) {
                label.append(((Identifiable) relationInstance).getIdentifiableName());
            } else if (relationInstance.getClass().isAnnotationPresent(CustomizableEntity.class)) {
                label.append(this.getSanitizedStringCustomizableEntityIdentifierField(relationInstance));
            }
        }
        return label.toString();
    }

    /**
     * Generates the label for a CustomizableInstanceField, based on Parent entity, the labelKey of the field, and the generated dmPath
     *
     * @param entity   the instance of the @CustomizableEntity where the customizableInstanceField resides
     * @param labelKey the labelKey from the @CustomizableField annotation
     * @param dmPath   the dmPath generated for the current CustomizableInstanceField
     * @return the Generated Label for form/xls
     */
    public String generateCustomizableInstanceFieldLabel(Object entity, String labelKey, String dmPath) {
        StringBuilder label = new StringBuilder();
        if (Identifiable.class.isAssignableFrom(entity.getClass())) {
            label.append(((Identifiable) entity).getIdentifiableName());
        }
        if (!label.toString().isEmpty()) {
            label.append(EDCIConstants.StringPool.STRING_SPACE)
                    .append(XLS.Recipients.LABEL_SEPARATOR)
                    .append(EDCIConstants.StringPool.STRING_SPACE);
        }
        label.append(this.getEdciMessageService().getMessage(labelKey));

        boolean isInnerIdPresent = dmPath.chars().filter(ch -> ch == CustomizableEntity.dmPathIdBracketOpen.charAt(0)).count() > 2;
        if (dmPath.contains(CustomizableEntity.dmPathIdBracketOpen) && isInnerIdPresent) {
            String idString = dmPath.substring(dmPath.lastIndexOf(CustomizableEntity.dmPathIdBracketOpen), dmPath.lastIndexOf(CustomizableEntity.dmPathIdBracketClose) + 1);
            label.append(EDCIConstants.StringPool.STRING_SPACE);
            label.append(idString);
        }

        if (dmPath.contains(CustomizableEntity.dmPathLangBracketOpen)) {
            String langString = dmPath.substring(dmPath.lastIndexOf(CustomizableEntity.dmPathLangBracketOpen), dmPath.lastIndexOf(CustomizableEntity.dmPathLangBracketClose) + 1);
            label.append(EDCIConstants.StringPool.STRING_SPACE);
            label.append(langString);
        }
        return label.toString();
    }

    /**
     * Generate a base DMPath with credential ID and entityID
     *
     * @param entity         the entity instance
     * @param credPK         the credential PK
     * @param originalDMPath the original DM Path
     * @return the base instance dmPath
     */
    public String generateBaseInstanceDmPath(Object entity, String credPK, String originalDMPath) {
        String baseInstancePath = this.generateBaseDmPath(credPK).concat(originalDMPath);
        //replace EntityCode placeHolder with entity PK
        if (baseInstancePath.contains(CustomizableEntity.entityIDPlaceHolder)) {
            String entityPK = this.getSanitizedStringCustomizableEntityIdentifierField(entity);
            baseInstancePath = baseInstancePath.replace(CustomizableEntity.entityIDPlaceHolder, entityPK);
        }
        return baseInstancePath;
    }

    /**
     * Generate the base DMPath with the credential ID
     *
     * @param credPk the credential ID
     * @return the base DMPath
     */
    private String generateBaseDmPath(String credPk) {
        return new StringBuilder()
                .append(String.format("%s#%s%s", CustomizableEntity.dmPathIdBracketOpen, credPk, CustomizableEntity.dmPathIdBracketClose))
                .append(CustomizableEntity.dmPathSeparator)
                .toString();
    }

    /**
     * Generates dmPaths for fields with ID placeholder
     *
     * @param parsedDmPath base path
     * @param fieldObject  field instance object
     * @return a list of dmPaths
     */
    private List<String> generateIdFieldDMPaths(String parsedDmPath, Object fieldObject) {
        List<String> dmPaths = new ArrayList<>();
        if (this.getReflectiveUtil().isCollectionInstance(fieldObject)) {
            Collection<Object> collectionField = (Collection<Object>) fieldObject;
            for (Object instanceItem : collectionField) {
                String pkString = this.getSanitizedStringCustomizableEntityIdentifierField(instanceItem);
                //Additional note case
                if (parsedDmPath.contains(CustomizableEntity.idPlaceHolder)) {
                    dmPaths.add(parsedDmPath.replace(CustomizableEntity.idPlaceHolder, pkString));
                    //Citizenship country case
                } else {
                    dmPaths.add(parsedDmPath);
                }
            }
        } else {
            String pkString = this.getSanitizedStringCustomizableEntityIdentifierField(fieldObject);
            dmPaths.add(parsedDmPath.replace(CustomizableEntity.idPlaceHolder, String.valueOf(pkString)));
        }
        return dmPaths;
    }

    /**
     * Generate dmPaths for fields with Lang placeholder
     *
     * @param parsedDmPath      base path
     * @param entity            field instance object
     * @param originalFieldPath the original field path
     * @return a list of dmPaths
     */
    private List<String> generateLangFieldDMPaths(String parsedDmPath, Object entity, String originalFieldPath) {
        List<String> dmPaths = new ArrayList<>();
        if (IMultilangDAO.class.isAssignableFrom(entity.getClass())) {
            IMultilangDAO multilangInstance = (IMultilangDAO) entity;
            for (String lang : multilangInstance.getLanguages()) {
                dmPaths.add(parsedDmPath.replace(CustomizableEntity.langPlaceHolder, lang));
            }
        } else {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_BAD_MULTILINGUAL_FORMAT)
                    .addDescription("FieldPath " + originalFieldPath + " inside class " + entity.getClass().getName() + " is not a multilingual field, fieldPath structure suggests otherwise");
        }
        return dmPaths;
    }

    /**
     * Generates the DMPaths based on a field's original path, on original path from an entity may result in more than one path in an instance, due to multilingual and collection fields
     *
     * @param entity            the Entity instance where the field resides (@CustomizableEntity annotated instance)
     * @param originalFieldPath the original field Path of the field to be generated
     * @param fieldObject       the instance of the field to generate dmPaths for (also accepts Collections)
     * @return the DMPaths for that field and instances
     */
    public List<String> generateDMPathsForField(Object entity, String credPk, String originalFieldPath, Object fieldObject) {
        List<String> dmPaths = new ArrayList<>();
        String parsedDmPath = this.generateBaseInstanceDmPath(entity, credPk, originalFieldPath);

        //Field with and $id placeholder and $lang placholder
        if (parsedDmPath.contains(CustomizableEntity.idPlaceHolder) && parsedDmPath.contains(CustomizableEntity.langPlaceHolder)) {
            dmPaths.addAll(this.generateIdFieldDMPaths(parsedDmPath, fieldObject)
                    .stream()
                    .map(dmPath -> this.generateLangFieldDMPaths(dmPath, entity, originalFieldPath))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );
            //Field with an $id placeholder
        } else if (parsedDmPath.contains(CustomizableEntity.idPlaceHolder)) {
            dmPaths.addAll(this.generateIdFieldDMPaths(parsedDmPath, fieldObject));
            //Field with a $language placeholder
        } else if (parsedDmPath.contains(CustomizableEntity.langPlaceHolder)) {
            dmPaths.addAll(this.generateLangFieldDMPaths(parsedDmPath, entity, originalFieldPath));
            //Simple field case
        } else {
            //If Collection, add N times , if not, only 1 time
            if (Collection.class.isAssignableFrom(fieldObject.getClass())) {
                Collection collectionObject = (Collection) fieldObject;
                for (int i = 0; i < collectionObject.size(); i++) {
                    dmPaths.add(parsedDmPath);
                }
            } else {
                dmPaths.add(parsedDmPath);
            }
        }

        return dmPaths;
    }

    /**
     * Get The additional info for a @CustomizableField annotated field, based also on @CustomizableCLField
     *
     * @param field the field annotated with @CustomizableField
     * @return the additional information
     */
    public List<String> getCustomizableInstanceFieldAdditionalInfo(Field field) {
        List<String> additionalInfo = new ArrayList<>();
        CustomizableField customizableField = field.getAnnotation(CustomizableField.class);
        if (customizableField == null) {
            throw new EDCIException().addDescription(String.format("field %s is not annotated with @CustomizableField", field.getName()));
        }
        if (field.isAnnotationPresent(CustomizableCLField.class)) {
            CustomizableCLField customizableCLField = field.getAnnotation(CustomizableCLField.class);
            additionalInfo.add(this.getEdciMessageService().getMessage(customizableCLField.descriptionLabelKey()));
        } else {
            additionalInfo = Arrays.asList(customizableField.additionalInfo());
        }
        return additionalInfo.stream().map(additionalInfoItem -> this.getEdciMessageService().getMessage(additionalInfoItem)).collect(Collectors.toList());
    }

    /**
     * Get the frameworkURI based on @CustomizableCLField
     *
     * @param field the field annotated with @CustomizableField and @CustomizableCLField
     * @return the additional information
     */
    public String getCustomizableInstanceFieldControlledList(Field field) {
        String controlledList = null;
        CustomizableField customizableField = field.getAnnotation(CustomizableField.class);
        if (customizableField == null) {
            throw new EDCIException().addDescription(String.format("field %s is not annotated with @CustomizableField", field.getName()));
        }
        if (field.isAnnotationPresent(CustomizableCLField.class)) {
            CustomizableCLField customizableCLField = field.getAnnotation(CustomizableCLField.class);
            controlledList = customizableCLField.targetFramework().getName();
        }
        return controlledList;
    }

    /**
     * Checks if an instance of a @CustomizableField should be instanced or not
     *
     * @param fieldObject              the instance of the @CustomizableField
     * @param shouldInstanceMethodName the name of the method to be used
     * @param entityInstance           the entity instance annotated with @CustomizabledEntity where the @CustomizableField resides
     * @return true if it should be instanced
     */
    public boolean shouldInstanceFieldObject(@Nullable Object fieldObject, String shouldInstanceMethodName, Object entityInstance) {
        boolean shouldInstance = false;
        //If the Object is null, check if it has to be created
        if (fieldObject == null && !shouldInstanceMethodName.isEmpty()) {
            //If the shouldInstanceMethodName is true, instance
            if (shouldInstanceMethodName.equals(EDCIConstants.StringPool.STRING_TRUE)) {
                shouldInstance = true;
            } else {
                //if shouldInstanceMethodName method is found and returns true, instance
                try {
                    Method method = entityInstance.getClass().getMethod(shouldInstanceMethodName);
                    shouldInstance = (Boolean) method.invoke(entityInstance);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_SHOULD_INSTANCE_METHOD_NOT_FOUND).setCause(e)
                            .addDescription("Could not get shouldInstance method " + shouldInstanceMethodName + " of class " + entityInstance.getClass().getName());
                }
            }
        }
        return shouldInstance;
    }

    /**
     * Finds Customizable Instances of a class inside a credential, treats special cases like Recipient and root instance
     *
     * @param clazz                     The class to search instances of
     * @param europassCredentialSpecDAO the credential where the instances reside
     * @return a list with the instances of that class for the credential
     */
    public Set<Object> findCustomizableInstances(Class clazz, EuropassCredentialSpecDAO europassCredentialSpecDAO) {
        Set<Object> customizableInstances = this.getReflectiveUtil().getUniqueInnerObjectsOfType(clazz, europassCredentialSpecDAO);

        if (customizableInstances.isEmpty()) {
            //Recipient Entity Case
            if (clazz.equals(CustomizableEntity.recipientClass)) {
                try {
                    customizableInstances = Collections.singleton(clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new EDCIException().addDescription(String.format("Could not instance Recipient class %s", clazz.getName()));
                }
                //Root Object (Credential) case
            } else if (clazz.equals(europassCredentialSpecDAO.getClass())) {
                customizableInstances = Collections.singleton(europassCredentialSpecDAO);
            } else {
                logger.debug(String.format("No entities of specClass %s where found in credential with oid %s", clazz.getName(), europassCredentialSpecDAO.getHashCodeSeed()));
            }
        }
        return customizableInstances;
    }

    /**
     * Creates a new instance of an object given a field
     *
     * @param field class to instantiate
     * @return new instance created by the default constructor
     * @Param collectionSize If the fieldType is a collection, we set the number of elements
     */
    public Object newInstance(Field field, int collectionSize) {
        Object instance = null;
        try {
            if (LocalDate.class.equals(field.getType())) {
                instance = LocalDate.now();
            } else {
                instance = field.getType().newInstance();
                if (instance instanceof Collection) {
                    ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                    Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                    for (int i = 0; i < collectionSize; i++) {
                        Object collectionObj = stringListClass.newInstance();
                        ((Collection) instance).add(collectionObj);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EDCIException(e).addDescription(String.format("Could not instance field %s of type %s", field.getName(), field.getType()));
        }
        return instance;
    }

    /**
     * Get the description for a XLS Header of a customizableInstanceFieldDTO entry
     *
     * @param customizableInstanceFieldDTO the customizableInstanceFieldDTO
     * @return the description
     */
    public String getDescriptionForXLSFieldHeader(CustomizableInstanceFieldDTO customizableInstanceFieldDTO) {
        if (this.getValidator().notEmpty(customizableInstanceFieldDTO.getAdditionalInfo())) {
            return String.join(",", customizableInstanceFieldDTO.getAdditionalInfo());
        } else {
            return customizableInstanceFieldDTO.getMandatory() ?
                    this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.Customization.DESCRIPTION_MANDATORY)
                    : this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.Customization.DESCRIPTION_OPTIONAL);
        }
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
