package eu.europa.ec.empl.edci.mapper.commons;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import org.mapstruct.Mapper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface StringDateMapping {

    static SimpleDateFormat formatterFull = new SimpleDateFormat(EDCIConstants.DATE_ISO_8601); //ISO_8601
    static DateTimeFormatter formatterLocal = DateTimeFormatter.ofPattern(EDCIConstants.DATE_LOCAL); //ISO_8601

    default String dateToString(Date date) {

        if (date == null) {
            return null;
        }

        return formatterFull.format(date);

    }

    default Date stringToDate(String dateStr) {

        if (dateStr == null || dateStr.length() <= 0) {
            return null;
        }

        Date returnValue = null;
        try {
            returnValue = formatterFull.parse(dateStr);
        } catch (Exception e) {
            throw new EDCIBadRequestException().addDescription("Date format not valid: " + dateStr);
        }

        return returnValue;

    }

    default String localDateToString(LocalDate date) {

        if (date == null) {
            return null;
        }

        return date.format(formatterLocal);

    }

    default LocalDate stringToLocalDate(String dateStr) {

        if (dateStr == null) {
            return null;
        }

        LocalDate returnValue = null;
        try {
            returnValue = LocalDate.parse(dateStr, formatterLocal);
        } catch (Exception e) {
            throw new EDCIBadRequestException().addDescription("Date format not valid: " + dateStr);
        }

        return returnValue;

    }

}