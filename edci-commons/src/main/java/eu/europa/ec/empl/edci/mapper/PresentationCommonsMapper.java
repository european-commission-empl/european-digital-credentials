package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import eu.europa.ec.empl.edci.datamodel.view.*;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.joda.time.Period;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PresentationCommonsMapper {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PresentationCommonsMapper.class);

    static SimpleDateFormat formatterDateFull = new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT);
    static SimpleDateFormat formatterDateOnly = new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL);

    public static final Validator val = new Validator();

    default String toStringText(Text title) {
        if (title == null) {
            return null;
        }

        return title.getStringContent();
    }

    default Map<String,String> toMapText(Text title) {
        if (title == null) {
            return null;
        }

        Map<String, String> returnMap = new HashMap<>();
        title.getContents().forEach(c-> returnMap.put(c.getLanguage(), c.getContent()));

        return returnMap;
    }

    default String toStringNote(Note note) {

        if (note == null) {
            return null;
        }

        return note.getStringContent(LocaleContextHolder.getLocale().getLanguage());

    }

    default NoteFieldView toFieldNote(Note note) {

        NoteFieldView noteField = new NoteFieldView();

        if (note == null) {
            return null;
        }

        noteField.setTopic(note.getTopic());
        noteField.setContent(note.getStringContent(LocaleContextHolder.getLocale().getLanguage()));

        return noteField;
    }

    default String toStringIdentifier(Identifier identifier) {
        if (identifier == null) {
            return null;
        }

        return identifier.getContent();
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

    default String toStringMail(WebDocumentDTO web) {
        if (web == null || web.getId() == null) {
            return null;
        }

        return web.getId().toString();
    }

    default String toStringUri(URI uri) {

        if (uri == null) {
            return null;
        }

        return uri.toString();
    }

    default String toStringScore(Score score) {

        if (score == null) {
            return null;
        }

        return score.getContent();
    }

    default String toStringCode(Code code) {

        if (code == null || code.getTargetName() == null) {
            return null;
        }

        return code.getTargetName().getStringContent(LocaleContextHolder.getLocale().getLanguage());

    }

    default LinkFieldView toLinkCode(Code code) {

        if (code == null || code.getTargetName() == null) {
            return null;
        }

        LinkFieldView lfv = new LinkFieldView();

        try {
            lfv.setLink(new URL(code.getUri()));
            lfv.setTitle(code.getTargetName().getStringContent(LocaleContextHolder.getLocale().getLanguage()));
            code.getTargetName().getContents().forEach(c-> lfv.getTitleAvailableLangs().put(c.getLanguage(), c.getContent()));
        } catch (MalformedURLException e) {
            logger.error("Error mapping linkField. Leaving the object null", e);
        }

        return lfv;

    }

    @Named("toStringDateFull")
    default String toStringDateFull(Date date) {

        if (date == null) {
            return null;
        }

        return formatterDateFull.format(date);

    }

    @Named("toStringDateLocal")
    default String toStringDateLocal(Date date) {

        if (date == null) {
            return null;
        }

        return formatterDateOnly.format(date);

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
        }

        return String.valueOf(hoursDuration.getHours());
    }

    LocationFieldView toFieldLocation(LocationDTO location);

    @AfterMapping
    default void fillLocationAddressFields(LocationDTO location, @MappingTarget LocationFieldView lf) {

        if (location.getHasAddress() != null && !location.getHasAddress().isEmpty()) {
            lf.setAddress(new ArrayList<AddressFieldView>());
            lf.getAddress().addAll(val.getValueNullSafe(() -> location.getHasAddress().stream().map(fa -> new AddressFieldView(toStringNote(fa.getFullAddress()), toStringCode(fa.getCountryCode()))).collect(Collectors.toList())));
        }
    }

    IdentifierFieldView toFieldIdentifier(Identifier identifier);

    IdentifierFieldView toFieldLegalIdentifier(LegalIdentifier identifier);

    ContactPointFieldView toFieldContactPoint(ContactPoint cp);

    @AfterMapping
    default void fillContactPointAddressFields(ContactPoint cp, @MappingTarget ContactPointFieldView cpf) {

        if (cp.getPostalAddress() != null && !cp.getPostalAddress().isEmpty()) {
            cpf.setAddress(new ArrayList<AddressFieldView>());
            cpf.getAddress().addAll(val.getValueNullSafe(() -> cp.getPostalAddress().stream().map(fa -> new AddressFieldView(toStringNote(fa.getFullAddress()), toStringCode(fa.getCountryCode()))).collect(Collectors.toList())));
        }

    }

    MediaObjectFieldView toFieldMediaObject(MediaObject media);

    @AfterMapping
    default void fillMediaObjectFields(MediaObject media, @MappingTarget MediaObjectFieldView mof) {

        if (mof != null && val.isNotNull(() -> media.getContent(), () -> media.getContentType().getUri())) {

            try {
                mof.setBase64Content(new String(Base64.getEncoder().encode(media.getContent())));
                mof.setMimeType(new ControlledListsUtil().getMimeType(media.getContentType().getUri()));
            } catch (Exception e) {
                logger.error("Error mapping mediaObject. Leaving the object null", e);
            }
            if (mof.getBase64Content() == null || mof.getMimeType() == null) {
                mof = null;
                logger.error("Error mapping mediaObject. Leaving the object to null");
            }
        }

    }

    default LinkFieldView toFieldWebDocument(WebDocumentDTO webDocument) {

        LinkFieldView lfv = new LinkFieldView();

        if (webDocument == null) {
            return null;
        }

        lfv.setLink(webDocument.getId());
        lfv.setTitle(toStringText(webDocument.getTitle()));

        return lfv;

    }

}
