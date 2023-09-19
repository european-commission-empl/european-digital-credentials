package eu.europa.ec.empl.edci.mapper.accreditation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface QMSAccreditationsMapper {
    //TODO -> RESOTRE WHEN NEW DATAMODEL ACCREDITAIONS READY
//    public List<AccreditationDTO> toAccreditationDTOList(List<QMSAccreditationDTO> qmsAccreditationDTOS);
//
//    @Mappings({
//            @Mapping(source = "id", target = "id"),
//            @Mapping(source = "identifiers", target = "identifier"),
//            @Mapping(source = "type", target = "dcType"),
//            @Mapping(source = "titles", target = "title"),
//            @Mapping(source = "limitFields", target = "limitField"),
//            @Mapping(source = "limitEQFLevels", target = "limitEQFLevel"),
//            @Mapping(source = "limitJurisdictions", target = "limitJurisdiction"),
//            @Mapping(source = "issuedDate", target = "dateIssued")
//    })
//    public AccreditationDTO toAccreditationDTO(QMSAccreditationDTO accreditationDTO);
//
//    @Mappings({
//            @Mapping(source = "value", target = "id"),
//            @Mapping(source = "schemeAgencyName", target = "schemeName", ignore = true)
//    })
//    public Identifier toIdentifier(QMSIdentifierDTO qmsIdentifier);
//
//    @Mappings({
//            @Mapping(source = "value", target = "id"),
//            @Mapping(source = "schemeAgencyName", target = "schemeName"),
//            @Mapping(source = "spatialId", target = "spatial", ignore = true)
//    })
//    public LegalIdentifier toIdentifier(QMSLegalIdentifierDTO qmsIdentifier);
//
//
//    @Mappings({
//            @Mapping(source = "vatIdentifiers", target = "vatIdentifier"),
//            @Mapping(source = "taxIdentifiers", target = "taxIdentifier"),
//            @Mapping(source = "prefLabels", target = "prefLabel"),
//            @Mapping(source = "alternativeNames", target = "altLabel"),
//            @Mapping(source = "localizedHomepages", target = "homepage", ignore = true),
//            @Mapping(source = "locations", target = "location"),
//            @Mapping(source = "contactPoints", target = "contactPoint"),
//            @Mapping(source = "logo", target = "logo"),
//            @Mapping(source = "registration", target = "registration", ignore = true)
//    })
//    public OrganisationDTO toOrganizationDTO(QMSOrganizationDTO qmsOrganizationDTO);
//
//    public MediaObjectDTO toMediaObject(QMSMediaObjectDTO qmsMediaObjectDTO);
//
//    public List<ConceptDTO> toCodeListFromQMSCode(List<QMSCodeDTO> qmsCodeDTOS);
//
//    @Mappings({
//            @Mapping(source = "uri", target = "id", ignore = true),
//            @Mapping(source = "targetName", target = "prefLabel"),
//            @Mapping(source = "targetNotation", target = "notation")
//    })
//    public ConceptDTO toConcept(QMSCodeDTO qmsCodeDTO);
//
//    @Mappings({
//            @Mapping(source = "identifiers", target = "identifier"),
//            @Mapping(source = "names", target = "geographicName", ignore = true),
//            @Mapping(source = "fullAddress", target = "address"),
//            @Mapping(source = "descriptions", target = "description")
//
//    })
//    public LocationDTO toLocationDTO(QMSLocationDTO qmsLocationDTO);
//
//    @Mappings({
//            @Mapping(source = "notes", target = "additionalNote"),
//            @Mapping(source = "descriptions", target = "description"),
//            @Mapping(source = "addresses", target = "address"),
//            @Mapping(source = "phones", target = "phone"),
//            @Mapping(source = "mailBoxes", target = "emailAddress"),
//            @Mapping(source = "webResources", target = "contactForm")
//    })
//    public ContactPointDTO toContactPoint(QMSContactPointDTO qmsContactPoint);
//
//    @Mappings({
//            @Mapping(source = "uri", target = "id")
//    })
//    public MailboxDTO toMailBoxDTO(QMSMailBoxDTO qmsMailBoxDTO);
//
//    @Mappings({
//            @Mapping(source = "url", target = "id")
//    })
//    public WebResourceDTO toInteractiveWebResourceDTO(QMSWebDocumentDTO qmsWebDocumentDTO);
//
//    @Mappings({
//            @Mapping(target = "id", source = "uri"),
//            @Mapping(target = "identifier", source = "identifiers"),
//            @Mapping(target = "altLabel", source = "alternativeLabels"),
//            @Mapping(target = "additionalNote", source = "additionalNotes", ignore = true),
//            @Mapping(target = "homepage", source = "homepages", ignore = true),
//            @Mapping(target = "supplementaryDocument", source = "supplementaryDocuments", ignore = true),
//            @Mapping(target = "thematicArea", source = "iscedFCodes", ignore = true),
//            @Mapping(target = "educationSubject", source = "educationSubjects"),
//            @Mapping(target = "educationLevel", source = "educationLevels"),
//            @Mapping(target = "language", source = "languages"),
//            @Mapping(target = "mode", source = "modes", ignore = true),
//            @Mapping(target = "targetGroup", source = "targetGroups"),
//            @Mapping(target = "accreditation", source = "accreditation", ignore = true)
//    })
//    public QualificationDTO toQualificationDTO(QMSQualificationDTO qmsQualificationDTO);
//
//    public List<NoteDTO> toNoteList(List<QMSNoteDTO> qmsNoteDTOS);
//
//    default NoteDTO toNote(QMSNoteDTO qmsNoteDTO) {
//        NoteDTO note = new NoteDTO();
//        return note;
//    }
//
//    default URL toURL(URI uri) {
//        try {
//            return uri.toURL();
//        } catch (MalformedURLException e) {
//            return null;
//        }
//    }
//
//    default URI toURI(UUID id) {
//        try {
//            return new URI(id.toString());
//        } catch (URISyntaxException e) {
//            return null;
//        }
//    }
//
//    @Named("toIdentifier")
//    default Identifier toIdentifier(URI uri) {
//        if (uri == null) return null;
//        Identifier identifier = new Identifier();
//        identifier.setCreator(uri);
//        return identifier;
//    }
//
//
//    default List<LiteralMap> toLiteralMapList(List<QMSLabelDTO> qmsLabelDTOS) {
//        if (qmsLabelDTOS == null) return null;
//        List<LiteralMap> literalMapList = new ArrayList<>();
//        literalMapList.add(this.toLiteralMap(qmsLabelDTOS));
//        return literalMapList;
//    }
//
//    default List<NoteDTO> toNodeList(List<QMSLabelDTO> qmsLabelDTOS) {
//        if (qmsLabelDTOS == null) return null;
//        List<NoteDTO> noteList = new ArrayList<>();
//        noteList.add(this.toNote(qmsLabelDTOS));
//        return noteList;
//    }
//
//    default PhoneDTO toPhone(String phone) {
//        if (phone == null) return null;
//        PhoneDTO phoneDTO = new PhoneDTO();
//        phoneDTO.setPhoneNumber(phone);
//        return phoneDTO;
//    }
//
//    default List<AddressDTO> toSingleAddress(List<QMSLabelDTO> qmsLabelDTOS) {
//        if (qmsLabelDTOS == null) return null;
//        List<AddressDTO> addressDTOS = new ArrayList<>();
//        addressDTOS.add(this.toAddressDTO(qmsLabelDTOS));
//        return addressDTOS;
//    }
//
//    default AddressDTO toAddressDTO(List<QMSLabelDTO> qmsLabelDTOS) {
//        if (qmsLabelDTOS == null) return null;
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setFullAddress(this.toNote(qmsLabelDTOS));
//        return addressDTO;
//    }
//
//    default ConceptDTO toConcept(URI uri) {
//        if (uri == null) return null;
//        ConceptDTO code = new ConceptDTO();
//        code.setId(uri);
//        return code;
//    }
//
//    public List<ConceptDTO> toCodeList(List<URI> uris);
//
//    default LiteralMap toLiteralMap(QMSLabelDTO qmsLabelDTO) {
//        LiteralMap literalMap = new LiteralMap();
//        literalMap.put(qmsLabelDTO.getLanguage(), qmsLabelDTO.getValue());
//        return literalMap;
//    }
//
//    default LiteralMap toLiteralMap(List<QMSLabelDTO> qmsLabelDTOList) {
//        if (qmsLabelDTOList == null) return null;
//        LiteralMap literalMap = new LiteralMap();
//        qmsLabelDTOList.stream().forEach(label -> literalMap.put(label.getValue(), label.getLanguage()));
//        return literalMap;
//    }
//
//    default NoteDTO toNote(List<QMSLabelDTO> qmsLabelDTOList) {
//        if (qmsLabelDTOList == null) return null;
//        NoteDTO note = new NoteDTO();
//        qmsLabelDTOList.stream().forEach(label -> note.getNoteLiteral().put(label.getLanguage(), label.getValue()));
//        return note;
//    }


}
