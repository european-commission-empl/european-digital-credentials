package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.AssessmentDTO;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.Gradeable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Association;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.GradeObject;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessages;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS.EQUIVALENCE;
import eu.europa.ec.empl.edci.issuer.common.model.AssessmentsListIssueDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.RecipientPersonMapper;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookUtil;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookWriter;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("WorkBookServiceV2")
public class EDCIWorkbookService implements IWorkBookService {

    Logger logger = Logger.getLogger(EDCIWorkbookService.class);

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private EDCIWorkBookReader edciWorkBookReader;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private AssociationService associationService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private Validator validator;

    @Autowired
    private EDCIWorkBookWriter edciWorkBookWriter;

    @Autowired
    private EDCIWorkBookUtil edciWorkBookUtil;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private RecipientPersonMapper recipientPersonMapper;

    @Autowired
    public FileUtil fileUtil;

    @Autowired
    private FileService fileService;


    /*public enum PARSE_TYPE { //Moved to XLS
        PROPERTY, ASSOCIATION, NESTED_PROPERTY, EXTERNAL_ASSOCIATION, IGNORE, MULTIPLE_ASSOCIATION
    }*/


    public Workbook createWorkBook(InputStream inputStream) throws IOException, InvalidFormatException, FileBaseDataException {
        return WorkbookFactory.create(inputStream);
    }

    /**
     * Generate recipient workbook and get bytes
     *
     * @param assessmentsListIssueDTO the assessments to be included
     * @return
     */
    public byte[] generateRecipientWorkbookBytes(AssessmentsListIssueDTO assessmentsListIssueDTO, String lang) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            this.generateRecipientWorkBook(assessmentsListIssueDTO, lang).write(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("could not generate recipient workbook", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Generate recipient workbook from assessments
     *
     * @param assessmentsListIssueDTO the assessments to be included
     * @return
     */
    public Workbook generateRecipientWorkBook(AssessmentsListIssueDTO assessmentsListIssueDTO, String lang) {
        try {
            Workbook recipientWorkbook = this.createWorkBook(new ByteArrayInputStream(fileService.getTemplate(XLS.RECIPIENT_TEMPLATE_NAME)));
            Sheet recipientSheet = recipientWorkbook.getSheet(XLS.RECIPIENTS_SHEET);
            edciWorkBookWriter.writeRecipientXLSGradeColumns(assessmentsListIssueDTO, recipientSheet);
            edciWorkBookWriter.replaceRecipientTemplateHeaders(recipientSheet, lang);
            return recipientWorkbook;
        } catch (IOException | InvalidFormatException e) {
            throw new EDCIException(EDCIIssuerMessages.ERROR_RECIPIENT_TEMPLATE_INVALID, fileUtil.getTemplateFilePath(XLS.RECIPIENT_TEMPLATE_NAME));
        }
    }


    public List<RecipientDataDTO> parseRecipientsData(Workbook workbook) {
        Map<String, Map<Integer, Object>> clasifiedBag = null;
        List<GradeObject> gradeObjects = new ArrayList<>();
        List<Association> associations = new ArrayList<>();
        clasifiedBag = edciWorkBookReader.parseWorkBookIntoClassifiedBag(workbook, associations, gradeObjects, false);
        this.buildAssociations(clasifiedBag, associations);

        Map<Integer, Object> personMap = clasifiedBag.get(EQUIVALENCE.RECIPIENT_CLASS.getKey());
        Map<Long, String> assessmentGrades;

        List<RecipientDataDTO> recipientDataDTOS = new ArrayList<>();
        for (Map.Entry<Integer, Object> personEntry : personMap.entrySet()) {
            PersonDTO personDTO = (PersonDTO) personEntry.getValue();
            int row = personEntry.getKey();
            assessmentGrades = gradeObjects.stream().filter(gradeObject -> gradeObject.getOriginRef() == row).collect(Collectors.toMap(object -> Long.valueOf(object.getGradedRef()), object -> object.getGrade().toString()));
            RecipientDataDTO recipientDataDTO = recipientPersonMapper.toRecipientDTO(personDTO, LocaleContextHolder.getLocale().toString());
            recipientDataDTO.setAssessmentGrades(assessmentGrades);
            recipientDataDTOS.add(recipientDataDTO);
        }

        return recipientDataDTOS;
    }


    @Override
    public List<EuropassCredentialDTO> parseCredentialData(Workbook workbook) {
        Map<String, Map<Integer, Object>> clasifiedBag = null;
        List<Association> associations = new ArrayList<>();
        List<GradeObject> gradeObjects = new ArrayList<>();

        if (isValidFormat(workbook) && isValidNumDataRows(workbook)) {
            clasifiedBag = edciWorkBookReader.parseWorkBookIntoClassifiedBag(workbook, associations, gradeObjects, true);
        }
        //Create associations between entities updating entities references
        this.buildAssociations(clasifiedBag, associations);
        //get root class from the classified Bag, Cast them to correct class and set initially to valid
        List<EuropassCredentialDTO> europassCredentialDTOS = clasifiedBag.get(EQUIVALENCE.ROOT_CLASS.getKey()).values().stream().map(object -> {
            EuropassCredentialDTO credentialDTO = (EuropassCredentialDTO) object;
            credentialDTO.setValid(true);
            return credentialDTO;
        }).collect(Collectors.toList());
        //Stop any operations if no credentials were found
        if (europassCredentialDTOS.size() == 0) {
            throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_CREDENTIAL_NOTFOUND);
        }

        //Execute actions that will be shared across object references (ie: downloaded assets)
        credentialService.preProcessCredentials(europassCredentialDTOS);
        /*
        Credentials are cloned to avoid them pointing the same associates objects (ie: achievements).
        This is required to fill credential-specific fields (ie: grades, Ids)
         */

        List<EuropassCredentialDTO> processedCredentials = new ArrayList<>();

        try {
            EuropassCredentialDTO[] europassCredentialDTOSArray = europassCredentialDTOS.toArray(new EuropassCredentialDTO[europassCredentialDTOS.size()]);
            processedCredentials = edciCredentialModelUtil.cloneArrayModel(europassCredentialDTOSArray);
        } catch (JAXBException | IOException e) {
            throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_CREDENTIAL_GENERATION);
        }

        Map<Integer, Object> personsMap = clasifiedBag.get(EQUIVALENCE.GRADE_HOLDER.getKey());
        Map<Integer, AssessmentDTO> assessmentMap = new HashMap<>();

        if (clasifiedBag.get(EQUIVALENCE.GRADEABLE_ITEM.getKey()) != null) {
            clasifiedBag.get(EQUIVALENCE.GRADEABLE_ITEM.getKey()).entrySet().stream().forEach(entry -> {
                assessmentMap.put(entry.getKey(), (AssessmentDTO) entry.getValue());
            });
        }

        if (personsMap.size() > 0 && assessmentMap.size() > 0) {
            setGradesForCredentials(processedCredentials, personsMap, assessmentMap, gradeObjects);
        }

        //Clone all objects and execute actions that will be specific for each object (ie: setting identifiers)
        credentialService.postProcessCredentials(processedCredentials);
        return processedCredentials;
    }

    public <T, S extends Gradeable> void setGradesForCredentials(List<EuropassCredentialDTO> europassCredentialDTOS, Map<Integer, T> gradeHolders, Map<Integer, S> gradeableItems, List<GradeObject> gradeObjects) {
        //Get the field that contains the holder (credentialSubject holds grades)
        Field holderField = null;
        try {
            holderField = reflectiveUtil.findField(europassCredentialDTOS.get(0), Class.forName(EQUIVALENCE.GRADE_HOLDER.getValue()));
        } catch (ClassNotFoundException e) {
            throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_HOLDERCLASS_NOTVALID, EQUIVALENCE.GRADE_HOLDER.getValue());
        }
        //Get the class of the gradeable items
        Class gradeableClass = gradeableItems.entrySet().iterator().next().getValue().getClass();
        //Loop though credentials
        for (EuropassCredentialDTO europassCredentialDTO : europassCredentialDTOS) {
            try {
                //Get the holder field for the credentials
                T holder = (T) reflectiveUtil.getOrInstantiateField(holderField, europassCredentialDTO, null);
                //get any gradeObject that contains this holder (notice: we will have grades of another credential of the same person if that person has multiple credentials)
                List<GradeObject> holderGrades = findHolderGradeObjects(gradeObjects, holder, gradeHolders);
                //loop through grades that contain this holder rference
                for (GradeObject holderGrade : holderGrades) {
                    //Look for the pk of the current gradeable Item
                    String pk = gradeableItems.get(holderGrade.getGradedRef()).getPk();
                    //Get any gradeable item with the same type for the holder
                    List<S> holderGradeableItems = reflectiveUtil.getInnerObjectsOfType(gradeableClass, holder, null, null);
                    for (S holderGradeableItem : holderGradeableItems) {
                        if (holderGradeableItem.getPk().equals(pk)) {
                            Object grade = holderGrade.getGrade();
                            if (grade != null && grade instanceof Number) {
                                grade = new DecimalFormat("#.##").format(grade);
                            }
                            holderGradeableItem.graduate(String.valueOf(grade));
                        }
                    }
                }

            } catch (ReflectiveOperationException e) {
                throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_HOLDERFIELD_NOTVALID, holderField.getName());
            }

        }
    }

    public <T> List<GradeObject> findHolderGradeObjects(List<GradeObject> gradeObjects, T holder, Map<Integer, T> holders) {
        //Add any indexes where the user is found (same user can be in two rows)
        List<Integer> holderIndexes = new ArrayList<Integer>();

        //Loop the current holders, if any is equal to the one in the param, add to the list
        for (Map.Entry<Integer, T> holderEntry : holders.entrySet()) {
            T holderItem = holderEntry.getValue();
            if (holderItem.equals(holder)) {
                holderIndexes.add(holderEntry.getKey());
            }
        }
        /*Assign all of the gradeable objects to all of the grade holders, including the ones related to another credentials, then leave blank if not found.
        (workaround to multiple persons and multiple credentials in same excel) - this means that we cannot throw an exception if an assessment has no value
         */
        return gradeObjects.stream().filter(gradeObject -> holderIndexes.contains(gradeObject.getOriginRef())).collect(Collectors.toList());
    }


    private void buildAssociations(Map<String, Map<Integer, Object>> clasifiedBag, List<Association> associations) {
        //Validate that the associations are ok
        /*if (associationService.validateAssociations(associations)) {
            getLogger.error("Detected recursion");
            throw new FileBaseDataException("Recursive association found");
        }*/
        //Build associations
        edciWorkBookUtil.buildAssociations(associations, clasifiedBag);
    }

    /**
     * Checks if all of the Workbook Sheets are valid (no entities are split across sheets)
     *
     * @param workbook The Workbook to be analyzed
     * @return a boolean representing excel validity
     */
    public boolean isValidFormat(Workbook workbook) throws FileBaseDataException {
        List<String> scannedEntities = new ArrayList<String>();
        try {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!XLS.UNSCANNED_SHEETS.contains(sheet.getSheetName())) {
                    scannedEntities = edciWorkBookUtil.scanSheet(sheet, scannedEntities);
                }
            }

            return true;

        } catch (FileBaseDataException e) {
            logger.error("Duplicated entities across sheets", e);
            throw e;
        }
    }

    /**
     * Checks if all of the Workbook Sheets are valid (no entities are split across sheets)
     *
     * @param workbook The Workbook to be analyzed
     * @return a boolean representing excel validity
     */
    public boolean isValidNumDataRows(Workbook workbook) throws FileBaseDataException {
        List<String> scannedEntities = new ArrayList<String>();
        try {
            boolean europassCredFound = false;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!XLS.UNSCANNED_SHEETS.contains(sheet.getSheetName()) && !XLS.UNVALIDATED_SHEETS.contains(sheet.getSheetName())) {
                    int columnInfoEndIndex = edciWorkBookUtil.getLastColumnInfoIndex(sheet);
                    int rowInfoEndIndex = edciWorkBookUtil.getLastRowInfoIndex(sheet, columnInfoEndIndex);
                    int dataRows = rowInfoEndIndex - (XLS.ROW_STARTING_INDEX - 1);
                    if (!europassCredFound && edciWorkBookUtil.scanSheet(sheet, scannedEntities).contains("EuropassCredential")) {
                        europassCredFound = true;
                        if (dataRows > XLS.MAX_DATA_ROWS_CREDENTIALS) {
                            throw new EDCIBadRequestException("exception.file.excel.max.rows.allowed", String.valueOf(dataRows),
                                    String.valueOf(XLS.MAX_DATA_ROWS_CREDENTIALS), sheet.getSheetName());
                        }
                    } else {
                        if (dataRows > XLS.MAX_DATA_ROWS_OTHERS) {
                            throw new EDCIBadRequestException("exception.file.excel.max.rows.allowed", String.valueOf(dataRows),
                                    String.valueOf(XLS.MAX_DATA_ROWS_CREDENTIALS), sheet.getSheetName());
                        }
                    }
                }
            }

            return true;

        } catch (FileBaseDataException e) {
            logger.error("Duplicated entities across sheets", e);
            throw e;
        }
    }


}
