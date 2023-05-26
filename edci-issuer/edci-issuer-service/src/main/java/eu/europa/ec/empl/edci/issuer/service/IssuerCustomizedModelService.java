package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.ITranslatable;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.ReflectiveException;
import eu.europa.ec.empl.edci.issuer.common.constants.Customization;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.DeliveryDetailsDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.issuer.common.model.customization.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.service.beans.RelatedEntity;
import eu.europa.ec.empl.edci.issuer.service.beans.RelatedMethod;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.IssuerCustomizableModelUtil;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.Period;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IssuerCustomizedModelService {

    @Autowired
    private EDCIWorkBookReader edciWorkBookReader;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private StringDateMapping stringDateMapping;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private IssuerCustomizableModelUtil issuerCustomizableModelUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private DiplomaService diplomaService;

    //Matches {#123}
    //Groups: $1 - credential Id
    private Pattern patternCredId = Pattern.compile("^\\{\\#([\\w-]+)\\}$");
    //Matches ASM{123}
    //Groups: $1 - entity name, $2 - entity Id
    private Pattern patternEntity = Pattern.compile("^(\\w{3})(\\{([\\w-]+)\\})?$");
    //Matches familyName or additionalNote{Topic01}(en) or description(en) or address(0)
    //Groups: $1 - field name, $2 - field Id (if any), $5 - field pos (if any), $7 - field lang (if any)
    private Pattern patternField = Pattern.compile("^(\\w+)(\\{([\\w-]+)\\})?(\\[(\\d{1,3})\\])?(\\((\\w{2})\\))?$");
    //Matches wasInfluencedBy{1875}
    //Groups: $1 - field name, $3 - field Id
    private Pattern patternRelation = Pattern.compile("^(\\w+)(\\{([\\w-]+)\\})$");

    private static SimpleDateFormat formatterFull = new SimpleDateFormat(EDCIConstants.DATE_ISO_8601); //ISO_8601
    private static SimpleDateFormat formatterLocal = new SimpleDateFormat(EDCIConstants.DATE_LOCAL); //ISO_8601

    private UrlValidator urlValidator = new UrlValidator(new String[]{"http"});


    /**
     * Parses a XLS recipients file into a CustomizedRecipientsDTO object with a list of entities and relations.
     *
     * @param file XLS recipients template file filled with values for one or more recipients.
     * @return the CustomizedRecipientsDTO containing the information from the file
     */
    public CustomizedRecipientsDTO getRecipientsFromXLS(MultipartFile file) { //TODO: unit test

        CustomizedRecipientsDTO recipients = new CustomizedRecipientsDTO();

        try (Workbook recipientWorkbook = new HSSFWorkbook(file.getInputStream())) {
            //TODO: validations + format check and parse error reports
            Sheet dataSheet = recipientWorkbook.getSheet(XLS.Recipients.DATA_SHEET_NAME);
            //Read Headers
            Map<Integer, CustomizableXLSColumnInfoDTO> columnInfoDTOS = this.getColumnInfoFromXLSHeaders(dataSheet);

            //Loop Through rows
            for (Row row : dataSheet) {
                if (row.getRowNum() > XLS.Recipients.LAST_HEADER_ROW) {
                    //Read any row below the LAST_HEADER_ROW
                    CustomizedRecipientDTO customizedRecipientDTO = new CustomizedRecipientDTO();
                    //We don't use the row iterator because it only returns the cell with values, this may leave "relations" without remal in further processes
                    for (int i = 0; i < dataSheet.getRow(0).getLastCellNum(); i++) {
                        Cell valueCell = row.getCell(i);
                        //Get Column Info based on column index
                        int columnIndex = i;
                        CustomizableXLSColumnInfoDTO columnInfoDTO = columnInfoDTOS.get(columnIndex);
                        //Get Cell value, create CustomizedFieldDTO or CustomizedRelationDTO depending on fieldType
                        String cellValue = this.getEdciWorkBookReader().getStringCellValue(valueCell);
                        switch (columnInfoDTO.getFieldType()) {
                            case FIELD:
                                if (cellValue != null) {
                                    customizedRecipientDTO.getFields().add(new CustomizedFieldDTO(columnInfoDTO.getReference(), cellValue));
                                }
                                break;
                            case RELATION:
                                boolean include = cellValue != null ? cellValue.equalsIgnoreCase(XLS.Recipients.INCLUDE_CHAR_Y) : false;
                                customizedRecipientDTO.getRelations().add(new CustomizedRelationDTO(include, columnInfoDTO.getReference()));
                                break;
                        }
                    }
                    recipients.getRecipients().add(customizedRecipientDTO);
                }
            }
        } catch (IOException e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_RECIPIENT_XLS_NOT_READABLE);
        }

        return recipients;

    }

    private Map<Integer, CustomizableXLSColumnInfoDTO> getColumnInfoFromXLSHeaders(Sheet dataSheet) {
        Map<Integer, CustomizableXLSColumnInfoDTO> customizableXLSColumnInfoDTOS = new HashMap<Integer, CustomizableXLSColumnInfoDTO>();
        Row referencesRow = dataSheet.getRow(XLS.Recipients.REFERENCES_ROW);
        for (Cell referenceCell : referencesRow) {
            int columnIndex = referenceCell.getColumnIndex();
            String reference = this.getEdciWorkBookReader().getStringCellValue(referenceCell);
            Cell fieldTypeCell = this.getEdciWorkBookReader().getCellAt(dataSheet, XLS.Recipients.FIELD_TYPE_ROW, columnIndex);
            String fieldTypeStr = this.getEdciWorkBookReader().getStringCellValue(fieldTypeCell);
            XLS.Recipients.FIELD_TYPE fieldType = XLS.Recipients.FIELD_TYPE.forName(fieldTypeStr);
            if (fieldType == null) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_TYPE_HEADER_NOT_FOUND,
                        fieldTypeStr,
                        this.getEdciWorkBookReader().getColumnStringValue(columnIndex),
                        String.valueOf(columnIndex));
            }
            customizableXLSColumnInfoDTOS.put(columnIndex, new CustomizableXLSColumnInfoDTO(columnIndex, reference, fieldType));
        }
        return customizableXLSColumnInfoDTOS;
    }

    /**
     * Null safe method to retrieve information given a pattern matcher
     *
     * @param r supplier used to execute the matcher.group() call
     * @return a String with the matched group
     */
    public static String retrieveFromMatcherNullSafe(Supplier<String> r) {
        String returnValue = null;
        try {
            returnValue = r.get();
        } catch (Exception e) {
        }
        return returnValue;
    }

    /**
     * Obtains the credential Id from the first recipent's columns. All of the credential ids from the Recipient information have to match
     *
     * @param recipients a list of recipients and their information associated
     * @return a Credential Id, currently represented by it's PK
     */
    public String getCredentialIdFromRecipients(CustomizedRecipientsDTO recipients) {

        Iterator<CustomizedRecipientDTO> recipientsIterator = recipients.getRecipients().iterator();

        String fieldPath = null;
        String credentialId = null;

        if (recipientsIterator.hasNext()) {
            CustomizedRecipientDTO recipient = recipientsIterator.next();
            if (!recipient.getFields().isEmpty()) {
                fieldPath = recipient.getFields().iterator().next().getFieldPathIdentifier();
            } else {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RECIPIENT_NO_FIELDS);
            }
        }

        /* ******************** Credential id check **************/
        //We obtain the credential id from the first field. All the others should have the same credential
        Matcher matcherId = patternCredId.matcher(fieldPath.split("\\.")[0]);
        if (matcherId.find()) {
            credentialId = matcherId.group(1);
        } else {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_INVALID_CREDENTIAL_ID_FORMAT, fieldPath);
        }

        return credentialId;

    }

    public String searchCustomizableRecipientField(CustomizedRecipientDTO customizableRecipientDTO, String field) {
        CustomizedFieldDTO customizedFieldDTO = customizableRecipientDTO.getFields()
                .stream().filter(f -> f.getFieldPathIdentifier().endsWith(field)).findFirst().orElse(null);
        return customizedFieldDTO != null ? customizedFieldDTO.getValue() : null;
    }


    public String getFullNameFromCustomizedRecipient(CustomizedRecipientDTO customizedRecipientDTO) {
        return this.searchCustomizableRecipientField(customizedRecipientDTO, "fullName");
    }

    /**
     * The information of a recipient stored into the CustomizedRecipientDTO is used to fill and overwrite the one into EuropassCredentialDTO in this method
     *
     * @param uploadCred   Credential specs retrieved from OCB already parsed into a DTO
     * @param recipìent    Infrmation of a student entered by dynamic form or XLS
     * @param credentialId Credential entity Id
     */
    public void replaceCustomFields(EuropeanDigitalCredentialUploadDTO uploadCred, CustomizedRecipientDTO recipìent, String credentialId) {

        Set<String> cleanedLists = new HashSet<>();
        String fullName = this.getFullNameFromCustomizedRecipient(recipìent);

        for (CustomizedFieldDTO field : recipìent.getFields()) {

            //Obtain the path to the field, splitted
            LinkedList<String> pathList = Arrays.stream(field.getFieldPathIdentifier().split("\\.")).collect(Collectors.toCollection(LinkedList::new));

            /* ******************** Credential id check **************/
            String idSegment = pathList.removeFirst();
            Matcher matcherId = patternCredId.matcher(idSegment);
            if (matcherId.find()) {
                String id = matcherId.group(1);
                //The credential entity ID from the recipient's info is the same as the one from the DAO?
                if (!StringUtils.equals(credentialId, id)) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_CREDENTIAL_ID_MISMATCH,
                            fullName, field.getFieldPathIdentifier(), credentialId, id);
                }
            } else {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_INVALID_CREDENTIAL_ID_FORMAT, fullName, idSegment);
            }

            /* ******************** Entity Id retrival **************/
            String entityName = null;
            String entityId = null;
            String entitySegment = pathList.removeFirst();
            Matcher matcherEntity = patternEntity.matcher(entitySegment);
            if (matcherEntity.find()) {
                entityName = matcherEntity.group(1);
                entityId = retrieveFromMatcherNullSafe(() -> matcherEntity.group(3));
            } else {
                entityName = Customization.CustomizableEntities.CREDENTIAL.getCode();
                pathList.addFirst(entitySegment);
            }

            Class entityClass = Customization.CustomizableEntities.getByCode(entityName).getClazz();
            String finalEntityId = entityId;

            List<Object> foundEntity = new ArrayList<>();

            if (Customization.CustomizableEntities.PERSONAL.getCode().equals(entityName)) {
                foundEntity.add(uploadCred.getCredential().getCredentialSubject());
            } else if (Customization.CustomizableEntities.CREDENTIAL.getCode().equals(entityName)) {
                foundEntity.add(uploadCred.getCredential());
            } else {
                foundEntity = (List<Object>) getReflectiveUtil().getInnerObjectsOfType(entityClass, uploadCred.getCredential())
                        .stream().filter(entity -> finalEntityId.equals(this.getIssuerCustomizableModelUtil().getCustomizableIdentifiedIdentifierField(entity).toString())).collect(Collectors.toList());
            }

            /* ******************** Finding the field and setting value **************/
            if (foundEntity != null) {

                for (Object iterFoundEntity : foundEntity) {
                    Object nestedEntity = iterFoundEntity;
                    for (Iterator<String> i = pathList.iterator(); i.hasNext(); ) {
                        String section = i.next();

                        Matcher matcherField = patternField.matcher(section);

                        if (!matcherField.find()) {
                            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_INVALID_FIELD_FORMAT_FIELDPATH, fullName, section, field.getFieldPathIdentifier());
                        }

                        String fieldName = matcherField.group(1);
                        String fieldId = null;
                        String fieldLang = null;
                        String fieldPos = null;
                        fieldId = retrieveFromMatcherNullSafe(() -> matcherField.group(3));
                        fieldPos = retrieveFromMatcherNullSafe(() -> matcherField.group(5));
                        fieldLang = retrieveFromMatcherNullSafe(() -> matcherField.group(7));

                        //If we are not at the end of the path, we iterate and retrieve the next entity
                        if (i.hasNext()) {

                            nestedEntity = iterateOverEntites(nestedEntity, fieldName, fieldPos, fullName);

                        } else {

                            //Wallet address goes outside of the credential from 1.8 version
                            if ("walletAddress".equalsIgnoreCase(fieldName)) {
                                uploadCred.setDeliveryDetails(new DeliveryDetailsDTO(field.getValue()));
                            } else {
                                //If there are no more elements in the Path we set the value into the field
                                Object finalEntity = null;
                                try {
                                    Field fieldAux = reflectiveUtil.findField(nestedEntity.getClass(), fieldName);
                                    if (reflectiveUtil.hasParameterlessPublicConstructor(fieldAux.getType())) {
                                        finalEntity = reflectiveUtil.getOrInstanceAnyField(fieldAux, nestedEntity);
                                    } else {
                                        finalEntity = reflectiveUtil.getOrInstanceAnyField(fieldAux, nestedEntity, field.getValue());
                                    }
                                } catch (Exception e) {
                                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INSTANCE_ERROR, fullName, fieldName, nestedEntity.getClass().getSimpleName()).setCause(e);
                                }

                                if (!(finalEntity instanceof Collection)) {

                                    setFinalEntity(nestedEntity, finalEntity, fieldName, field.getValue(), fieldLang, uploadCred.getCredential(), fullName);

                                } else {

                                    setFinalCollectionEntity(nestedEntity, finalEntity, fieldName, fieldId, field.getValue(), fieldLang, uploadCred.getCredential(), cleanedLists, fullName);

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The entity relations removed or selected at the CustomizedRecipientDTO is used to mantain or remove the ones into EuropassCredentialDTO in this method
     *
     * @param credentialDTO Credential specs retrieved from OCB already parsed into a DTO
     * @param recipìent     Information of a student entered by dynamic form or XLS
     * @param credentialId  Credential entity Id
     */
    public void replaceCustomRelations(EuropeanDigitalCredentialDTO credentialDTO, CustomizedRecipientDTO recipìent, String credentialId) {

        List<RelatedEntity> cleanRelations = new ArrayList<>();

        String fullName = this.getFullNameFromCustomizedRecipient(recipìent);

        for (CustomizedRelationDTO relation : recipìent.getRelations()) {

            LinkedList<String> pathList = Arrays.stream(relation.getRelPathIdentifier().split("\\.")).collect(Collectors.toCollection(LinkedList::new));

            /* ******************** Credential id check **************/
            String idSegment = pathList.removeFirst();
            Matcher matcherId = patternCredId.matcher(idSegment);
            if (matcherId.find()) {
                String id = matcherId.group(1);
                if (!StringUtils.equals(credentialId, id)) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_CREDENTIAL_ID_MISMATCH,
                            fullName, relation.getRelPathIdentifier(), credentialId, id);
                }
            } else {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_INVALID_CREDENTIAL_ID_FORMAT, fullName, idSegment);
            }

            /* ******************** Entity Id retrival **************/
            String entityName = null;
            String entityId = null;
            String entitySegment = pathList.removeFirst();
            Matcher matcherEntity = patternEntity.matcher(entitySegment);
            if (matcherEntity.find()) {
                entityName = matcherEntity.group(1);
                entityId = retrieveFromMatcherNullSafe(() -> matcherEntity.group(3));
            } else {
                entityName = Customization.CustomizableEntities.CREDENTIAL.getCode();
                pathList.addFirst(entitySegment);
            }

            Class entityClass = Customization.CustomizableEntities.getByCode(entityName).getClazz();

            String finalEntityId = entityId;
            List<Object> foundEntity = new ArrayList<>();

            if (Customization.CustomizableEntities.PERSONAL.getCode().equals(entityName)) {
                foundEntity.add(credentialDTO.getCredentialSubject());
            } else if (Customization.CustomizableEntities.CREDENTIAL.getCode().equals(entityName)) {
                foundEntity.add(credentialDTO);
            } else {
                foundEntity = (List<Object>) getReflectiveUtil().getInnerObjectsOfType(entityClass, credentialDTO)
                        .stream().filter(entity -> finalEntityId.equals(this.getIssuerCustomizableModelUtil().getCustomizableIdentifiedIdentifierField(entity).toString())).collect(Collectors.toList());
            }

            /* ******************** Adding and removing the collection elements **************/
            String relationSegment = pathList.removeFirst();
            Matcher matcherRelation = patternRelation.matcher(relationSegment);

            if (!matcherRelation.find()) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_INVALID_FIELD_FORMAT_FIELDPATH, fullName, relationSegment, relation.getRelPathIdentifier());
            }

            String relationName = matcherRelation.group(1);
            String relationId = null;
            relationId = retrieveFromMatcherNullSafe(() -> matcherRelation.group(3));
            String finalRelationId = relationId;

            for (Object iterFoundEntity : foundEntity) {
                Method method = null;
                try {
                    method = iterFoundEntity.getClass().getMethod("get" + StringUtils.capitalize(relationName), null);
                } catch (Exception e) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RELATION_NOT_FOUND, fullName, relationName, iterFoundEntity.getClass().getName()).setCause(e);
                }

                Collection relatedCollection = null;
                try {

                    relatedCollection = (Collection) method.invoke(iterFoundEntity);

                } catch (IllegalAccessException e) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RELATION_ERROR_GET, fullName, relationName, iterFoundEntity.getClass().getName()).setCause(e);
                } catch (InvocationTargetException e) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RELATION_ERROR_GET, fullName, relationName, iterFoundEntity.getClass().getName()).setCause(e);
                }

                //We set into a map the collections and the elements that are to be modified either by substracting or adding elements
                Object relationEntity = relatedCollection.stream().filter(entity ->
                        finalRelationId.equals(this.getIssuerCustomizableModelUtil().getCustomizableIdentifiedIdentifierField(entity))).findFirst().orElse(null);

                RelatedEntity relationHolder = cleanRelations.stream().filter(entity -> entity.getEntity().equals(iterFoundEntity)).findFirst().orElse(null);

                //if (relationHolder == null) {

                RelatedMethod relatedMethod = new RelatedMethod(method);
                relatedMethod.addValues(relationEntity, relation.getIncluded());
                relationHolder = new RelatedEntity(iterFoundEntity);
                relationHolder.addMethod(relatedMethod);
                cleanRelations.add(relationHolder);
                /*} else {
                    Method finalMethod = method;
                    RelatedMethod relationMethod = relationHolder.getRelatedMethods().stream().filter(relMethod -> relMethod.getMethod().equals(finalMethod)).findFirst().orElse(null);
                    if (relationMethod != null) {
                        relationMethod.addValues(relationEntity, relation.getIncluded());
                    } else {
                        RelatedMethod relatedMethod = new RelatedMethod(method);
                        relatedMethod.addValues(relationEntity, relation.getIncluded());
                        relationHolder.addMethod(relatedMethod);
                    }
                }*/
            }
        }

        //We iterate the collections to set them with the exact elements retrieved from the form/XLS
        for (RelatedEntity relatedEntity : cleanRelations) {

            for (RelatedMethod relations : relatedEntity.getRelatedMethods()) {
                Method method = relations.getMethod();
                try {
                    ((Collection) method.invoke(relatedEntity.getEntity())).clear();
//                    System.out.println("Cleaning " + relatedEntity.getEntity() + "(" + ((Nameable) relatedEntity.getEntity()).getIdentifiableName() + ")." + method.getName() + " collection");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new EDCIException().setCause(e);
                }
                for (Map.Entry<Object, Boolean> relation : relations.getValues().entrySet()) {
                    try {
                        if (relation.getValue()) {
                            ((Collection) method.invoke(relatedEntity.getEntity())).add(relation.getKey());
//                            System.out.println("Adding " + relatedEntity.getEntity() + "(" + ((Nameable) relatedEntity.getEntity()).getIdentifiableName() + ")." + method.getName() + " - " +  relation.getKey() + "(" + ((Nameable) relation.getKey()).getIdentifiableName() + ")");
                        }
                    } catch (IllegalAccessException e) {
                        throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RELATION_ERROR_ADD, fullName, relations.getMethod().getName(), relatedEntity.getEntity().getClass().getName()).setCause(e);
                    } catch (InvocationTargetException e) {
                        throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_RELATION_ERROR_ADD, fullName, relations.getMethod().getName(), relatedEntity.getEntity().getClass().getName()).setCause(e);
                    }
                }
            }
        }
    }

    /**
     * Main method to generate a full XML DTO from some recipients information and a EuropassCredentialSpecDAO
     *
     * @param credential Credential specs retrieved from OCB
     * @param recipients Infrmation of multiple students entered by dynamic form or XLS
     * @return a credential DTO
     */
    public List<EuropeanDigitalCredentialUploadDTO> fromCustomToDTO(EuropassCredentialSpecDAO credential, CustomizedRecipientsDTO recipients) {

        List<EuropeanDigitalCredentialUploadDTO> recipientsCredentials = new ArrayList<>();

        for (CustomizedRecipientDTO recipìent : recipients.getRecipients()) {

            EuropeanDigitalCredentialDTO credentialDTO = getCredentialMapper().toDTO(credential);
            String credentialId = this.getIssuerCustomizableModelUtil().getCustomizableEntityIdentifierField(credential).toString();

            EuropeanDigitalCredentialUploadDTO uploadCred = new EuropeanDigitalCredentialUploadDTO();
            uploadCred.setCredential(credentialDTO);

            replaceCustomFields(uploadCred, recipìent, credentialId);

            replaceCustomRelations(credentialDTO, recipìent, credentialId);

            diplomaService.informDiplomaImage(credential, credentialDTO);

            recipientsCredentials.add(uploadCred);

        }

        return recipientsCredentials;
    }

    /**
     * Given an entity and a field name + extra information this method returns the object that is retrieved from this field.
     * It can be instanced if needed.
     *
     * @param nestedEntity root entity
     * @param fieldName    name of the field we are trying to access/instance
     * @param fieldPos     in case of retrieving a collection of elements, if this is informed, the element with in the position will be filtered and returned instead
     * @param fullName     the fullName of the subject, for error messages
     * @return an existing or instanced reference to the object retrieved by nestedentity and filedName
     */
    public Object iterateOverEntites(Object nestedEntity, String fieldName, String fieldPos, String fullName) {

        Object nextEntity = null;

        // Already informed object -> wasAwardedBy.date
        // Null object -> REC.nationalId.content
        try {
            nextEntity = reflectiveUtil.getOrInstanceAnyField(reflectiveUtil.findField(nestedEntity.getClass(), fieldName), nestedEntity);
        } catch (ReflectiveException | ReflectiveOperationException e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INSTANCE_ERROR, fullName, fieldName, nestedEntity.getClass().getSimpleName()).setCause(e);
        }

        // Particular Elem from a list -> location(0).name(en) && REC.placeOfBirth.hasAddress(0).countryCode
        if (fieldPos != null && (nextEntity instanceof List)) {
            int pos = Integer.parseInt(fieldPos);
            if (pos >= ((List<?>) nextEntity).size()) {
                if (pos == 0) {
                    try {
                        Field listField = null;
                        try {
                            listField = nestedEntity.getClass().getDeclaredField(fieldName);
                        } catch (java.lang.NoSuchFieldException e) {
                            listField = nestedEntity.getClass().getSuperclass().getDeclaredField(fieldName);
                        }
                        ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                        Class<?> stringListClass = (Class<?>) listType.getActualTypeArguments()[0];
                        ((List) nextEntity).add(stringListClass.newInstance());
                        nextEntity = ((List<?>) nextEntity).get(pos);

                    } catch (Exception e) {
                        throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INSTANCE_ERROR, fullName, fieldName, nestedEntity.getClass().getName(), fieldPos).setCause(e);
                    }
                } else {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_CREATE_POSITION_NOT_FOUND, fullName, fieldName, nestedEntity.getClass().getName(), fieldPos);
                }
            } else {
                nextEntity = ((List<?>) nextEntity).get(pos);
            }
        } else if (fieldPos != null) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_LIST_POSITION, fullName, fieldName, nestedEntity.getClass().getName(), fieldPos);
        }

        return nextEntity;

    }

    /**
     * TODO
     *
     * @param parentEntity
     * @param finalEntity
     * @param fieldName
     * @param finalFieldId
     * @param fieldValue
     * @param fieldLang
     * @param credentialDTO
     * @param cleanedLists
     * @param fullName
     */
    public void setFinalCollectionEntity(Object parentEntity, Object finalEntity, String fieldName, String
            finalFieldId, String fieldValue, String fieldLang, EuropeanDigitalCredentialDTO credentialDTO, Set<String> cleanedLists, String fullName) {

        String primaryLanguage = controlledListCommonsService.searchLanguageISO639ByConcept(credentialDTO.getDisplayParameter().getPrimaryLanguage());
        List<String> availableLanguages = controlledListCommonsService.searchLanguageISO639ByConcept(credentialDTO.getDisplayParameter().getLanguage());

        //Particular Elem from a list + multilang -> additionalNote{Topic02}(en)
        if (finalFieldId != null && fieldLang == null) {
            ((Collection<?>) finalEntity).removeIf(entity -> finalFieldId.equals(this.getIssuerCustomizableModelUtil().getSanitizedStringCustomizableIdentifiedIdentifierField(entity).toString()));
            setObjectField(parentEntity, fieldName, fieldValue, primaryLanguage, true, fullName);
        } else if (finalFieldId != null && fieldLang != null) {
            ITranslatable entryEntity = ((Collection<ITranslatable>) finalEntity).stream().filter(entity ->
                    finalFieldId.equals(this.getIssuerCustomizableModelUtil().getSanitizedStringCustomizableIdentifiedIdentifierField(entity).toString())).findFirst().orElse(null);
            setTranslatableField(entryEntity, fieldLang, fieldValue, primaryLanguage);
            //List to set -> citizenshipCountry
        } else if (finalFieldId == null) {

            Field collectionField = null;
            String fieldAuxId = parentEntity.getClass().getSimpleName() + fieldName;
            try {
                collectionField = parentEntity.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_FOUND, fullName, fieldName, parentEntity.getClass().getName()).setCause(e);
            }

            if (!cleanedLists.contains(fieldAuxId)) {
                cleanedLists.add(fieldAuxId);
                finalEntity = reflectiveUtil.getOrInstanceCollectionField(collectionField, parentEntity);
                ((Collection<?>) finalEntity).clear();
            }

            ParameterizedType collectionType = (ParameterizedType) collectionField.getGenericType();
            Class<?> integerListClass = (Class<?>) collectionType.getActualTypeArguments()[0];

            //Code -> Sex, country
            if (ConceptDTO.class.equals(integerListClass)) {
                setCodeField(parentEntity, fieldName, fieldValue, primaryLanguage, availableLanguages, true, fullName);
                //Any other object -> ???
            } else {
                setObjectField(parentEntity, fieldName, fieldValue, primaryLanguage, true, fullName);
            }

        }
    }

    /**
     * TODO
     *
     * @param parentEntity
     * @param finalEntity
     * @param fieldName
     * @param fieldValue
     * @param fieldLang
     * @param credentialDTO
     */
    public void setFinalEntity(Object parentEntity, Object finalEntity, String fieldName, String fieldValue, String
            fieldLang, EuropeanDigitalCredentialDTO credentialDTO, String fullName) {

        String primaryLanguage = controlledListCommonsService.searchLanguageISO639ByConcept(credentialDTO.getDisplayParameter().getPrimaryLanguage());
        List<String> availableLanguages = controlledListCommonsService.searchLanguageISO639ByConcept(credentialDTO.getDisplayParameter().getLanguage());

        // Multilang -> title(en)
        if (finalEntity instanceof ITranslatable) {
            setTranslatableField((ITranslatable) finalEntity, fieldLang, fieldValue, primaryLanguage);
            // Code -> Sex, country
        } else if (finalEntity instanceof ConceptDTO) {
            setCodeField(parentEntity, fieldName, fieldValue, primaryLanguage, availableLanguages, false, fullName);
            // Object -> familyName
            // List<Object> -> ?????
        } else {
            setObjectField(parentEntity, fieldName, fieldValue, primaryLanguage, false, fullName);
        }

    }

    public void setObjectField(Object target, String fieldName, String fieldValue, String credentialLang,
                               boolean isCollection, String fullName) {
        PropertyAccessor myAccessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
        if (!isCollection) {
            myAccessor.setPropertyValue(fieldName, stringToGenericType(myAccessor.getPropertyType(fieldName), fieldValue, credentialLang, fullName));
        } else {
            Collection property = null;
            try {
                property = (Collection) reflectiveUtil.getOrInstanceAnyField(fieldName, target);
            } catch (ReflectiveOperationException e) {
                //TODO
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INSTANCE_ERROR, fullName, fieldName, target.getClass().getName()).setCause(e);
            }

            Field listField = null;
            try {
                listField = target.getClass().getDeclaredField(fieldName);
            } catch (java.lang.NoSuchFieldException e) {
                try {
                    listField = target.getClass().getSuperclass().getDeclaredField(fieldName);
                } catch (NoSuchFieldException noSuchFieldException) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_FOUND, fullName, fieldName, target.getClass().getName()).setCause(e);
                }
            }

            ParameterizedType listType = (ParameterizedType) listField.getGenericType();
            Class<?> stringListClass = (Class<?>) listType.getActualTypeArguments()[0];
            property.add(stringToGenericType(stringListClass, fieldValue, credentialLang, fullName));
        }
    }

    public void setTranslatableField(ITranslatable target, String fieldLang, String fieldValue, String primaryLanguage) {
        target.getContents().overrideValue(fieldLang != null ? fieldLang : primaryLanguage, fieldValue);
    }

    public void setCodeField(Object target, String fieldName, String fieldValue, String
            primaryLang, List<String> retrieveLangs, boolean isCollection, String fullName) {

        List<String> availableLangs = new ArrayList<>(retrieveLangs);
        if (!availableLangs.contains(primaryLang)) {
            availableLangs.add(primaryLang);
        }

        ConceptDTO code = null;
        CustomizableCLFieldDTO controlledList = null;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (!field.isAnnotationPresent(CustomizableCLFieldDTO.class)) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_CL_FIELD, fullName, fieldName, target.getClass().getName());
            }
            controlledList = field.getAnnotation(CustomizableCLFieldDTO.class);
        } catch (NoSuchFieldException e) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_FOUND, fullName, fieldName, target.getClass().getName()).setCause(e);
        }

        //Use CustomizableCLField on DTO
        if (ControlledList.HUMAN_SEX.equals(controlledList.targetFramework())) {
            String clFrameworkUri = null;
            String clUri = null;
            clFrameworkUri = ControlledList.HUMAN_SEX.getUrl();
            if (urlValidator.isValid(fieldValue)) {
                clUri = fieldValue;
            } else if ("M".equals(fieldValue)) {
                clUri = ControlledListConcept.HUMAN_SEX_MALE.getUrl();
            } else if ("F".equals(fieldValue)) {
                clUri = ControlledListConcept.HUMAN_SEX_FEMALE.getUrl();
            } else if ("NAP".equals(fieldValue)) {
                clUri = ControlledListConcept.HUMAN_SEX_NAP.getUrl();
            } else {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT, fullName, fieldName, fieldValue);
            }
            code = controlledListCommonsService.searchConceptByUri(clFrameworkUri, clUri, primaryLang, availableLangs);
        } else if (ControlledList.COUNTRY.equals(controlledList.targetFramework())) {
            try {
                if (urlValidator.isValid(fieldValue)) {
                    code = controlledListCommonsService.searchConceptByUri(ControlledList.COUNTRY.getUrl(), fieldValue, primaryLang, availableLangs);
                } else {
                    code = controlledListCommonsService.searchCountryByEuvocField(ControlledList.COUNTRY.getUrl(), fieldValue, availableLangs);
                }
                if (code == null) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT, fullName, fieldName, fieldValue);
                }
            } catch (Exception e) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT, fullName, fieldName, fieldValue);
            }
        } else if (ControlledList.ATU.equals(controlledList.targetFramework())) {
            try {
                if (urlValidator.isValid(fieldValue)) {
                    code = controlledListCommonsService.searchConceptByUri(ControlledList.ATU.getUrl(), fieldValue, primaryLang, availableLangs);
                }
                if (code == null) {
                    throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT, fullName, fieldName, fieldValue);
                }
            } catch (Exception e) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT, fullName, fieldName, fieldValue);
            }
        } else {
            throw new EDCIException();
        }

        if (!isCollection) {
            PropertyAccessor myAccessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
            myAccessor.setPropertyValue(fieldName, code);
        } else {
            Collection property = null;
            try {
                property = (Collection) reflectiveUtil.getOrInstanceAnyField(fieldName, target);
            } catch (ReflectiveOperationException e) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_NOT_FOUND, fullName, fieldName, target.getClass().getName()).setCause(e);
            }
            property.add(code);
        }
    }

    protected Date stringToDate(String dateStr, String fullName) {

        if (dateStr == null || dateStr.length() <= 0) {
            return null;
        }

        Date returnValue = null;
        try {
            returnValue = formatterLocal.parse(dateStr);
        } catch (Exception e) {
            try {
                returnValue = formatterFull.parse(dateStr);
            } catch (Exception e1) {
                throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_DATE_FORMAT_INVALID, fullName, dateStr).setCause(e1);
            }
        }

        return returnValue;

    }

    public <T> T stringToGenericType(Class<T> fieldType, String value, String credentialLang, String fullName) {

        T field = null;

        if (Date.class.equals(fieldType)) {
            field = (T) stringToDate(value, fullName);
        } else if (String.class.equals(fieldType)) {
            field = (T) value;
        } else if (LiteralMap.class.equals(fieldType)) {
            field = (T) new LiteralMap(credentialLang, value);
        } else if (Integer.class.equals(fieldType)) {
            try {
                field = (T) Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new EDCIException(e);
            }
        } else if (URL.class.equals(fieldType)) {
            try {
                field = (T) new URL(value);
            } catch (MalformedURLException e) {
                throw new EDCIException(e);
            }
        } else if (URI.class.equals(fieldType)) {
            try {
                field = (T) new URI(value);
            } catch (URISyntaxException e) {
                throw new EDCIException(e);
            }
        } else if (ZonedDateTime.class.equals(fieldType)) {
            //field = (T) LocalDateTime.parse(value).atZone(ZoneId.systemDefault());
            field = (T) ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } else if (Period.class.equals(fieldType)) {
            field = (T) Period.hours(Integer.valueOf(value));
        } else {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_XLS_FIELD_TYPE_NOT_SUPPORTED, fullName, fieldType.getName());
        }

        return field;
    }


    public EDCIWorkBookReader getEdciWorkBookReader() {
        return edciWorkBookReader;
    }

    public void setEdciWorkBookReader(EDCIWorkBookReader edciWorkBookReader) {
        this.edciWorkBookReader = edciWorkBookReader;
    }

    public CredentialMapper getCredentialMapper() {
        return credentialMapper;
    }

    public void setCredentialMapper(CredentialMapper credentialMapper) {
        this.credentialMapper = credentialMapper;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public StringDateMapping getStringDateMapping() {
        return stringDateMapping;
    }

    public void setStringDateMapping(StringDateMapping stringDateMapping) {
        this.stringDateMapping = stringDateMapping;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public IssuerCustomizableModelUtil getIssuerCustomizableModelUtil() {
        return issuerCustomizableModelUtil;
    }

    public void setIssuerCustomizableModelUtil(IssuerCustomizableModelUtil issuerCustomizableModelUtil) {
        this.issuerCustomizableModelUtil = issuerCustomizableModelUtil;
    }
}
