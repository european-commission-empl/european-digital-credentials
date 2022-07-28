package eu.europa.ec.empl.edci.exception;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import org.springframework.http.HttpStatus;

public class FileBaseDataException extends EDCIException {

    public int row;
    public String column;
    public String sheetName;

    public FileBaseDataException(String messageKey) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.FILE_BASE_DATA, messageKey);
    }

    public FileBaseDataException(String messageKey, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.FILE_BASE_DATA, messageKey);
        this.setCause(cause);
    }


    public FileBaseDataException(String messageKey, String... messageArgs) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.FILE_BASE_DATA, messageKey, messageArgs);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public boolean hasAllCellInfo() {
        return this.getColumn() != null && this.getRow() != 0 && this.getSheetName() != null;
    }
}
