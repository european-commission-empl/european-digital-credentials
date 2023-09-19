package eu.europa.ec.empl.edci.constants;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;

import java.util.Arrays;

public enum ControlledListConcept {

    VERIFICATION_CHECKS_OWNER(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/1faf8d450f"),
    VERIFICATION_CHECKS_REVOCATION(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/271aef9eb4"),
    VERIFICATION_CHECKS_FORMAT(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/2f5b89b96f"),
    VERIFICATION_CHECKS_EXPIRY(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/94d828f160"),
    VERIFICATION_CHECKS_ACCREDITATION(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/e2bbc86a28"),
    VERIFICATION_CHECKS_SEAL(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/f9c2016fe9"),
    VERIFICATION_CHECKS_CUSTOM(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/c_50990fe3"),

    VERIFICATION_TYPE_NOTARIZED_COPY(ControlledList.VERIFICATION_TYPE, "http://data.europa.eu/snb/verifiable-presentation/c_e01977e3"),
    VERIFICATION_TYPE_MANDATED_ISSUE(ControlledList.VERIFICATION_TYPE, "http://data.europa.eu/snb/verifiable-presentation/c_825cddc0"),
    VERIFICATION_TYPE_SHARED(ControlledList.VERIFICATION_TYPE, "http://data.europa.eu/snb/verifiable-presentation/c_409018f7"),

    VERIFICATION_STATUS_SKIPPED(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/641f0c5e5d"),
    VERIFICATION_STATUS_OK(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/9895008394"),
    VERIFICATION_STATUS_ERROR(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/9d26eb9a37"),

    CREDENTIAL_TYPE_GENERIC(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/e34929035b"),
    CREDENTIAL_TYPE_CONVERTED(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/c_7e400154"),
    CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/6dff8a0f87"),
    CREDENTIAL_TYPE_ACCREDITATION(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/c_ae121a00"),
    CREDENTIAL_TYPE_ISSUED_MANDATE(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/c_9a31f32a"),

    ACCREDITATION_TYPE_INSTITUTIONAL_LICENSE(ControlledList.ACCREDITATION, "http://data.europa.eu/snb/accreditation/003293d2ce"),
    ACCREDITATION_TYPE_PROGRAM_LICENSE(ControlledList.ACCREDITATION, "http://data.europa.eu/snb/accreditation/e57dddfcf3"),
    ACCREDITATION_TYPE_PROGRAM_QUALITY(ControlledList.ACCREDITATION, "http://data.europa.eu/snb/accreditation/820101cba6"),
    ACCREDITATION_TYPE_INSTITUTIONAL_QUALITY(ControlledList.ACCREDITATION, "http://data.europa.eu/snb/accreditation/bb4618238e"),

    HUMAN_SEX_MALE(ControlledList.HUMAN_SEX, "http://publications.europa.eu/resource/authority/human-sex/MALE"),
    HUMAN_SEX_FEMALE(ControlledList.HUMAN_SEX, "http://publications.europa.eu/resource/authority/human-sex/FEMALE"),
    HUMAN_SEX_NAP(ControlledList.HUMAN_SEX, "http://publications.europa.eu/resource/authority/human-sex/NAP"),

    ENCODING_BASE64(ControlledList.ENCODING, "http://data.europa.eu/snb/encoding/6146cde7dd"),
    FILE_TYPE_JPEG(ControlledList.FILE_TYPE, "http://publications.europa.eu/resource/authority/file-type/JPEG"),
    FILE_TYPE_PNG(ControlledList.FILE_TYPE, "http://publications.europa.eu/resource/authority/file-type/PNG"),
    FILE_TYPE_XML(ControlledList.FILE_TYPE, "http://publications.europa.eu/resource/authority/file-type/XML"),
    FILE_TYPE_PDF(ControlledList.FILE_TYPE, "http://publications.europa.eu/resource/authority/file-type/PDF"),

    EVIDENCE_TYPE_MANDATE(ControlledList.EVIDENCE_TYPE, "http://data.europa.eu/snb/evidence-type/c_18016257"),
    EVIDENCE_TYPE_CONVERSION(ControlledList.EVIDENCE_TYPE, "http://data.europa.eu/snb/evidence-type/c_c4f849d7"),
    EVIDENCE_TYPE_ACCREDITATION(ControlledList.EVIDENCE_TYPE, "http://data.europa.eu/snb/evidence-type/c_991b06c6");

    private ControlledList controlledList;
    private String url;

    private ControlledListConcept(ControlledList controlledList, String url) {
        this.controlledList = controlledList;
        this.url = url;
    }


    public static ConceptDTO asConceptDTO(ControlledListConcept cLConcept) {
        ConceptDTO conceptDTO = null;
        if (cLConcept != null) {
            conceptDTO = new ConceptDTO(cLConcept.getUrl());
            conceptDTO.setInScheme(new ConceptSchemeDTO(cLConcept.getControlledList().getUrl()));
        }
        return conceptDTO;
    }

    public static ConceptDTO asConceptDTO(String url) {
        ControlledListConcept cLConcept = ControlledListConcept.fromUrl(url);
        return cLConcept != null ? ControlledListConcept.asConceptDTO(cLConcept) : null;
    }

    public static ControlledListConcept fromUrl(String url) {
        return Arrays.stream(ControlledListConcept.values()).filter(controlledListConcept -> controlledListConcept.getUrl().equals(url)).findFirst().orElse(null);
    }

    public ControlledList getControlledList() {
        return controlledList;
    }

    public void setControlledList(ControlledList controlledList) {
        this.controlledList = controlledList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
