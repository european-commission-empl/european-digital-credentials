package eu.europa.ec.empl.edci.mapper.commons;

import org.joda.time.Period;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DurationLongMapper {

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
}
