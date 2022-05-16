package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IWorkBookService {

    public Workbook createWorkBook(InputStream inputStream) throws IOException, InvalidFormatException, FileBaseDataException;

    public abstract boolean isValidFormat(Workbook workBook);

    public abstract List<EuropassCredentialDTO> parseCredentialData(Workbook workBook);

    public List<RecipientDataDTO> parseRecipientsData(Workbook workbook);

}
