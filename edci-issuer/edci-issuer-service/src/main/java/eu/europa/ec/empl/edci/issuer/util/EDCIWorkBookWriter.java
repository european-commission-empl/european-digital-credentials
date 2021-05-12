package eu.europa.ec.empl.edci.issuer.util;


import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessages;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.AssessmentsListIssueDTO;
import eu.europa.ec.empl.edci.issuer.common.model.ColumnInfo;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component()
public class EDCIWorkBookWriter {

    @Autowired
    private EDCIWorkBookReader edciWorkBookReader;

    @Autowired
    private EDCIWorkBookUtil edciWorkBookUtil;

    @Autowired
    private EDCIMessageService edciMessageService;


    public void setOrCreateCellValue(Sheet sheet, int row, int column, String value) {
        edciWorkBookReader.getOrCreateCellAt(sheet, row, column).setCellValue(value);
    }

    /**
     * Adds columns for Grades at the end of the sheet. Used with recipient XLS templates.
     *
     * @param assessmentsListIssueDTO
     * @param recipientSheet
     */
    public void writeRecipientXLSGradeColumns(AssessmentsListIssueDTO assessmentsListIssueDTO, Sheet recipientSheet) {
        int lastColumnIndex = edciWorkBookUtil.getLastColumnInfoIndex(recipientSheet);
        int currentIndex = 0;
        //Get necessary Styles
        CellStyle labelCellStyle = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.LABEL_ROW, XLS.FIRST_MANDATORY_LABEL_RECIPIENTS).getCellStyle();
        CellStyle descriptionCellStyle = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DESCRIPTION_ROW, XLS.FIRST_MANDATORY_LABEL_RECIPIENTS).getCellStyle();
        CellStyle languageDescriptionCellStyle = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.LANGUAGE_DESCRIPTION_ROW, XLS.FIRST_MANDATORY_LABEL_RECIPIENTS).getCellStyle();
        CellStyle valueCellStyle = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DATA_ROW - 1, XLS.FIRST_MANDATORY_LABEL_RECIPIENTS).getCellStyle();
        CellStyle defaultValueCellStyle = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DEFAULT_VALUE_ROW, XLS.FIRST_DEFAULT_LABEL_RECIPIENTS).getCellStyle();
        //Loop through all assessments
        for (Map.Entry<Long, String> entry : assessmentsListIssueDTO.getAssessments().entrySet()) {
            //start after last column written
            int currentColumn = lastColumnIndex + currentIndex;
            //Get all Necessary Cells
            Cell classCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.CLASS_ROW, currentColumn);
            Cell fieldCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.FIELD_ROW, currentColumn);
            Cell typeCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.TYPE_ROW, currentColumn);
            Cell labelCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.LABEL_ROW, currentColumn);
            Cell descriptionCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DESCRIPTION_ROW, currentColumn);
            Cell languageDescriptionCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.LANGUAGE_DESCRIPTION_ROW, currentColumn);
            Cell defaultValueCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DEFAULT_VALUE_ROW, currentColumn);
            Cell valueCell = edciWorkBookReader.getOrCreateCellAt(recipientSheet, XLS.DATA_ROW - 1, currentColumn);
            //Set values
            classCell.setCellValue("Person");
            fieldCell.setCellValue(entry.getKey());
            typeCell.setCellValue(XLS.PARSE_TYPE.GRADES_ASSOCIATION.value());
            labelCell.setCellValue(entry.getValue());
            //Set styles
            labelCell.setCellStyle(labelCellStyle);
            descriptionCell.setCellStyle(descriptionCellStyle);
            languageDescriptionCell.setCellStyle(languageDescriptionCellStyle);
            defaultValueCell.setCellStyle(defaultValueCellStyle);
            valueCell.setCellStyle(valueCellStyle);
            recipientSheet.autoSizeColumn(currentColumn);
            currentIndex++;
        }
    }

    public void replaceRecipientTemplateHeaders(Sheet recipientSheet, String lang) {
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.LABEL_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_LABEL));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.LABEL_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_DEFINITION));
        this.setOrCreateCellValue(recipientSheet, XLS.LANGUAGE_DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.LABEL_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_LANGUAGE));
        this.setOrCreateCellValue(recipientSheet, XLS.DEFAULT_VALUE_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.LABEL_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_DEFAULT));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.GIVENNAME_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_GIVENNAME, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.FAMILYNAME_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_FAMILYNAME, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.DATEOFBIRTH_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_DATEOFBIRTH, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.DATEOFBIRTH_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_DATEOFBIRTH_DESCRIPTION));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.GENDER_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_GENDER, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.NATIONALID_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_NATIONALID, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.NATIONALID_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_NATIONALID_NUMBER_DESCRIPTION, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.NATIONALID_COLUMN) + 1, edciMessageService.getMessage(EDCIIssuerMessages.HEADER_NATIONALID_COUNTRY_DESCRIPTION, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.PLACEOFBIRTH_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_PLACEOFBIRTH, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.PLACEOFBIRTH_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_PLACEOFBIRTH_DESCRIPTION, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFCITIZENSHIP_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_COUNTRYOFCITIZENSHIP, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFCITIZENSHIP_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_COUNTRYOFCITIZENSHIP_DESCRIPTION, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.EMAILADDRESS_COLMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_EMAILADDRESS, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.WALLETADDRESS_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_WALLETADDRESS, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.LABEL_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFRESIDENCE_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_COUNTRYOFRESIDENCE, lang));
        this.setOrCreateCellValue(recipientSheet, XLS.DESCRIPTION_ROW, edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFRESIDENCE_COLUMN), edciMessageService.getMessage(EDCIIssuerMessages.HEADER_COUNTRYOFRESIDENCE_DESCRIPTION, lang));

        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.LABEL_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.GIVENNAME_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.FAMILYNAME_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.DATEOFBIRTH_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.GENDER_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.NATIONALID_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.PLACEOFBIRTH_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFCITIZENSHIP_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.NATIONALID_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.EMAILADDRESS_COLMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.WALLETADDRESS_COLUMN));
        recipientSheet.autoSizeColumn(edciWorkBookReader.getColumnIndex(XLS.Recipient.COUNTRYOFRESIDENCE_COLUMN));
    }

    public void replaceWorkbookLanguageFields(Workbook workbook, String lang) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (!XLS.UNSCANNED_SHEETS.contains(sheet.getSheetName())) {
                replaceWorkbookLanguageFields(sheet, lang);
            }
        }
    }

    public void replaceWorkbookLanguageFields(Sheet sheet, String lang) {
        int columnInfoEndIndex = edciWorkBookUtil.getLastColumnInfoIndex(sheet);
        Map<Integer, ColumnInfo> columnInfoMap = edciWorkBookReader.getSheetColumnInfo(sheet, columnInfoEndIndex);
        for (Map.Entry<Integer, ColumnInfo> columnInfoEntry : columnInfoMap.entrySet()) {
            if (edciWorkBookUtil.isLanguageField(columnInfoEntry.getValue())) {
                Cell langCell = edciWorkBookReader.getOrCreateCellAt(sheet, XLS.LANGUAGE_ROW, columnInfoEntry.getKey());
                langCell.setCellValue(lang);
            }
        }
    }

}
