package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import org.joda.time.Period;
import org.mapstruct.Mapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface VariousObjectsMapper {

    default String toBase64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    default byte[] stringToBytes(String content) { return content.getBytes(); }

    default Period getDurationHours(Long hours) {
        if (hours == null) {
            return null;
        }

        return Period.hours(Integer.valueOf(hours.toString()));
    }

    default Long getHours(Period hoursDuration) {

        if (hoursDuration == null) {
            return null;
        }

        return Long.valueOf(hoursDuration.getHours());
    }

    default Period getDurationMonths(Integer months) {

        if (months == null) {
            return null;
        }

        return Period.months(months);
    }

    default Integer getMonths(Period months) {

        if (months == null) {
            return null;
        }

        return months.getMonths();
    }

    default Object getObjectFromFloat(Float number) {
        return number;
    }

    default ZonedDateTime toZonedDateTime(Date date) {
        if(date == null) {
            return null;
        }

        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
    }

    default Date toDate(ZonedDateTime date) {
        if(date == null) {
            return null;
        }

        return Date.from(date.toInstant());
    }

    default URI stringToURI(String url) {

        if (url == null) {
            return null;
        }

        return URI.create(url);
    }

    default String URIToString(URI url) {

        if (url == null) {
            return null;
        }

        return url.toString();
    }

    default URI urltoURI(URL url) {

        if (url == null) {
            return null;
        }

        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    default URL uritoURL(URI uri) {

        if (uri == null) {
            return null;
        }

        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            return null;
        }

    }

}
