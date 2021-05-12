package eu.europa.ec.empl.edci.issuer.web.exception;

import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.rest.ExceptionControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionIssuerControllerAdvice extends ExceptionControllerAdvice {

    //    private static final Logger logger = LoggerFactory.getLogger(ExceptionIssuerControllerAdvice.class);
    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExceptionIssuerControllerAdvice.class);

    //XLS errors
    @ExceptionHandler(FileBaseDataException.class)
    public ResponseEntity<?> handleEDCIException(HttpServletRequest req, FileBaseDataException ex) {
        ApiErrorMessage error = ex.toApiErrorMessage(messageSource, req);
        //ToDo-> localize/parametrize excel error messages
        String message = error.getMessage();
        if (ex.hasAllCellInfo()) {
            message = message.concat(" - ").concat(messageSource.getMessage(MessageKeys.Exception.XLS.FILE_EXCEL_CELL_ERROR_MESSAGE, ex.getSheetName(), ex.getRow(), ex.getColumn()));
        }
        error.setMessage(message);
        logger.error(ex.getMessage(), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                ex.getHttpStatus());
    }

}