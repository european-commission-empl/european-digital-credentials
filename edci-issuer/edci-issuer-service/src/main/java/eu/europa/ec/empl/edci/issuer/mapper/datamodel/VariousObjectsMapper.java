package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.model.MailboxDTO;
import eu.europa.ec.empl.edci.datamodel.model.Standard;
import eu.europa.ec.empl.edci.datamodel.model.WebDocumentDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Score;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ScoreDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.StandardDTDAO;
import org.joda.time.Period;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VariousObjectsMapper {

    CodeDTDAO getCodeDTDAO(Code code);

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

    default Score getScore(ScoreDTDAO number) {

        if (number == null) {
            return null;
        }

        Score score = new Score();
        score.setContent(number.getContent());
        score.setScoringSchemeId(number.getScoringSchemeId());

        return score;

    }

    default ScoreDTDAO getScore(Score number) {

        if (number == null) {
            return null;
        }

        ScoreDTDAO score = new ScoreDTDAO();
        score.setContent(number.getContent());
        score.setScoringSchemeId(number.getScoringSchemeId());

        return score;

    }

    default StandardDTDAO mapStandardTODO(Standard identifier) {
        return new StandardDTDAO();
    }

    default Standard mapStandardDTOTODO(StandardDTDAO identifier) {
        return new Standard();
    }


    default Text toTextString(String title, @Context String locale) {

        if (title == null) {
            return null;
        }

        Text text = new Text();
        text.setContent(locale, title);

        return text;
    }

    default String toStringText(Text title, @Context String locale) {
        if (title == null) {
            return null;
        }

        if (title.getContents() == null) {
            return null;
        }

        if (locale == null) {
            return title.getContents().get(0).getContent();
        }

        return title.getContents().stream().filter(c -> locale.equals(c.getLanguage())).findFirst()
                .orElse(title.getContents().get(0)).getContent();

    }

    default List<MailboxDTO> toMailString(String title, @Context String locale) {

        if (title == null) {
            return null;
        }

        List<MailboxDTO> mail = new ArrayList<>();
        mail.add(new MailboxDTO() {{
            setId((StringUtils.hasLength(title) ? URI.create(title.trim()) : null));
        }});

        return mail;
    }

    default String toStringMail(List<MailboxDTO> mail, @Context String locale) {

        if (mail == null || mail.size() == 0) {
            return null;
        }

        return mail.get(0).toString();
    }

    default List<Note> toNoteString(String title, @Context String locale) {

        if (title == null) {
            return null;
        }

        List<Note> noteList = new ArrayList<>();
        noteList.add(new Note() {{
            setContent(locale, title);
        }});

        return noteList;
    }

    default String toStringNote(List<Note> noteList, @Context String locale) {

        if (noteList == null || noteList.size() == 0) {
            return null;
        }

        return noteList.get(0).getStringContent(locale);
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

    default LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    default Date convertToDateViaInstant(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Mappings(
            {@Mapping(target = "content", source = "id")}
    )
    WebDocumentDCDAO webDocumentDTOToWebDocumentDCDAO(WebDocumentDTO webDocumentDTO);

    @Mappings(
            {@Mapping(target = "id", source = "content")}
    )
    WebDocumentDTO webDocumentDCDAOToWebDocumentDTO(WebDocumentDCDAO webDocumentDCDAO);

    default List<AccreditationDTO> uriToAccreditationDTO(URI accreditationId) {
        List<AccreditationDTO> mappingTarget = null;
        if (accreditationId != null) {
            AccreditationDTO accred = new AccreditationDTO();
            accred.setId(accreditationId);
            mappingTarget = new ArrayList<>();
            mappingTarget.add(accred);
        }

        return mappingTarget;
    }

    default URI uriToAccreditationDTO(List<AccreditationDTO> accreditationList) {
        URI mappingTarget = null;
        if (accreditationList != null && !accreditationList.isEmpty()) {
            mappingTarget = accreditationList.get(0).getId();
        }

        return mappingTarget;
    }

}
