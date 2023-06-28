package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.mapstruct.Mapper;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseMapper {

    default String toStringFromConceptDTO(ConceptDTO conceptDTO) {
        return conceptDTO != null ? conceptDTO.toString() : "";
    }

    default String toStringFromURI(URI uri) {
        return uri != null ? uri.toString() : "";
    }

    default String toStringFromPeriod(Period period) {
        return period != null ? period.toString() : "";
    }

    default String toStringBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    default byte[] toBytesString(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        return Base64.getDecoder().decode(str);
    }

    default String dateToString(ZonedDateTime date) {

        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(EDCIConstants.DATE_ISO_8601));

    }

    default ZonedDateTime stringToDate(String dateStr) {

        if (dateStr == null || dateStr.length() <= 0) {
            return null;
        }

        ZonedDateTime returnValue = null;
        try {
            returnValue = ZonedDateTime.parse(dateStr);
        } catch (Exception e) {
            throw new EDCIBadRequestException().addDescription("Date format not valid: " + dateStr);
        }

        return returnValue;

    }

    public abstract List<LinkFieldView> toListFieldView(List<ConceptDTO> conceptDTOS);

    default LinkFieldView toLinkFieldView(ConceptDTO conceptDTO) {
        LinkFieldView linkFieldView = new LinkFieldView();
        linkFieldView.setLink(conceptDTO.getId());
        linkFieldView.setTitle(MultilangFieldUtil.getLiteralStringListOrAny(conceptDTO.getPrefLabel(), LocaleContextHolder.getLocale().toString()));
        linkFieldView.setTargetFramework(conceptDTO.getInScheme().getId().toString());
        linkFieldView.setTitleAvailableLangs(conceptDTO.getPrefLabel());
        return linkFieldView;
    }
}
