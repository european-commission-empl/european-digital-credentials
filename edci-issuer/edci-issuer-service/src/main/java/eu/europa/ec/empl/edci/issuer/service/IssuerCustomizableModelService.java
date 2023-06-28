package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableRelation;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.issuer.common.model.customization.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.IssuerCustomizableModelUtil;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Used to perform operations regarding the customization of the datamodel
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IssuerCustomizableModelService {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private IssuerCustomizableModelUtil issuerCustomizableModelUtil;

    @Autowired
    private EuropassCredentialSpecService europassCredentialSpecService;

    @Autowired
    private EDCIWorkBookReader edciWorkBookReader;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private Validator validator;


    private static final Logger logger = LogManager.getLogger(IssuerCustomizableModelService.class);

    /**
     * Gets the CustomizableSpec of the application, with the description of which entities can be customized, and a list of the fields and relations available.
     *
     * @return the CustomizableSpecDTO representing scanned annotations.
     */
    public CustomizableSpecDTO getFullCustomizableSpecList() {
        Set<CustomizableEntityDTO> customizableEntityDTOS;
        final String basePackage = "eu.europa.ec.empl.edci.issuer";
        try {
            //Get all Classes with @CustomizableEntity annotation
            Set<Class> customizableEntityClasses = this.getReflectiveUtil().getAllClassesAnnotatedWith(CustomizableEntity.class, basePackage);
            //Filter out @CustomizableEntities with no position (ie: NOTEDAO), generate CustomizableEntityDTOs for the rest
            customizableEntityDTOS = customizableEntityClasses.stream().filter(clazz -> {
                CustomizableEntity customizableEntity = (CustomizableEntity) clazz.getAnnotation(CustomizableEntity.class);
                return customizableEntity.position() != -1;
            }).map(this::generateCustomizableEntity).collect(Collectors.toSet());
        } catch (ClassNotFoundException e) {
            String errorMessage = String.format("Could not find customizable class: %s", e.getMessage());
            logger.error(errorMessage, e);
            throw new EDCIException().addDescription(errorMessage);
        }
        if (customizableEntityDTOS.isEmpty()) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_ENTITIES_NOT_FOUND).addDescription("Could not find any @CustomizableEntity after scanning dataModel with basePackage: " + basePackage);
        } else {
            //Add all customizableEntities to Spec
            CustomizableSpecDTO customizableSpecDTO = new CustomizableSpecDTO();
            customizableSpecDTO.setCustomizableEntityDTOS(customizableEntityDTOS);
            return customizableSpecDTO;
        }
    }

    /**
     * Generate the CustomizableInstanceSpec based on a selection of the CustomizableSpecDTO and an oid of a credential residing in the OCB.
     *
     * @param customizableSpecDTO The selection of fields to be customized
     * @param oid                 the ID of the credential in the OCB
     * @return the CustomizableInstanceSpec for XLS and form generation
     */
    public CustomizableInstanceSpecDTO getCustomizableInstanceSpec(CustomizableSpecDTO customizableSpecDTO, Long oid) {
        if (!this.getEuropassCredentialSpecService().exists(oid)) {
            throw new EDCINotFoundException(ErrorCode.CUSTOMIZABLE_CREDENTIAL_NOT_FOUND, String.valueOf(oid));
        }
        CustomizableInstanceSpecDTO customizableInstanceSpecDTO = new CustomizableInstanceSpecDTO();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = this.getEuropassCredentialSpecService().find(oid);

        for (CustomizableEntityDTO customizableEntityDTO : customizableSpecDTO.getCustomizableEntityDTOS()) {
            customizableInstanceSpecDTO.getCustomizableInstanceDTOS().addAll(this.getCustomizableInstanceDTOS(customizableEntityDTO, europassCredentialSpecDAO));
        }
        return customizableInstanceSpecDTO;
    }


    /**
     * Generates a customizable recipients XLS Template Based on a CustomizableSpecDTO selection, and a credential OOID
     *
     * @param customizableSpecDTO The selection of the customizableSpec
     * @param oid                 The ID of the credential in the OCB
     * @return the bytes of the credential
     */
    public byte[] getCustomizableXLSTemplate(CustomizableSpecDTO customizableSpecDTO, Long oid) {
        CustomizableInstanceSpecDTO customizableInstanceSpecDTO = this.getCustomizableInstanceSpec(customizableSpecDTO, oid);
        //Sort CustomizableInstanceDTO Entities by position
        List<CustomizableInstanceDTO> customizableInstanceDTOS = customizableInstanceSpecDTO.getCustomizableInstanceDTOS()
                .stream()
                .sorted(Comparator.comparing(CustomizableInstanceDTO::getPosition, Comparator.naturalOrder()))
                .collect(Collectors.toList());

        try (Workbook recipientWorkbook = new HSSFWorkbook()) {
            recipientWorkbook.createSheet(XLS.Recipients.DATA_SHEET_NAME);
            Sheet dataSheet = recipientWorkbook.getSheet(XLS.Recipients.DATA_SHEET_NAME);

            this.doCreateHeaderRowsAndAddStyles(dataSheet);

            int columnCount = 0;
            for (CustomizableInstanceDTO customizableInstanceDTO : customizableInstanceDTOS) {
                //Sort Customizable Relations
                List<CustomizableInstanceRelationDTO> customizableInstanceRelationDTOS = customizableInstanceDTO.getRelations().stream()
                        .sorted(Comparator.comparing(CustomizableInstanceRelationDTO::getPosition, Comparator.naturalOrder()))
                        .collect(Collectors.toList());
                //Write Customizable Relations
                for (CustomizableInstanceRelationDTO customizableInstanceRelationDTO : customizableInstanceRelationDTOS) {
                    this.writeCustomizableXLSHeaders(dataSheet
                            , customizableInstanceRelationDTO.getRelPath()
                            , customizableInstanceRelationDTO.getLabel()
                            , columnCount
                            , XLS.Recipients.FIELD_TYPE.RELATION
                            , this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.Customization.INCLUDE_ELEMENT_DESCRIPTION)
                            , null);
                    columnCount++;
                }

                //Sort Customizable Fields
                List<CustomizableInstanceFieldDTO> customizableInstanceFieldDTOS = customizableInstanceDTO.getFields().stream()
                        .sorted(Comparator.comparing(CustomizableInstanceFieldDTO::getPosition, Comparator.naturalOrder()))
                        .collect(Collectors.toList());
                //Write Customizable Fields
                for (CustomizableInstanceFieldDTO customizableInstanceFieldDTO : customizableInstanceFieldDTOS) {
                    //Get custom descriptionLabelKey
                    String description = this.getIssuerCustomizableModelUtil().getDescriptionForXLSFieldHeader(customizableInstanceFieldDTO);
                    //Check if custom styles are needed
                    CellStyle cellStyle = null;
                    if (customizableInstanceFieldDTO.getMandatory()) {
                        CellStyle baseCellStyle = dataSheet.getRow(XLS.Recipients.DESCRIPTION_ROW).getRowStyle();
                        cellStyle = recipientWorkbook.createCellStyle();
                        cellStyle.cloneStyleFrom(baseCellStyle);
                        cellStyle.setFillForegroundColor(XLS.Recipients.COLOR_DESCRIPTION_MANDATORY.getIndex());
                    }
                    this.writeCustomizableXLSHeaders(dataSheet
                            , customizableInstanceFieldDTO.getFieldPath()
                            , customizableInstanceFieldDTO.getLabel()
                            , columnCount
                            , XLS.Recipients.FIELD_TYPE.FIELD
                            , description
                            , cellStyle);
                    columnCount++;
                }
            }

            //Format Columns
            for (int i = 0; i <= columnCount; i++) {
                dataSheet.autoSizeColumn(i);
            }
            //ToDo -> control that row exists.
            //Hide references and field type rows
            dataSheet.getRow(XLS.Recipients.REFERENCES_ROW).setZeroHeight(true);
            dataSheet.getRow(XLS.Recipients.FIELD_TYPE_ROW).setZeroHeight(true);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                recipientWorkbook.write(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new EDCIException().addDescription("Could not create output stream");
            }
        } catch (IOException e) {
            throw new EDCIException().addDescription("Error creating XLS Workbook");
        }
    }

    /**
     * Create Header Rows and add styles
     *
     * @param dataSheet dataSheet where the rows will be created
     */
    private void doCreateHeaderRowsAndAddStyles(Sheet dataSheet) {
        //Styles for headers row
        Workbook recipientWorkbook = dataSheet.getWorkbook();
        Row labelsRow = dataSheet.createRow(XLS.Recipients.LABELS_ROW);
        Font labelsFont = recipientWorkbook.createFont();
        labelsFont.setFontHeightInPoints(XLS.Recipients.LABELS_ROW_FONTSIZE);
        labelsFont.setFontName("Verdana");
        labelsFont.setColor(XLS.Recipients.FONT_COLOR.getIndex());
        labelsFont.setBold(true);
        labelsFont.setItalic(false);
        CellStyle labelsStyle = recipientWorkbook.createCellStyle();
        labelsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        labelsStyle.setFont(labelsFont);
        labelsStyle.setFillForegroundColor(XLS.Recipients.COLOR_LABELS_ROW.getIndex());
        labelsRow.setRowStyle(labelsStyle);
        //Styles for descriptionLabelKey row
        Row descriptionRow = dataSheet.createRow(XLS.Recipients.DESCRIPTION_ROW);
        Font descriptionFont = recipientWorkbook.createFont();
        descriptionFont.setFontHeightInPoints(XLS.Recipients.DESCRIPTION_ROW_FONTSIZE);
        descriptionFont.setFontName("Verdana");
        descriptionFont.setColor(XLS.Recipients.FONT_COLOR.getIndex());
        descriptionFont.setBold(false);
        descriptionFont.setItalic(false);
        CellStyle descriptionStyle = recipientWorkbook.createCellStyle();
        descriptionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        descriptionStyle.setFillForegroundColor(XLS.Recipients.COLOR_DESCRIPTION_ROW.getIndex());
        descriptionStyle.setFont(descriptionFont);
        descriptionRow.setRowStyle(descriptionStyle);
    }


    /**
     * Writes headers for the Customizable Recipients XLS template
     *
     * @param sheet          Data sheet to be used
     * @param referenceValue the value to be written in the references row
     * @param labelValue     the value to be written in the label row
     * @param columnCount    the column index to be used
     * @param fieldType      the fieldtype to be written
     * @param description    the description to be written, can be null
     */
    private void writeCustomizableXLSHeaders(Sheet sheet, String referenceValue, String labelValue, int columnCount, XLS.Recipients.FIELD_TYPE fieldType, @Nullable String description, @Nullable CellStyle descriptionCellStyle) {
        Cell referenceCell = this.getEdciWorkBookReader().getCellAt(sheet, XLS.Recipients.REFERENCES_ROW, columnCount);
        Cell fieldTypeCell = this.getEdciWorkBookReader().getCellAt(sheet, XLS.Recipients.FIELD_TYPE_ROW, columnCount);
        Cell labelCell = this.getEdciWorkBookReader().getCellAt(sheet, XLS.Recipients.LABELS_ROW, columnCount);
        referenceCell.setCellValue(referenceValue);
        fieldTypeCell.setCellValue(fieldType.getType());
        labelCell.setCellValue(labelValue);
        if (description != null) {
            Cell descriptionCell = this.getEdciWorkBookReader().getCellAt(sheet, XLS.Recipients.DESCRIPTION_ROW, columnCount);
            descriptionCell.setCellValue(description);
            if (descriptionCellStyle != null) {
                descriptionCell.setCellStyle(descriptionCellStyle);
            }
        }
    }

    /**
     * Generates the CustomizableInstanceDTOS from a CustomizableEntityDTO and a credential, One CustomizableEntityDTO may result in more than one CustomizableInstanceDTOS,
     * as there could be more than one instance of the same entity type inside the credential
     *
     * @param customizableEntityDTO     The CustomizableEntityDTO with the user selection
     * @param europassCredentialSpecDAO the EuropassCredentialSpecDAO
     * @return a Set of CustomizableInstanceDTOs
     */
    private Set<CustomizableInstanceDTO> getCustomizableInstanceDTOS(CustomizableEntityDTO customizableEntityDTO, EuropassCredentialSpecDAO europassCredentialSpecDAO) {
        Class entityClass;
        //Check that class is available
        try {
            entityClass = Class.forName(customizableEntityDTO.getSpecClass());
        } catch (ClassNotFoundException e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_NOT_FOUND, customizableEntityDTO.getSpecClass());
        }
        if (!entityClass.isAnnotationPresent(CustomizableEntity.class)) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_NO_CUSTOMIZABLE_ENTITY, customizableEntityDTO.getSpecClass());
        }

        //Here we order the instances by name into a LinkedHashSet
        Set<Object> customizableInstances = this.getIssuerCustomizableModelUtil().findCustomizableInstances(entityClass, europassCredentialSpecDAO)
                .stream().filter(recipient -> recipient instanceof RecipientDataDTO).collect(Collectors.toCollection(LinkedHashSet::new));

        if (customizableInstances == null || customizableInstances.isEmpty()) {
            customizableInstances = this.getIssuerCustomizableModelUtil().findCustomizableInstances(entityClass, europassCredentialSpecDAO)
                    .stream().filter(recipient -> !(recipient instanceof RecipientDataDTO))
                    .map(identifiable -> (Identifiable) identifiable)
                    .sorted(Comparator.comparing(Identifiable::getName)).collect(Collectors.toCollection(LinkedHashSet::new));
        }

        String credPK = this.getIssuerCustomizableModelUtil().getSanitizedStringCustomizableEntityIdentifierField(europassCredentialSpecDAO);

        Set<CustomizableInstanceDTO> customizableInstancesDTOs = new HashSet<>();
        int order = 1;
        for (Object instance : customizableInstances) {
            CustomizableEntity customizableEntity = this.getIssuerCustomizableModelUtil().getCustomizableEntityAnnotation(instance);
            CustomizableInstanceDTO customizableInstanceDTO = new CustomizableInstanceDTO();
            //When no identifiable Object is present, use label
            customizableInstanceDTO.setLabel(this.getEdciMessageService().getMessage(customizableEntity.labelKey()));
            if (Identifiable.class.isAssignableFrom(instance.getClass())) {
                Identifiable identifiable = (Identifiable) instance;
                customizableInstanceDTO.setLabel(identifiable.getName());
            }
            //ToDO issue? all entities have same position
            customizableInstanceDTO.setPosition(customizableEntity.position());
            //ToDO check this
            customizableInstanceDTO.setOrder(order++);
            //dmPath generation
            if (customizableEntityDTO.getFields() != null) {
                customizableInstanceDTO.setFields(this.getCustomizableInstanceFieldDTOS(credPK, customizableEntityDTO.getFields(), instance));
            }
            if (customizableEntityDTO.getRelations() != null) {
                customizableInstanceDTO.setRelations(this.generateCustomizableInstanceRelationDTOS(credPK, customizableEntityDTO.getRelations(), instance));
            }
            if (this.getValidator().notEmpty(customizableInstanceDTO.getRelations()) || this.getValidator().notEmpty(customizableInstanceDTO.getFields()))
                customizableInstancesDTOs.add(customizableInstanceDTO);
        }
        return customizableInstancesDTOs;
    }

    /**
     * Get a set of CustomizableInstanceFieldDTOS for a Credential based on a selection of CustomizableFieldDTOS and a @CustomizableEntity instance
     *
     * @param credentialPK                  the credential Primary key
     * @param originalCustomizableFieldDTOS the selection of CustomizableFieldsDTO to be used
     * @param entityInstance                the instance that contains the fields
     * @return a set of CustomizableInstanceFieldDTOS with generated dmPaths
     */
    private Set<CustomizableInstanceFieldDTO> getCustomizableInstanceFieldDTOS(String credentialPK, Set<CustomizableFieldDTO> originalCustomizableFieldDTOS, Object entityInstance) {
        Set<CustomizableInstanceFieldDTO> generatedCustomizableFieldDTOS = new HashSet<>();
        //Check for CustomizableFieldDTOs that are relatedTo, concat with the received selection
        Set<CustomizableFieldDTO> originalCompleteCustomizableFields = Stream.concat(
                originalCustomizableFieldDTOS.stream(),
                this.getIssuerCustomizableModelUtil().getMissingRelatesToCustomizableFields(originalCustomizableFieldDTOS, entityInstance).stream())
                .collect(Collectors.toSet());
        //Loop through selection of customizable fields
        for (CustomizableFieldDTO originalCustomizableFieldDTO : originalCompleteCustomizableFields) {
            Field field = this.getIssuerCustomizableModelUtil().findFieldByFieldPath(originalCustomizableFieldDTO.getFieldPath(), entityInstance);
            if (field == null)
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_FIELD_NOT_FOUND, originalCustomizableFieldDTO.getFieldPath(), entityInstance.getClass().getName());

            CustomizableField customizableField = field.getAnnotation(CustomizableField.class);
            Object fieldObject = this.getReflectiveUtil().getField(field, entityInstance);
            String shouldInstanceMethodName = customizableField.shouldInstanceMethodName();

            if (this.getIssuerCustomizableModelUtil().shouldInstanceFieldObject(fieldObject, shouldInstanceMethodName, entityInstance)) {
                fieldObject = this.getIssuerCustomizableModelUtil().newInstance(field, customizableField.size());
            }

            final List<String> additionalInfo = this.getIssuerCustomizableModelUtil().getCustomizableInstanceFieldAdditionalInfo(field);

            if (fieldObject != null) {
                Map<String, Object> dmPaths = this.getIssuerCustomizableModelUtil().generateDMPathsForField(entityInstance, credentialPK, originalCustomizableFieldDTO.getFieldPath(), fieldObject);
                //generate a new customizableFieldDTO for each dmPath
                generatedCustomizableFieldDTOS.addAll(dmPaths.entrySet().stream().map(dmPath -> {
                    CustomizableInstanceFieldDTO customizableinstanceFieldDTO = new CustomizableInstanceFieldDTO();
                    customizableinstanceFieldDTO.setAdditionalInfo(additionalInfo);
                    customizableinstanceFieldDTO.setLabel(this.getIssuerCustomizableModelUtil().generateCustomizableInstanceFieldLabel(dmPath.getValue(), customizableField.labelKey(), customizableField.dynamicMethodLabelKey(), dmPath.getKey()));
                    customizableinstanceFieldDTO.setMandatory(customizableField.mandatory());
                    customizableinstanceFieldDTO.setPosition(customizableField.position());
                    customizableinstanceFieldDTO.setFieldType(customizableField.fieldType());
                    customizableinstanceFieldDTO.setValidation(customizableField.validation());
                    customizableinstanceFieldDTO.setControlledListType(this.getIssuerCustomizableModelUtil().getCustomizableInstanceFieldControlledList(field));
                    customizableinstanceFieldDTO.setFieldPath(dmPath.getKey());
                    return customizableinstanceFieldDTO;
                }).collect(Collectors.toSet()));
            }
        }
        return generatedCustomizableFieldDTOS;
    }

    /**
     * Generate a set of CustomizableInstanceRelationDTOs from a set of CustomizableRelationDTO
     *
     * @param credPk                   the PK of the credential where CustomizableRelationDTO is
     * @param customizableRelationDTOS the CustomizableRelationDTOS to be generated
     * @param entity                   the Entity where the @CustomizableRelation annotated field resides
     * @return the set of CustomizableInstanceRelationDTOs
     */
    private Set<CustomizableInstanceRelationDTO> generateCustomizableInstanceRelationDTOS(String credPk, Set<CustomizableRelationDTO> customizableRelationDTOS, Object entity) {
        Set<CustomizableInstanceRelationDTO> customizableInstanceRelationDTOS = new HashSet();
        customizableRelationDTOS.forEach(customizableRelationDTO -> {
            customizableInstanceRelationDTOS.addAll(this.generateCustomizableInstanceRelationDTOS(credPk, customizableRelationDTO, entity));
        });
        AtomicReference<Integer> pos = new AtomicReference<>(1);
        Set<CustomizableInstanceRelationDTO> customizableInstanceSorted = customizableInstanceRelationDTOS.stream()
                .sorted(Comparator.comparing(CustomizableInstanceRelationDTO::getGroupId).thenComparing(CustomizableInstanceRelationDTO::getLabel))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        customizableInstanceSorted.forEach(relation -> relation.setOrder(pos.getAndSet(pos.get() + 1)));

        return customizableInstanceSorted;
    }

    /**
     * Generates a set of CustomizableInstanceRelationDTO from a single customizableRelationDTO
     *
     * @param credPk                  The credential PK
     * @param customizableRelationDTO the Customizable Relation DTO
     * @param entity                  The @CustomizableEntity instance where the @CustomizableRelationDTO annotated field resides
     * @return
     */
    private Set<CustomizableInstanceRelationDTO> generateCustomizableInstanceRelationDTOS(String credPk, CustomizableRelationDTO customizableRelationDTO, Object entity) {
        Field field = this.getIssuerCustomizableModelUtil().findFieldByRelPath(customizableRelationDTO.getRelPath(), customizableRelationDTO.getPosition(), entity);
        if (field == null)
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CLASS_RELATION_NOT_FOUND, customizableRelationDTO.getRelPath(), entity.getClass().getName());
        CustomizableRelation customizableRelation = field.getAnnotation(CustomizableRelation.class);
        Object relationObject = this.getReflectiveUtil().getField(field, entity);
        Set<CustomizableInstanceRelationDTO> customizableInstanceRelationDTOS = new HashSet<>();
        String basePath = this.getIssuerCustomizableModelUtil().generateBaseInstanceDmPath(entity, credPk, customizableRelationDTO.getRelPath());
        if (basePath.contains(CustomizableEntity.idPlaceHolder)) {
            if (Collection.class.isAssignableFrom(relationObject.getClass())) {
                Collection<Object> collectionField = (Collection<Object>) relationObject;
                for (Object collectionInstance : collectionField) {
                    String pkString = this.getIssuerCustomizableModelUtil().getSanitizedStringCustomizableEntityIdentifierField(collectionInstance);
                    CustomizableInstanceRelationDTO customizableInstanceRelationDTO = new CustomizableInstanceRelationDTO();
                    String dmPath = basePath.replace(CustomizableEntity.idPlaceHolder, String.valueOf(pkString));
                    customizableInstanceRelationDTO.setRelPath(dmPath);
                    customizableInstanceRelationDTO.setPosition(customizableRelation.position());
                    customizableInstanceRelationDTO.setLabel(this.getIssuerCustomizableModelUtil().generateCustomizableInstanceRelationLabel(entity, collectionInstance, dmPath));
                    customizableInstanceRelationDTO.setGroupLabel(getEdciMessageService().getMessage(customizableRelation.labelKey()));
                    customizableInstanceRelationDTO.setGroupId(customizableRelation.groupId());
                    customizableInstanceRelationDTOS.add(customizableInstanceRelationDTO);
                }
            }
        } else {
            //ToDo  Non-list, relation case is possible?
        }
        return customizableInstanceRelationDTOS;
    }

    /**
     * Generate a Customizable Entity DTO based on a Class' CustomizableEntity, CustomizableField and CustomizableRelation annotation values
     *
     * @param clazz the class to be scanned
     * @return the scanned customizable entity DTO
     * @throws EDCIException if the class does not have the @CustomizableEntity annotation
     */
    protected CustomizableEntityDTO generateCustomizableEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CustomizableEntity.class)) {
            String errorMessage = String.format("Class %s IS NOT a Customizable Entity could not generate a CustomizableEntityDTO", clazz.getName());
            logger.error(errorMessage);
            throw new EDCIException().addDescription(errorMessage);
        }
        CustomizableEntity customizableAnnotation = clazz.getAnnotation(CustomizableEntity.class);
        CustomizableEntityDTO customizableEntityDTO = new CustomizableEntityDTO(customizableAnnotation);

        Set<Field> customizableFields = this.getReflectiveUtil().getAllFieldsAnnotatedWith(clazz, CustomizableField.class);
        customizableEntityDTO.setFields(customizableFields.stream().map(this::generateCustomizableField).collect(Collectors.toSet()));
        Set<Field> customizableRelations = this.getReflectiveUtil().getAllFieldsAnnotatedWith(clazz, CustomizableRelation.class);
        customizableEntityDTO.setRelations(customizableRelations.stream().map(this::generateCustomizableRelation).collect(Collectors.toSet()));

        return customizableEntityDTO;
    }

    /**
     * Generate a Customizable Field DTO based on a Field's CustomizableField annotation values
     *
     * @param field the field to be scanned
     * @return the scanned Customizable Field DTO
     * @throws EDCIException if the field does not have the @CustomizableField annotation
     */
    protected CustomizableFieldDTO generateCustomizableField(Field field) {
        if (!field.isAnnotationPresent(CustomizableField.class)) {
            String errorMessage = String.format("Field %s IS NOT a customizable field, could not generate a CustomizableFieldDTO", field.getName());
            logger.error(errorMessage);
            throw new EDCIException().addDescription(errorMessage);
        }
        CustomizableField customizableAnnotation = field.getAnnotation(CustomizableField.class);
        return new CustomizableFieldDTO(customizableAnnotation);
    }

    /**
     * Generate a Customizable Relation DTO based on a Field's CustomizableRelation annotation values
     *
     * @param field the field to be scanned
     * @return the scanned Customizable Relation DTO
     * @throws EDCIException if the field does not have the @CustomizableRelation annotation
     */
    protected CustomizableRelationDTO generateCustomizableRelation(Field field) {
        if (!field.isAnnotationPresent(CustomizableRelation.class)) {
            String errorMessage = String.format("Field %s IS NOT a customizable relation, could not generate a CustomizableEntityDTO", field.getName());
            logger.error(errorMessage);
            throw new EDCIException().addDescription(errorMessage);
        }
        CustomizableRelation customizableAnnotation = field.getAnnotation(CustomizableRelation.class);
        return new CustomizableRelationDTO(customizableAnnotation);
    }


    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public EuropassCredentialSpecService getEuropassCredentialSpecService() {
        return europassCredentialSpecService;
    }

    public void setEuropassCredentialSpecService(EuropassCredentialSpecService europassCredentialSpecService) {
        this.europassCredentialSpecService = europassCredentialSpecService;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public EDCIWorkBookReader getEdciWorkBookReader() {
        return edciWorkBookReader;
    }

    public void setEdciWorkBookReader(EDCIWorkBookReader edciWorkBookReader) {
        this.edciWorkBookReader = edciWorkBookReader;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public IssuerCustomizableModelUtil getIssuerCustomizableModelUtil() {
        return issuerCustomizableModelUtil;
    }

    public void setIssuerCustomizableModelUtil(IssuerCustomizableModelUtil issuerCustomizableModelUtil) {
        this.issuerCustomizableModelUtil = issuerCustomizableModelUtil;
    }


}
