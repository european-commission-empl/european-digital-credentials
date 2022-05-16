package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Localizable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Association;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.GradeObject;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.ColumnInfo;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("WorkBookEuroPassUtil")
public class EDCIWorkBookUtil {

    private Logger logger = LogManager.getLogger(EDCIWorkBookUtil.class);
    @Autowired
    public ReflectiveUtil reflectiveUtil;

    @Autowired
    private EDCIWorkBookReader edciWorkBookReader;

    @Autowired
    public Validator validator;

    /*
     *
     *
     * @param sheet the sheet to be parsed
     * @return a list of objects
     */
    public int getLastColumnInfoIndex(Sheet sheet) {
        Row classRow = sheet.getRow(XLS.CLASS_ROW);
        Row typeRow = sheet.getRow(XLS.TYPE_ROW);
        for (int i = XLS.STARTING_INDEX_COLUMN_INFO; i <= classRow.getLastCellNum(); i++) {
            Cell classCell = classRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell typeCell = typeRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (validator.isEmpty(edciWorkBookReader.getSafeCellValue(classCell)) || XLS.PARSE_TYPE.COMMENT.value().equals(edciWorkBookReader.getStringCellValue(typeCell))) {
                return i;
            }
        }
        //no info found in the excel
        return 0;
    }

    public int getLastRowInfoIndex(Sheet sheet, int columInfoEndIndex) {
        for (int i = XLS.ROW_STARTING_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            //created but totally blank row case
            if (validator.isEmpty(row)) return i - 1;
            List<Cell> cells = new ArrayList<Cell>();
            for (int j = XLS.STARTING_COLUMN_INDEX; j < columInfoEndIndex; j++) {
                cells.add(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            boolean isLast = cells.stream().noneMatch(cell -> cell.getCellType() != CellType.BLANK && validator.notEmpty(edciWorkBookReader.getSafeCellValue(cell)));
            if (isLast) return i;
        }
        //first guess is OK
        return sheet.getLastRowNum();
    }


    public Map<Integer, Object> buildEntityMap(Class clazz, List<Map<Integer, Object>> superBag) {
        Map<Integer, Object> result = new HashMap<>();
        for (Map<Integer, Object> map : superBag) {

            for (Integer integer : map.keySet()) {
                Object obj = map.get(integer);
                if (obj.getClass().getTypeName() == clazz.getTypeName()) {
                    result.put(integer, obj);
                }
            }
        }

        return result;

    }

    private Object findItemInBag(String className, Integer index, Map<String, Map<Integer, Object>> classifiedBag) {
        Map<Integer, Object> classMap;
        Object result;
        classMap = classifiedBag.get(className);

        if (validator.notEmpty(classMap)) {
            result = classMap.get(index);
            if (validator.notEmpty(result)) {
                return result;
            } else {
                //message never used¿
                throw new FileBaseDataException(EDCIMessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR);
            }
        } else {
            //message never used?
            throw new FileBaseDataException(EDCIMessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR);
        }
    }

    public Map<String, Map<Integer, Object>> classifyBag(List<Map<Integer, List<Object>>> superBag, Map<String, String> equivalences) {
        Map<String, Map<Integer, Object>> classifiedBag = new HashMap<String, Map<Integer, Object>>();

        for (Map<Integer, List<Object>> sheetBag : superBag) {
            for (Map.Entry<Integer, List<Object>> entry : sheetBag.entrySet()) {

                for (Object object : entry.getValue()) {
                    String equivalence = equivalences.get(object.getClass().getName());
                    if (validator.notEmpty(equivalence) && validator.isEmpty(classifiedBag.get(equivalence))) {
                        classifiedBag.put(equivalence, new HashMap<Integer, Object>());
                    }
                    Map<Integer, Object> classReferences = classifiedBag.get(equivalence);
                    classReferences.put(entry.getKey(), object);
                }

            }
        }

        return classifiedBag;
    }

    public void buildAssociations(List<Association> associations, Map<String, Map<Integer, Object>> classifiedBag) {
        for (Association association : associations) {
            Field field = null;
            Object srcObject, destObject;

            try {
                srcObject = findItemInBag(association.getSrcClass(), association.getSrcId(), classifiedBag);
            } catch (FileBaseDataException e) {
                throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_ASSOCIATION_ERROR, String.valueOf(association.getDestId()), association.getDestClass());
            }

            try {
                destObject = findItemInBag(association.getDestClass(), association.getDestId(), classifiedBag);
            } catch (FileBaseDataException e) {
                throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_ASSOCIATION_ERROR, String.valueOf(association.getDestId()), association.getDestClass());
            }

            if (Pattern.matches(XLS.SPLIT_PATTERN_NESTED_ASSETS, association.getSrcFieldAssociation())) {
                List<String> parameterPath = new ArrayList<String>(Arrays.asList(association.getSrcFieldAssociation().split(XLS.SPLIT_STRING_NESTED_ASSETS)));

                if (parameterPath.size() <= 1) {
                    throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_NESTEDPROPERTY_MISSINGINFO);
                }
                String fieldName = parameterPath.remove(parameterPath.size() - 1);

                srcObject = reflectiveUtil.getLastInstanceFromParameterPath(parameterPath, srcObject, false);
                field = reflectiveUtil.findField(srcObject.getClass(), fieldName);

            } else {
                field = reflectiveUtil.findField(srcObject.getClass(), association.getSrcFieldAssociation());
            }


            if (validator.notEmpty(field)) {
                //If an associated field requires an object to be casted to a child instance
                if (reflectiveUtil.isChildField(srcObject.getClass(), field)) {
                    try {
                        srcObject = reflectiveUtil.castToChild(field.getDeclaringClass(), srcObject, null);
                    } catch (ReflectiveOperationException e) {
                        throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_CAST_FAILED, srcObject.getClass().getName(), field.getDeclaringClass().getName());
                    }
                }
                try {
                    if (reflectiveUtil.isListInstance(field)) {
                        List<Object> list = reflectiveUtil.getOrInstanciateListField(field, srcObject);
                        list.add(destObject);
                    } else {
                        reflectiveUtil.setField(field.getName(), srcObject, destObject);
                    }
                } catch (ReflectiveOperationException e) {
                    throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_ASSOCIATION_PROCESS_ERROR, field.getName(), srcObject.getClass().getName(), destObject.getClass().getName());
                }
            } else {
                throw new FileBaseDataException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_FIELDNOTFOUND, association.getSrcFieldAssociation(), association.getSrcClass());
            }

        }
    }


    public void updateAssociationListWithCellAndColumnInfo(String srcClass, String field, String destClass, int srcId, Cell
            currentCell, List<Association> associations) {
        String cellObject = edciWorkBookReader.getStringCellValue(currentCell);
        cellObject = StringUtils.deleteWhitespace(cellObject);
        if (validator.notEmpty(cellObject)) {
            if (Pattern.matches(XLS.PATTERN_MULTIPLE_ASSOCIATIONS, cellObject)) {
                List<Integer> references = Arrays.stream(cellObject.split(XLS.SPLIT_CHARACTER_MULTIPLE_ASSOCIATIONS)).map(reference -> Integer.valueOf(reference) - 1).collect(Collectors.toList());
                for (Integer reference : references) {
                    updateAssociationListWithCellAndColumnInfo(srcClass, reference, field, destClass, srcId, associations);
                }
            } else {
                try {
                    Double doubleValue = Double.valueOf(cellObject);
                    if (Double.valueOf(cellObject) > 0) {
                        Double destId = doubleValue - 1;
                        updateAssociationListWithCellAndColumnInfo(srcClass, destId.intValue(), field, destClass, srcId, associations);
                    }
                } catch (NumberFormatException e) {
                    throw this.createFileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_ASSOCIATION_FORMAT_ERORR, currentCell, String.valueOf(cellObject));
                }
            }
        }
    }


    private void updateAssociationListWithCellAndColumnInfo(String srcClass, int destId, String field, String destClass, int srcId, List<
            Association> associations) {
        if (validator.notEmpty(destId)) {
            associations.add(new Association(srcClass, srcId, field, destClass, destId));
        }
    }

    public void updatePropertyWithCellAndColumnInfo(Object instance, ColumnInfo
            columnInfo, Object cellObject, @Nullable Object parentInstance) throws InvocationTargetException, ReflectiveOperationException {

        XLS.PARSE_CASE parseCase = getPropertyParseCase(columnInfo);
        Field field = reflectiveUtil.findField(instance.getClass(), columnInfo.getField());
        if (validator.isEmpty(field))
            throw new FileBaseDataException(EDCIMessageKeys.Exception.Reflection.EXCEPTION_REFLECTION_FIELDNOTFOUND, columnInfo.getField(), columnInfo.getClassName());

        if (reflectiveUtil.isChildField(instance.getClass(), field)) {
            instance = reflectiveUtil.castToChild(field.getDeclaringClass(), instance, parentInstance);
        }


        updateObjectWithCellInfo(field, instance, cellObject, columnInfo, parseCase);
    }

    public void updateGradedObjectListWithCellAndColumnInfo(String origin, int originRef, int gradedRef, Object grade, List<GradeObject> gradeObjects, boolean isGradeRowRef) {
        if (isGradeRowRef) gradedRef = gradedRef - 1;
        gradeObjects.add(new GradeObject(origin, originRef, gradedRef, grade));
    }

    public XLS.PARSE_TYPE chooseParseType(ColumnInfo columnInfo) {
        return XLS.PARSE_TYPE.getParseType(columnInfo.getType());
    }

    private XLS.PARSE_CASE getPropertyParseCase(ColumnInfo columnInfo) {
        if (columnInfo.getRangeRef() > -1) {
            return getRangeRefPropertyCase(columnInfo);
        } else {
            return getNoRangeRefPropertyCase(columnInfo);
        }
    }

    private XLS.PARSE_CASE getNoRangeRefPropertyCase(ColumnInfo columnInfo) {
        if (validator.isEmpty(columnInfo.getRangeProperty())) {
            if (validator.isEmpty(columnInfo.getLanguage())) {
                return XLS.PARSE_CASE.DIRECT_ATTRIBUTE;
            } else {
                return XLS.PARSE_CASE.MULTILANG_OBJECT;
            }

        } else {
            if (validator.isEmpty(columnInfo.getLanguage())) {
                return XLS.PARSE_CASE.DIRECT_OBJECT;
            } else {
                return XLS.PARSE_CASE.MULTILANG_OBJECT;
            }
        }
    }

    private XLS.PARSE_CASE getRangeRefPropertyCase(ColumnInfo columnInfo) {
        if (validator.isEmpty(columnInfo.getRangeProperty())) {
            if (validator.isEmpty(columnInfo.getLanguage())) {
                return XLS.PARSE_CASE.ATTRIBUTE_LIST;
            } else {
                return XLS.PARSE_CASE.MULTILANG_OBJECT_LIST;
            }

        } else {
            if (validator.isEmpty(columnInfo.getLanguage())) {
                return XLS.PARSE_CASE.OBJECT_LIST;
            } else {
                return XLS.PARSE_CASE.MULTILANG_OBJECT_LIST;
            }
        }
    }


    /**
     * Checks the entities in the sheets and fills a list
     *
     * @param sheet           The Sheet to be scanned
     * @param scannedEntities The CustomList of already scanned entities in other sheet
     * @return a new aggregated list with sheet entities
     */
    public List<String> scanSheet(Sheet sheet, List<String> scannedEntities) {
        Row classRow = sheet.getRow(XLS.CLASS_ROW);

        List<String> sheetEntities = new ArrayList<String>();

        for (int i = XLS.DEFINITION_COLUMN + 1; i < classRow.getLastCellNum(); i++) {
            Cell currentCell = classRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String entity = edciWorkBookReader.getBaseMandatoryStringCellValue(currentCell, "The entity is not provided");
            if (!sheetEntities.contains(entity)) {
                if (!isEntityScanned(scannedEntities, entity)) {
                    sheetEntities.add(entity);
                } else {
                    throw this.createFileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_DEFINITION_FORMAT, currentCell, entity);
                }
            }
        }

        List<String> aggregatedEntities = new ArrayList<String>(scannedEntities);
        aggregatedEntities.addAll(sheetEntities);
        return aggregatedEntities;
    }

    /**
     * Inner method to check if an entity is already scanned
     *
     * @param scannedEntities the list of already scanned entities
     * @param entity          the entity to be added
     * @return result of scanned entity
     */
    private boolean isEntityScanned(List<String> scannedEntities, String entity) {
        return scannedEntities.stream().anyMatch(value -> value.equals(entity));
    }


    public boolean isLanguageField(ColumnInfo columnInfo) {
        XLS.PARSE_TYPE parseType = this.chooseParseType(columnInfo);
        if (parseType == XLS.PARSE_TYPE.PROPERTY || parseType == XLS.PARSE_TYPE.NESTED_PROPERTY) {
            XLS.PARSE_CASE parseCase = this.getPropertyParseCase(columnInfo);
            if (parseCase == XLS.PARSE_CASE.MULTILANG_OBJECT || parseCase == XLS.PARSE_CASE.MULTILANG_OBJECT_LIST) {
                return true;
            }
        }
        return false;
    }

    private void updateObjectWithCellInfo(Field targetField, Object instance, Object cellObject, ColumnInfo
            columnInfo, XLS.PARSE_CASE parseCase) throws ReflectiveOperationException, FileBaseDataException, InvocationTargetException {
        logger.trace("[I] - updateObjectWithCellInfo");
        Object listItem, object;
        List<Object> objectList;
        //Field field;
        Localizable localizable;
        switch (parseCase) {
            case DIRECT_ATTRIBUTE:
                targetField = reflectiveUtil.findField(instance.getClass(), columnInfo.getField());
                if (!reflectiveUtil.isListInstance(targetField)) {
                    reflectiveUtil.setField(columnInfo.getField(), instance, cellObject);
                } else {
                    if (reflectiveUtil.isPrimitiveList(targetField)) {
                        List<Object> arrayList = reflectiveUtil.getOrInstanciateListField(targetField, instance);
                        arrayList.add(cellObject);
                    } else {
                        throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_ENTITY_LIST, columnInfo.getField());
                    }
                }
                break;
            case DIRECT_OBJECT:
                // GET OBJECT
                targetField = reflectiveUtil.findField(instance.getClass(), columnInfo.getField());
                object = reflectiveUtil.getOrInstantiateField(targetField, instance, null);
                // SET PROPERTY
                reflectiveUtil.setField(columnInfo.getRangeProperty(), object, cellObject);
                break;
            case MULTILANG_OBJECT:
                // GET OBJECT
                // SINCE MULTILINGUAL OBJECT, GET CONTENT OBJECT
                object = reflectiveUtil.getOrInstantiateField(targetField, instance, null);

                if (!(object instanceof Localizable) && validator.notEmpty(columnInfo.getRangeProperty())) {
                    targetField = reflectiveUtil.findField(object.getClass(), columnInfo.getRangeProperty());
                    localizable = (Localizable) reflectiveUtil.getOrInstantiateField(targetField, object, null);
                } else {
                    localizable = (Localizable) object;
                }

                localizable.setContent(columnInfo.getLanguage(), String.valueOf(cellObject));
                break;
            case ATTRIBUTE_LIST:
                // GET ARRAY LIST
                List<Object> arrayList = reflectiveUtil.getOrInstanciateListField(targetField, instance);
                //add item to list
                arrayList.add(columnInfo.getRangeRef() - 1, cellObject);
                break;
            case OBJECT_LIST:
                // GET OBJECT LIST
                objectList = reflectiveUtil.getOrInstanciateListField(targetField, instance);
                listItem = reflectiveUtil.getOrInstanciateListItem(objectList, columnInfo.getRangeRef() - 1, targetField);
                Field innerField = reflectiveUtil.findField(listItem.getClass(), columnInfo.getRangeProperty());
                if (reflectiveUtil.isListInstance(innerField)) {
                    List<Object> innerList = reflectiveUtil.getOrInstanciateListField(innerField, listItem);
                    //Add primitive (String) values to lists
                    if (reflectiveUtil.isPrimitiveList(innerField)) {
                        innerList.add(cellObject);
                    } else {
                        throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_NONSTRING_LIST_ITEM, innerField.getName(), cellObject.toString());
                    }

                } else {
                    reflectiveUtil.setField(columnInfo.getRangeProperty(), listItem, cellObject);
                }
                break;
            case MULTILANG_OBJECT_LIST:
                // GET OBJECT LIST
                objectList = reflectiveUtil.getOrInstanciateListField(targetField, instance);
                object = reflectiveUtil.getOrInstanciateListItem(objectList, columnInfo.getRangeRef() - 1, targetField);
                if (!(object instanceof Localizable) && validator.notEmpty(columnInfo.getRangeProperty())) {
                    targetField = reflectiveUtil.findField(object.getClass(), columnInfo.getRangeProperty());
                    localizable = (Localizable) reflectiveUtil.getOrInstantiateField(targetField, object, null);
                } else {
                    localizable = (Localizable) object;
                }

                localizable.setContent(columnInfo.getLanguage(), String.valueOf(cellObject));

                break;
            default:
                throw new FileBaseDataException(EDCIMessageKeys.Exception.XLS.FILE_EXCEL_UNVALID_COLUMINFO);
        }

        logger.trace("[E] - updateObjectWithCellInfo");
    }

    public void addFileBaseDataExceptionCellData(FileBaseDataException fbde, Cell currentCell) {
        fbde.setRow(currentCell.getRowIndex() + 1);
        fbde.setColumn(edciWorkBookReader.getColumnStringValue(currentCell.getColumnIndex()).toUpperCase());
        fbde.setSheetName(currentCell.getSheet().getSheetName());
    }

    public FileBaseDataException createFileBaseDataException(String messageKey, Cell currentCell, String... messageArgs) {
        FileBaseDataException fbde = new FileBaseDataException(messageKey, messageArgs);
        this.addFileBaseDataExceptionCellData(fbde, currentCell);
        return fbde;
    }

}
