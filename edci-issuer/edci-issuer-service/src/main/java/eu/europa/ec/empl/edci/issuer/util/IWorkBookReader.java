package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public interface IWorkBookReader {

    abstract Logger getLogger();

    abstract Validator getValidator();

    default void setOrCreateCellValue(Sheet sheet, int row, int column, String value) {
        this.getOrCreateCellAt(sheet, row, column).setCellValue(value);
    }

    default Cell getOrCreateCellAt(Sheet sheet, int row, int column) {
        return sheet.getRow(row).getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    /**
     * Extracts String List from a CSV-type cell
     *
     * @param cell the cell to extract the values from
     * @return the string list from cell values
     */
    default List<String> getCSVListCellValue(Cell cell) {
        List<String> list = new ArrayList<String>();
        String referenceString = getStringCellValue(cell).replaceAll("[\n\r]", EDCIIssuerConstants.STRING_EMPTY);
        if (referenceString != null && !referenceString.equals(EDCIIssuerConstants.STRING_EMPTY)) {
            list = Arrays.asList(referenceString.split(EDCIIssuerConstants.STRING_SEMICOLON));
        }
        return list;
    }

    /**
     * Get a String value from a cell or, if null/empty throw an exception
     *
     * @param cell         the cell to retrieve the value from
     * @param errorMessage the error message for the exception
     * @return the cell string value
     * @throws FileBaseDataException this will stop excel parsing and return an exception
     */

    default String getBaseMandatoryStringCellValue(Cell cell, String errorMessage) throws FileBaseDataException {
        String result = String.valueOf(getSafeCellValue(cell));
        if (!this.getValidator().isEmpty(result)) {
            return result;
        } else {
            throw new FileBaseDataException(errorMessage);
        }
    }

    /**
     * Get string value from cell
     *
     * @param cell the cell to extract the value from
     * @return the string value
     */
    default String getStringCellValue(Cell cell) {
        Object object = getSafeCellValue(cell);

        String returnValue = null;

        if (!this.getValidator().isEmpty(object)) {

            if (object instanceof Number) {
                object = new DecimalFormat("#.##").format(object);
            }
            returnValue = String.valueOf(object);

        }

        return returnValue;
    }

    /**
     * Get double value from cell
     *
     * @param cell the cell to extract the value from
     * @return the double value
     */
    default Double getDoubleCellValue(Cell cell) {
        if (this.getValidator().notEmpty(getSafeCellValue(cell))) {
            return cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    /**
     * Get boolean value from cell
     *
     * @param cell the cell to extract the value from
     * @return the boolean value
     */
    default Boolean getBooleanCellValue(Cell cell) {
        if (!getStringCellValue(cell).equals(EDCIIssuerConstants.STRING_EMPTY)) {
            return Boolean.valueOf(getStringCellValue(cell));
        } else {
            return false;
        }
    }

    /**
     * Get date value from cell
     *
     * @param cell the cell to extract the value from
     * @return the boolean value
     */
    default Date getDateCellValue(Cell cell) {
        Date date = null;

        try {
            date = cell.getDateCellValue();
        } catch (Exception e) {
            throw new FileBaseDataException(MessageKeys.Exception.XLS.FILE_EXCEL_DATE_FORMAT_ERROR, String.valueOf(cell.getRowIndex()), String.valueOf(cell.getColumnIndex()), cell.getSheet().getSheetName());
        }

        return date;
    }


    default Object getSafeCellValue(CellType cellType, Cell cell) {
        Object object;
        switch (cellType) {
            case STRING:
                object = cell.getStringCellValue();
                break;
            case NUMERIC:
                Double value = cell.getNumericCellValue();
                if (Pattern.matches(EDCIIssuerConstants.XLS_PATTERN_FORCENUMBERASTEXT, cell.getCellStyle().getDataFormatString())) {
                    object = new DecimalFormat("#.##").format(value * 100).concat(EDCIIssuerConstants.STRING_PERCENTAGE);
                } else if (Pattern.matches(EDCIIssuerConstants.XLS_PATTERN_FORCENUMBERASDATE, cell.getCellStyle().getDataFormatString())) {
                    object = cell.getDateCellValue();
                } else {
                    object = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                object = cell.getBooleanCellValue();
                break;
            case BLANK:
                object = EDCIIssuerConstants.STRING_EMPTY;
                break;
            default:
                getLogger().error("[E] - Unsupported cell type, fallback to blank");
                object = EDCIIssuerConstants.STRING_EMPTY;
                break;
        }
        return this.getValidator().isEmpty(object) ? null : object;
    }

    /**
     * Helper method to get a Safe value from cell
     *
     * @param cell the Cell
     * @return the Object safe value
     */
    default Object getSafeCellValue(Cell cell) {
        if(getLogger().isTraceEnabled()) {
            getLogger().trace(String.format("Getting Cell with format : [%s]", cell.getCellStyle().getDataFormatString()));
        }
        Object object = null;
        if (cell.getCellType() == CellType.FORMULA) {
            //object = getSafeCellValue(cell.getCachedFormulaResultType(), cell);
            try {

                CellReference cellReference = new CellReference(cell.getCellFormula());
                String sheetName = getValidator().notEmpty(cellReference.getSheetName()) ? cellReference.getSheetName() : cell.getSheet().getSheetName();
                Cell formulaCell = cell.getSheet().getWorkbook().getSheet(sheetName).getRow(cellReference.getRow()).getCell(cellReference.getCol());
                object = getSafeCellValue(formulaCell);
                //Too many use cases to catch specific exceptions, null value should be OK if exception thrown
            } catch (Exception e) {
                if(getLogger().isTraceEnabled()) {
                    getLogger().trace(String.format("Could not create cell Reference from formula, tyring cachedType [%s] -> [%s]", cell.getCellFormula(), getCellInfo(cell)));
                }
                object = getSafeCellValue(cell.getCachedFormulaResultType(), cell);
                return object;
            }
        } else {
            object = getSafeCellValue(cell.getCellType(), cell);
        }
        if (this.getValidator().notEmpty(object)) {
            if(getLogger().isTraceEnabled()) {
                getLogger().trace(String.format("Value extracted from cell : [%s]", String.valueOf(object)));
                getLogger().trace(String.format("Instance extracted from cell: [%s]", object.getClass().getName()));
            }
        }
        return object;
    }

    default String getColumnStringValue(int index) {
        return CellReference.convertNumToColString(index);
    }

    default int getColumnIndex(String columnName) {
        return CellReference.convertColStringToIndex(columnName);
    }

    /**
     * Build an error message pointint to the excel position
     *
     * @param cell the cell where error was thrown
     * @return the string message indicating the cell
     */
    default String getCellInfo(Cell cell) {
        return (String.format("row [%d] of sheet [%s] in column [%s] : cell{%d,%s}", cell.getRowIndex() + 1, cell.getSheet().getSheetName(), getColumnStringValue(cell.getColumnIndex()), cell.getRowIndex() + 1, getColumnStringValue(cell.getColumnIndex())));
    }
}
