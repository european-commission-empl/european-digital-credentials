package eu.europa.ec.empl.edci.issuer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Association;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.GradeObject;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.exception.ReflectiveException;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.ColumnInfo;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.util.*;

@Component("DynamicWorkBookUtil")
public class EDCIWorkBookReader implements IWorkBookReader {

    private static final Logger logger = Logger.getLogger(EDCIWorkBookReader.class);

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Autowired
    public ReflectiveUtil reflectiveUtil;

    @Autowired
    private EDCIWorkBookUtil edciWorkBookUtil;

    @Autowired
    public Validator validator;


    /**
     * @param startingIndex The Staring index where data is stored
     * @param endIndex      The Ending index where data is stored
     * @param row           the row where the metadata resides
     * @return The list of the metadata for that row, in Strings
     */
    public List<String> extractMetaDataStringRow(int startingIndex, int endIndex, Row row) {
        List<String> rowData = new ArrayList<>();
        for (int i = startingIndex; i < endIndex; i++) {
            Cell currentCell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            rowData.add(this.getStringCellValue(currentCell));
        }
        return rowData;
    }

    public List<Integer> extractMetaDataIntegerRow(int startingIndex, int columnInfoEndIndex, Row row) {
        List<Integer> rowData = new ArrayList<>();
        for (int i = startingIndex; i < columnInfoEndIndex; i++) {
            Cell currentCell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            try {
                int value = validator.isEmpty(this.getDoubleCellValue(currentCell)) ? -1 : this.getDoubleCellValue(currentCell).intValue();
                rowData.add(value);
            } catch (Exception e) {
                //If string found, add -1 to avoid sizing errors
                rowData.add(-1);
                getLogger().error(String.format("%s -> [%s]", e.getMessage(), this.getCellInfo(currentCell)));
            }

        }
        return rowData;
    }

    /**
     * Pases a sheet and retrieves the objects+
     *
     * @param sheet        the sheet to be parsed
     * @param associations a list of Associations
     * @return a list of objects
     */
    public Map<Integer, List<Object>> parseSheet(Sheet sheet, List<Association> associations, List<GradeObject> gradeObjects, boolean rowRef) {
        logger.trace("[I] - parseSheet");
        Map<Integer, List<Object>> sheetObjects = new HashMap<Integer, List<Object>>();
        //Calculate last indexes for rows and columns
        int columnInfoEndIndex = edciWorkBookUtil.getLastColumnInfoIndex(sheet);
        int rowInfoEndIndex = edciWorkBookUtil.getLastRowInfoIndex(sheet, columnInfoEndIndex);
        logger.trace(String.format("Sheet %s - last row %d - last cell %s", sheet.getSheetName(), rowInfoEndIndex, CellReference.convertNumToColString(columnInfoEndIndex)));
        //Get column info
        Map<Integer, ColumnInfo> columnInfoMap = this.getSheetColumnInfo(sheet, columnInfoEndIndex);

        if (logger.isTraceEnabled()) {
            try {
                logger.trace(new ObjectMapper().writeValueAsString(columnInfoMap));
            } catch (JsonProcessingException e) {
                logger.error(e);
            }
        }
        logger.trace("Starting Row " + XLS.ROW_STARTING_INDEX + " / " + sheet.getLastRowNum());
        //Get Sheet objects in index/object format

        for (int i = XLS.ROW_STARTING_INDEX; i <= rowInfoEndIndex; i++) {
            logger.trace(String.format("Parsing : [sheet %s - row index %d - row num %d]", sheet.getSheetName(), i, sheet.getRow(i).getRowNum()));
            //Parse objects from sheet and add to results map
            sheetObjects.put(i, parseSheetRow(sheet.getRow(i), columnInfoMap, associations, gradeObjects, columnInfoEndIndex, rowRef));
        }
        logger.trace("[D] - parseSheet");
        return sheetObjects;
    }

    /**
     * @param sheet              The sheet to get the column info from
     * @param columnInfoEndIndex the column info of the last not blank cell
     * @return A map with an integer referencing the column index,
     */
    public Map<Integer, ColumnInfo> getSheetColumnInfo(Sheet sheet, int columnInfoEndIndex) {

        Map<Integer, ColumnInfo> sheetColumnInfo = new HashMap<Integer, ColumnInfo>();

        /**
         * Get columnInfo from predefined rows
         */
        List<String> classNames = this.extractMetaDataStringRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.CLASS_ROW));
        List<String> fields = this.extractMetaDataStringRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.FIELD_ROW));
        List<String> types = this.extractMetaDataStringRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.TYPE_ROW));
        List<String> rangeProperties = this.extractMetaDataStringRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.RANGEPROP_ROW));
        List<Integer> rangeRefs = this.extractMetaDataIntegerRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.RANGEREF_ROW));
        List<String> languages = this.extractMetaDataStringRow(XLS.DEFINITION_COLUMN + 1, columnInfoEndIndex, sheet.getRow(XLS.LANGUAGE_ROW));

        if (types.size() < fields.size())
            throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_FORMAT_SHEET_ERROR, String.valueOf(classNames.size()), String.valueOf(types.size()), sheet.getSheetName());
        ;

        for (int i = 0; i < fields.size(); i++) {
            String rangeProperty = i < rangeProperties.size() ? rangeProperties.get(i) : "";
            Integer rangeRef = i < rangeRefs.size() ? rangeRefs.get(i) : -1;
            String language = i < languages.size() ? languages.get(i) : "";
            ColumnInfo columnInfo = new ColumnInfo(classNames.get(i), fields.get(i), types.get(i), rangeProperty, rangeRef, language);
            sheetColumnInfo.put(XLS.DEFINITION_COLUMN + 1 + i, columnInfo);
        }


        return sheetColumnInfo;
    }


    /**
     * Parses the information for a particular row, creates instances of the objects in the row
     *
     * @param currentRow         the row where the information resides
     * @param columnInfoMap      the map with the information for the columns in the row
     * @param associations       a list of Associations
     * @param columnInfoEndIndex the column info of the last not blank cell
     * @return a CustomList of objects with all updated information
     */
    public List<Object> parseSheetRow(Row currentRow, Map<Integer, ColumnInfo> columnInfoMap, List<Association> associations, List<GradeObject> gradeObjects, int columnInfoEndIndex, boolean rowRef) {
        logger.trace("[I] - ParseSheetRow");
        //References to objects in this row
        List<Object> rowObjects = new ArrayList<Object>();
        //Map<Integer, Object> rowObjects = new HashMap<>();
        //Instances of the objects
        for (int i = XLS.STARTING_COLUMN_INDEX; i < columnInfoEndIndex; i++) {
            Cell currentCell = currentRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            ColumnInfo columnInfo = columnInfoMap.get(i);
            parseSheetColumn(currentCell, columnInfo, associations, gradeObjects, rowObjects, rowRef);
        }

        logger.trace("[E] - parseSheetRow");
        return rowObjects;
    }

    private void parseSheetColumn(Cell currentCell, ColumnInfo
            columnInfo, List<Association> associations, List<GradeObject> gradeObjects, List<Object> rowObjects, boolean rowRef) {
        Object updatedObject;
        XLS.PARSE_TYPE parseType = edciWorkBookUtil.chooseParseType(columnInfo);
        Object cellObject = this.getSafeCellValue(currentCell);
        String className = "";
        if (validator.notEmpty(cellObject)) {
            switch (parseType) {
                case PROPERTY:
                    if (!reflectiveUtil.getEquivalences().containsKey(columnInfo.getClassName()))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL, currentCell, columnInfo.getClassName());

                    className = reflectiveUtil.getEquivalences().get(columnInfo.getClassName());
                    try {
                        updatedObject = reflectiveUtil.findOrCreateInstanceOf(className, rowObjects);
                        edciWorkBookUtil.updatePropertyWithCellAndColumnInfo(updatedObject, columnInfo, cellObject, updatedObject);
                        rowObjects.add(updatedObject);
                    } catch (Exception e) {
                        this.handleXLSException(e, currentCell);
                    }
                    break;
                case ASSOCIATION:
                    if (!reflectiveUtil.getEquivalences().containsKey(columnInfo.getClassName()))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL, currentCell, columnInfo.getClassName());

                    edciWorkBookUtil.updateAssociationListWithCellAndColumnInfo(columnInfo.getClassName(), columnInfo.getField(), columnInfo.getRangeProperty(), currentCell.getRowIndex(), currentCell, associations);

                    break;
                case NESTED_ASSOCIATION:
                    int splitIndex = columnInfo.getClassName().indexOf(XLS.SPLIT_CHARACTER_NESTED_ASSETS);
                    if (splitIndex == -1)
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_NESTEDASSOCIATION_NOTFOUND, currentCell);

                    className = columnInfo.getClassName().substring(0, splitIndex);
                    String field = columnInfo.getClassName().substring(splitIndex + 1).concat(String.valueOf(XLS.SPLIT_CHARACTER_NESTED_ASSETS)).concat(columnInfo.getField());

                    if (validator.isEmpty(className))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTFOUND, currentCell);

                    if (!reflectiveUtil.getEquivalences().containsKey(className))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL, currentCell, columnInfo.getClassName());

                    edciWorkBookUtil.updateAssociationListWithCellAndColumnInfo(className, field, columnInfo.getRangeProperty(), currentCell.getRowIndex(), currentCell, associations);

                    break;
                //ToDO -> Deprecated (unUsed)
                case EXTERNAL_ASSOCIATION:
                    if (!reflectiveUtil.getEquivalences().containsKey(columnInfo.getClassName()))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL, currentCell, columnInfo.getClassName());

                    List<String> originInfo = Arrays.asList(columnInfo.getField().split(XLS.SPLIT_STRING_NESTED_ASSETS));
                    if (originInfo.size() != 2)
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_EXTERNALASSOCIATION_INVALIDFORMAT, currentCell);

                    edciWorkBookUtil.updateAssociationListWithCellAndColumnInfo(originInfo.get(0), originInfo.get(1), columnInfo.getRangeProperty(), columnInfo.getRangeRef() - 1, currentCell, associations);
                    break;
                case NESTED_PROPERTY:

                    List<String> classParameterPath = Arrays.asList(columnInfo.getClassName().split(XLS.SPLIT_STRING_NESTED_ASSETS));

                    if (classParameterPath.isEmpty())
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_NESTEDPROPERTY_NOTFOUND, currentCell);

                    className = classParameterPath.get(0);

                    if (!reflectiveUtil.getEquivalences().containsKey(className))
                        throw edciWorkBookUtil.createFileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL, currentCell, columnInfo.getClassName());

                    //get real classname

                    className = reflectiveUtil.getEquivalences().get(className);
                    try {
                        Object parentInstance = reflectiveUtil.findOrCreateInstanceOf(className, rowObjects);
                        updatedObject = edciWorkBookUtil.reflectiveUtil.getLastInstanceFromParameterPath(classParameterPath.subList(1, classParameterPath.size()), parentInstance, false);
                        edciWorkBookUtil.updatePropertyWithCellAndColumnInfo(updatedObject, columnInfo, cellObject, parentInstance);
                        rowObjects.add(parentInstance);
                    } catch (Exception e) {
                        handleXLSException(e, currentCell);
                    }

                    break;
                case GRADES_ASSOCIATION:
                    edciWorkBookUtil.updateGradedObjectListWithCellAndColumnInfo(columnInfo.getClassName(), currentCell.getRowIndex(), Double.valueOf(columnInfo.getField()).intValue(), cellObject, gradeObjects, rowRef);
                    break;
                case IGNORE:
                    break;
            }
        }
    }

    //Parser info from workbook and fills a classified bag, if association and gradeObject list are provided, will also update accordingly to perfom operations
    public Map<String, Map<Integer, Object>> parseWorkBookIntoClassifiedBag(Workbook workbook, @Nullable List<Association> associations, @Null List<GradeObject> gradeObjects, @Null Boolean isGradeRowRef) {

        /*Initialize object and association bags*/
            /*
            SuperBag Has Format:
                List<Map(1 per sheet)<Integer(rowIndex),List<Object>(Objects in that row)
             */
        List<Map<Integer, List<Object>>> superBag = new ArrayList<>();
        if (associations == null) associations = new ArrayList<>();
        if (gradeObjects == null) gradeObjects = new ArrayList<>();
        if (isGradeRowRef == null) isGradeRowRef = true;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (!XLS.UNSCANNED_SHEETS.contains(sheet.getSheetName())) {
                //scan sheet, return objects and update associations
                superBag.add(parseSheet(sheet, associations, gradeObjects, isGradeRowRef));
            }
        }

        //Classify the bag in entity/map<Index,Object> format
        return edciWorkBookUtil.classifyBag(superBag, reflectiveUtil.getEquivalences());
    }

    private void handleXLSException(Exception e, Cell currentCell) {
        if (e instanceof FileBaseDataException) {
            FileBaseDataException ex = (FileBaseDataException) e;
            edciWorkBookUtil.addFileBaseDataExceptionCellData(ex, currentCell);
            throw ex;
        }

        if (e instanceof ReflectiveException) {
            e.printStackTrace();
            logger.error(e);
            ReflectiveException ex = (ReflectiveException) e;
            FileBaseDataException fileBaseDataException = new FileBaseDataException(ex.getMessageKey(), ex.getMessageArgs());
            edciWorkBookUtil.addFileBaseDataExceptionCellData(fileBaseDataException, currentCell);
            throw fileBaseDataException;
        }

        FileBaseDataException fbde = new FileBaseDataException(MessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR, e);
        edciWorkBookUtil.addFileBaseDataExceptionCellData(fbde, currentCell);
        throw fbde;
    }

}


