package eu.europa.ec.empl.edci.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.exception.security.EDCIForbiddenException;
import eu.europa.ec.empl.edci.exception.security.EDCIUnauthorizedException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.log4j.Logger;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ClientErrorException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;

//import org.springframework.web.servlet.NoHandlerFoundException;

public class ExceptionControllerAdvice {

    public static final Logger logger = Logger.getLogger(ExceptionControllerAdvice.class);

    @Autowired
    protected EDCIMessageService messageSource;

    //Default errors
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleOtherExceptions(HttpServletRequest req, Exception ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setPath(req.getPathInfo());
        logger.error(error.getPath() + " Error: " + ex.getMessage(), ex);
        return generateResponse(error, prepareHttpHeadersForJSONException(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Application errors
    @ExceptionHandler(EDCIException.class)
    public ResponseEntity<?> handleEDCIException(HttpServletRequest req, EDCIException ex) {
        ApiErrorMessage error = ex.toApiErrorMessage(messageSource, req);
        logger.error(error.getPath() + " Error: " + error.getCode() + " - " + error.getMessage()
                + (ex.getDescription() != null ? " Description: " + ex.getDescription() : ""), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                ex.getHttpStatus());
    }

    //API Rest external calls errors
    @ExceptionHandler(EDCIRestException.class)
    public ResponseEntity<?> handleRestException(HttpServletRequest req, EDCIRestException ex) {
        ApiErrorMessage error = ex.toApiErrorMessage(req);
        logger.error(error.getPath() + " Error: " + error.getCode() + " - " + error.getMessage(), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                ex.getHttpStatus());
    }

    //Client error exceptions
    @ExceptionHandler(ClientErrorException.class)
    @ResponseBody
    public ResponseEntity<?> handleClientErrorExceptions(HttpServletRequest req, ClientErrorException ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        try {
            error.setMessage(ex.getResponse().getStatusInfo().getReasonPhrase());
        } catch (Exception e) {
            //Do nothing
        }
        error.setPath(req.getPathInfo());
        logger.error(error.getPath() + " Error: " + ex.getMessage(), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                HttpStatus.valueOf(ex.getResponse() != null ? ex.getResponse().getStatus() : 500));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissinParametersExceptions(HttpServletRequest req, MissingServletRequestParameterException ex) {
        EDCIBadRequestException edciBadRequestException = new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.MISSING_PARAMETER, ex.getParameterName());
        return this.handleEDCIException(req, edciBadRequestException.setCause(ex));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(HttpServletRequest req, MissingServletRequestPartException ex) {
        EDCIBadRequestException edciBadRequestException = new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.MISSING_REQUEST_PART, ex.getRequestPartName());
        return this.handleEDCIException(req, edciBadRequestException.setCause(ex));
    }

    @ExceptionHandler({JsonMappingException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handleBadRequestExceptions(HttpServletRequest req, Exception ex) {
        logger.error(ex.getMessage(), ex);
        return handleEDCIException(req, new EDCIBadRequestException().setCause(ex));
    }


    //Database errors
    @ExceptionHandler({TransactionException.class, DataAccessException.class, PersistenceException.class})
    public ResponseEntity<?> handleTransactionException(HttpServletRequest req, Exception ex) {

        ResponseEntity<?> resp = null;

        Throwable t = ex;
        boolean found = false;
        while (t.getCause() != null && !found) {
            t = t.getCause();
            if (t.getCause() instanceof DatabaseException) {
                resp = handleDatabaseException(req, (DatabaseException) t.getCause());
                found = true;
            } else if (t.getCause() instanceof SQLException) {
                resp = handleSQLException(req, (SQLException) t.getCause());
                found = true;
            }
        }

        if (resp == null) {
            ApiErrorMessage error = new ApiErrorMessage();
            error.setPath(req.getPathInfo());
            error.setCode(ErrorCode.DATABASE_ERROR.getCode());
            logger.error(ex.getMessage(), ex);
            resp = generateResponse(error,
                    prepareHttpHeadersForJSONException(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return resp;
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(HttpServletRequest req, SQLException ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setPath(req.getPathInfo());
        error.setCode(ErrorCode.DATABASE_ERROR.getCode());
        logger.error(ex.getMessage(), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<?> handleDatabaseException(HttpServletRequest req, DatabaseException ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setPath(req.getPathInfo());
        if (ex.getCause() instanceof SQLIntegrityConstraintViolationException
                && ex.getDatabaseErrorCode() == 2292) { //If it's a FK dependendency error while deleting some resource
            error.setMessage(messageSource.getMessage("entity.delete.error.has.references"));
        }
        error.setCode(ErrorCode.DATABASE_ERROR.getCode());
        logger.error(ex.getMessage(), ex);
        return generateResponse(error,
                prepareHttpHeadersForJSONException(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //No endpoint found error
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleNotKnownExceptions(HttpServletRequest req, NoHandlerFoundException ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setCode(ErrorCode.ENDPOINT_NOT_FOUND.getCode());
        error.setMessage("Endpoint not implemented");
        error.setPath(req.getPathInfo());
        logger.error(ex.getMessage(), ex);
        return generateResponse(error, prepareHttpHeadersForJSONException(), HttpStatus.I_AM_A_TEAPOT); //TODO: 418, 501?
    }

    public <T> ResponseEntity<?> generateResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        return new ResponseEntity<T>(body, headers, status);
    }

    protected HttpHeaders prepareHttpHeadersForJSONException() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return httpHeaders;
    }

    @ResponseBody
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptableException(HttpServletRequest req, HttpMediaTypeNotAcceptableException ex) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setPath(req.getPathInfo());
        logger.error(ex.getMessage(), ex);
        return (ResponseEntity<String>) generateResponse(getJson(error), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Authentication errors

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<?> handleAccesDeniedException(HttpServletRequest req, AccessDeniedException ex) {
        //Assume 401
        ApiErrorMessage error = new EDCIUnauthorizedException(ErrorCode.UNAUTHORIZED).toApiErrorMessage(messageSource, req);
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        try {
            //Translate and check for 403
            ResponseEntity<?> result = new DefaultWebResponseExceptionTranslator().translate(ex);
            if (result.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                error = new EDCIForbiddenException(ErrorCode.FORBIDDEN).toApiErrorMessage(messageSource, req);
                httpStatus = HttpStatus.FORBIDDEN;
            }
        } catch (Exception e) {
            logger.error("Error translating exception", e);
        }
        error.setPath(req.getPathInfo());
        logger.error(error.getPath() + " Error: " + ex.getMessage(), ex);
        return generateResponse(error, prepareHttpHeadersForJSONException(), httpStatus);
    }

    @ExceptionHandler(EDCIForbiddenException.class)
    @ResponseBody
    public ResponseEntity<?> handleForbiddenException(HttpServletRequest req, EDCIForbiddenException ex) {
        ApiErrorMessage error = ex.toApiErrorMessage(messageSource, req);
        logger.error(error.getPath() + " Error: " + error.getCode() + " - " + error.getMessage()
                + (ex.getDescription() != null ? " Description: " + ex.getDescription() : ""), ex);
        return generateResponse(error, prepareHttpHeadersForJSONException(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EDCIUnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<?> handleUnauthorizedException(HttpServletRequest req, EDCIUnauthorizedException ex) {
        ApiErrorMessage error = ex.toApiErrorMessage(messageSource, req);
        logger.error(error.getPath() + " Error: " + error.getCode() + " - " + error.getMessage()
                + (ex.getDescription() != null ? " Description: " + ex.getDescription() : ""), ex);
        return generateResponse(error, prepareHttpHeadersForJSONException(), HttpStatus.UNAUTHORIZED);
    }

    private String getJson(Object exceptionResponse) {
        String jsonString = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setDateFormat(new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT));
            jsonString = mapper.writeValueAsString(exceptionResponse);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return jsonString;
    }
}