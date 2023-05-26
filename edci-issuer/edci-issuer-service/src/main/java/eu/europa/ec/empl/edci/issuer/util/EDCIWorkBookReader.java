package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("DynamicWorkBookUtil")
public class EDCIWorkBookReader implements IWorkBookReader {

    private static final Logger logger = LogManager.getLogger(EDCIWorkBookReader.class);


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
    public Validator validator;


    public Cell getCellAt(Sheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            sheet.createRow(rowIndex);
            row = sheet.getRow(rowIndex);
        }
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (row.getRowStyle() != null) {
            cell.setCellStyle(row.getRowStyle());
        }
        return cell;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}


