package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.WebResourceDTO;
import eu.europa.ec.empl.edci.model.view.fields.ContactPointFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LocationFieldView;
import eu.europa.ec.empl.edci.model.view.fields.MailboxFieldView;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Period;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BaseMapper.class})
public interface PresentationCommonsMapper {


    public static final Logger logger = LogManager.getLogger(PresentationCommonsMapper.class);
    public static final Validator val = new Validator();

    //TODO -> ADAPT TO NEW TYPES IN EUROPOEANDIGITALCREDENTIALVIEW, SHOULD BE CREATED IN VIEWER, THEN MOVE THIS MAPPER TO VIEWER

    default String toStringIdentifier(Identifier identifier) {
        if (identifier == null) {
            return null;
        }

        return identifier.toString();
    }


    default String toStringPhone(PhoneDTO phone) {
        if (phone == null) {
            return null;
        }

        return phone.getPhoneNumber();
    }

    default String toStringMail(MailboxDTO mail) {
        if (mail == null || mail.getId() == null) {
            return null;
        }

        return mail.getId().toString();
    }

    default String toStringMail(WebResourceDTO web) {
        if (web == null || web.getId() == null) {
            return null;
        }

        return web.getId().toString();
    }


    default MediaObjectDTO toMediaObject(URI mediaObjectUri) {
        MediaObjectDTO mediaObject = new MediaObjectDTO();
        mediaObject.setId(mediaObjectUri);
        return mediaObject;
    }

    default List<String> toStringList(LiteralMap literalMap) {
        return MultilangFieldUtil.getLiteralStringListOrAny(literalMap, LocaleContextHolder.getLocale().toString());
    }

    @Named("toStringDateFull")
    default String toStringDateFull(ZonedDateTime date) {

        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(EDCIConstants.DATE_FRONT_GMT));

    }

    @Named("toStringDateLocal")
    default String toStringDateLocal(ZonedDateTime date) {

        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(EDCIConstants.DATE_FRONT_LOCAL));

    }

    @Named("toStringMonths")
    default String toStringMonths(Period months) {

        if (months == null) {
            return null;
        }

        return String.valueOf(months.getMonths());
    }

    @Named("toStringHours")
    default String toStringHours(Period hoursDuration) {

        if (hoursDuration == null) {
            return null;
        } else if(hoursDuration.getHours() == 0) {
            return String.valueOf(hoursDuration.getMonths());
        } else {
            return String.valueOf(hoursDuration.getHours());
        }
    }

    LocationFieldView toFieldLocation(LocationDTO location);

    ContactPointFieldView toFieldContactPoint(ContactPointDTO cp);

    default MailboxFieldView toFieldMailbox(MailboxDTO mailboxDTO) {
        MailboxFieldView mailboxFieldView = null;

        if(mailboxDTO != null && mailboxDTO.getId() != null) {
            mailboxFieldView = new MailboxFieldView();
            mailboxFieldView.setId(mailboxDTO.getId().toString().replaceAll(DataModelConstants.Defaults.DEFAULT_MAILTO, ""));
        }

        return mailboxFieldView;
    }

    default LinkFieldView toFieldWebDocument(WebResourceDTO webDocument) {

        LinkFieldView lfv = new LinkFieldView();
        if (webDocument == null) {
            return null;
        }

        if (webDocument.getContentURL() != null) lfv.setLink(webDocument.getContentURL());
        if (webDocument.getTitle() != null)
            lfv.setTitle(MultilangFieldUtil.getLiteralStringListOrAny(webDocument.getTitle(), LocaleContextHolder.getLocale().toString()));


        return lfv;

    }

}
