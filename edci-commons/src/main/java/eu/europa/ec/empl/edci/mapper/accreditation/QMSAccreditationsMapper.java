package eu.europa.ec.empl.edci.mapper.accreditation;

import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import eu.europa.ec.empl.edci.mapper.PresentationCommonsMapper;
import eu.europa.ec.empl.edci.model.qmsaccreditation.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PresentationCommonsMapper.class})
public interface QMSAccreditationsMapper {

    public List<AccreditationDTO> toAccreditationDTOList(List<QMSAccreditationDTO> qmsAccreditationDTOS);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "identifiers", target = "identifier"),
            @Mapping(source = "type", target = "accreditationType"),
            @Mapping(source = "titles", target = "title"),
            @Mapping(source = "accreditedOrganization", target = "organization"),
            @Mapping(source = "limitFields", target = "limitField"),
            @Mapping(source = "limitEQFLevels", target = "limitEqfLevel"),
            @Mapping(source = "limitJurisdictions", target = "limitJurisdiction"),
            @Mapping(source = "accreditingAgent", target = "accreditingAgent"),
            @Mapping(source = "issuedDate", target = "issueDate")
    })
    public AccreditationDTO toAccreditationDTO(QMSAccreditationDTO accreditationDTO);

    @Mappings({
            @Mapping(source = "value", target = "content"),
            @Mapping(source = "schemeID", target = "identifierSchemeId"),
            @Mapping(source = "schemeAgencyName", target = "identifierSchemeName"),
            @Mapping(source = "schemeAgencyID", target = "identifierSchemeAgencyName")
    })
    public Identifier toIdentifier(QMSIdentifierDTO qmsIdentifier);

    @Mappings({
            @Mapping(source = "value", target = "content"),
            @Mapping(source = "schemeID", target = "identifierSchemeId"),
            @Mapping(source = "schemeAgencyName", target = "identifierSchemeName"),
            @Mapping(source = "schemeAgencyID", target = "identifierSchemeAgencyName"),
            @Mapping(source = "spatialId", target = "spatialId")
    })
    public LegalIdentifier toIdentifier(QMSLegalIdentifierDTO qmsIdentifier);


    public Score toScore(QMSScoreDTO qmsScoreDTO);

    @Mappings({
            @Mapping(source = "registration", target = "legalIdentifier", qualifiedByName = "toIdentifier"),
            @Mapping(source = "vatIdentifiers", target = "vatIdentifier"),
            @Mapping(source = "taxIdentifiers", target = "taxIdentifier"),
            @Mapping(source = "types", target = "type"),
            @Mapping(source = "prefLabels", target = "preferredName"),
            @Mapping(source = "alternativeNames", target = "alternativeName"),
            @Mapping(source = "localizedHomepages", target = "homepage"),
            @Mapping(source = "locations", target = "hasLocation"),
            @Mapping(source = "contactPoints", target = "contactPoint"),
            @Mapping(source = "logo", target = "logo")
    })
    public OrganizationDTO toOrganizationDTO(QMSOrganizationDTO qmsOrganizationDTO);

    public MediaObject toMediaObject(QMSMediaObjectDTO qmsMediaObjectDTO);

    public List<Code> toCodeListFromQMSCode(List<QMSCodeDTO> qmsCodeDTOS);

    public List<WebDocumentDTO> toWebDocumentList(List<URL> urls);

    default WebDocumentDTO toWebDocumentDTO(URL url) {
        WebDocumentDTO webDocumentDTO = new WebDocumentDTO();
        webDocumentDTO.setId(url);
        return webDocumentDTO;
    }

    public Code toCode(QMSCodeDTO qmsCodeDTO);

    @Mappings({
            @Mapping(source = "url", target = "id"),
            @Mapping(source = "titles", target = "title")
    })

    public WebDocumentDTO toWebDocumentDTO(QMSWebDocumentDTO qmsWebDocumentDTO);

    @Mappings({
            @Mapping(source = "identifiers", target = "identifier"),
            @Mapping(source = "names", target = "geographicName"),
            @Mapping(source = "fullAddress", target = "hasAddress"),
            @Mapping(source = "descriptions", target = "description")

    })
    public LocationDTO toLocationDTO(QMSLocationDTO qmsLocationDTO);

    @Mappings({
            @Mapping(source = "notes", target = "note"),
            @Mapping(source = "descriptions", target = "description"),
            @Mapping(source = "addresses", target = "postalAddress"),
            @Mapping(source = "phones", target = "phone"),
            @Mapping(source = "mailBoxes", target = "email"),
            @Mapping(source = "webResources", target = "contactForm")
    })
    public ContactPoint toContactPoint(QMSContactPointDTO qmsContactPoint);

    @Mappings({
            @Mapping(source = "uri", target = "id")
    })
    public MailboxDTO toMailBoxDTO(QMSMailBoxDTO qmsMailBoxDTO);

    @Mappings({
            @Mapping(source = "url", target = "id")
    })
    public InteractiveWebResourceDTO toInteractiveWebResourceDTO(QMSWebDocumentDTO qmsWebDocumentDTO);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "identifier", source = "identifiers"),
            @Mapping(target = "learningOpportunityType", source = "learningOpportunityTypes"),
            @Mapping(target = "alternativeLabel", source = "alternativeLabels"),
            @Mapping(target = "additionalNote", source = "additionalNotes"),
            @Mapping(target = "homepage", source = "homepages"),
            @Mapping(target = "supplementaryDocument", source = "supplementaryDocuments"),
            @Mapping(target = "iscedFCode", source = "iscedFCodes"),
            @Mapping(target = "educationSubject", source = "educationSubjects"),
            @Mapping(target = "educationLevel", source = "educationLevels"),
            @Mapping(target = "language", source = "languages"),
            @Mapping(target = "mode", source = "modes"),
            @Mapping(target = "targetGroup", source = "targetGroups"),
            @Mapping(target = "hasAccreditation", source = "accreditations")
    })
    public QualificationDTO toQualificationDTO(QMSQualificationDTO qmsQualificationDTO);

    public List<Note> toNoteList(List<QMSNoteDTO> qmsNoteDTOS);

    default Note toNote(QMSNoteDTO qmsNoteDTO) {
        Note note = new Note();
        note.setTopic(qmsNoteDTO.getTopic());
        note.setContents(this.toContentList(qmsNoteDTO.getContents()));
        return note;
    }

    default List<Content> toContentList(List<QMSLabelDTO> qmsLabelDTOS) {
        if (qmsLabelDTOS == null) {
            return null;
        } else {
            return qmsLabelDTOS.stream().map(qmsLabelDTO -> new Content(qmsLabelDTO.getValue(), qmsLabelDTO.getLanguage())).collect(Collectors.toList());
        }
    }

    default URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    default URI toURI(UUID id) {
        try {
            return new URI(id.toString());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Named("toIdentifier")
    default Identifier toIdentifier(URI uri) {
        if (uri == null) return null;
        Identifier identifier = new Identifier();
        identifier.setContent(uri.toString());
        return identifier;
    }

    default LegalIdentifier toLegalIdentifier(URI uri) {
        if (uri == null) return null;
        LegalIdentifier legalIdentifier = new LegalIdentifier();
        legalIdentifier.setContent(uri.toString());
        return legalIdentifier;
    }


    default List<Text> toTextList(List<QMSLabelDTO> qmsLabelDTOS) {
        if (qmsLabelDTOS == null) return null;
        List<Text> textList = new ArrayList<>();
        textList.add(this.toText(qmsLabelDTOS));
        return textList;
    }

    default List<Note> toNodeList(List<QMSLabelDTO> qmsLabelDTOS) {
        if (qmsLabelDTOS == null) return null;
        List<Note> noteList = new ArrayList<>();
        noteList.add(this.toNote(qmsLabelDTOS));
        return noteList;
    }

    default PhoneDTO toPhone(String phone) {
        if (phone == null) return null;
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setPhoneNumber(phone);
        return phoneDTO;
    }

    default List<AddressDTO> toSingleAddress(List<QMSLabelDTO> qmsLabelDTOS) {
        if (qmsLabelDTOS == null) return null;
        List<AddressDTO> addressDTOS = new ArrayList<>();
        addressDTOS.add(this.toAddressDTO(qmsLabelDTOS));
        return addressDTOS;
    }

    default AddressDTO toAddressDTO(List<QMSLabelDTO> qmsLabelDTOS) {
        if (qmsLabelDTOS == null) return null;
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setFullAddress(this.toNote(qmsLabelDTOS));
        return addressDTO;
    }

    default Code toCode(URI uri) {
        if (uri == null) return null;
        Code code = new Code();
        code.setUri(uri.toString());
        return code;
    }

    public List<Code> toCodeList(List<URI> uris);

    default Text toText(List<QMSLabelDTO> qmsLabelDTOList) {
        if (qmsLabelDTOList == null) return null;
        Text text = new Text();
        qmsLabelDTOList.stream().forEach(label -> text.addContent(label.getValue(), label.getLanguage()));
        return text;
    }

    default Note toNote(List<QMSLabelDTO> qmsLabelDTOList) {
        if (qmsLabelDTOList == null) return null;
        Note note = new Note();
        qmsLabelDTOList.stream().forEach(label -> note.setContent(label.getLanguage(), label.getValue()));
        return note;
    }

    default Code toCode(String string) {
        if (string == null) return null;
        return new Code(string);
    }

}
