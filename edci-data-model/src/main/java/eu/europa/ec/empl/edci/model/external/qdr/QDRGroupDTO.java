package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:group:")
public class QDRGroupDTO extends QDRJsonLdCommonDTO {

    private List<String> altLabel;
    private List<QDRContactPointDTO> contactPoint = new ArrayList<>();
    //private List<QDRAgentDTO> member = new ArrayList<>();
    private List<QDRLocationDTO> location = new ArrayList<>();
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    @NotNull
    private String prefLabel;
    private List<QDRConceptDTO> type = new ArrayList<>();

    public List<String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public List<QDRContactPointDTO> getContactPoint() {
        return contactPoint;
    }

/*    public List<QDRAgentDTO> getMember() {
        return member;
    }*/

    public List<QDRLocationDTO> getLocation() {
        return location;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<QDRConceptDTO> getType() {
        return type;
    }

    public void setContactPoint(List<QDRContactPointDTO> contactPoint) {
        this.contactPoint = contactPoint;
    }

/*    public void setMember(List<QDRAgentDTO> member) {
        this.member = member;
    }*/

    public void setLocation(List<QDRLocationDTO> location) {
        this.location = location;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setType(List<QDRConceptDTO> type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRGroupDTO)) return false;
        if (!super.equals(o)) return false;
        QDRGroupDTO groupDTO = (QDRGroupDTO) o;
        return Objects.equals(altLabel, groupDTO.altLabel) &&
                Objects.equals(contactPoint, groupDTO.contactPoint) &&
                //Objects.equals(member, groupDTO.member) &&
                Objects.equals(location, groupDTO.location) &&
                Objects.equals(additionalNote, groupDTO.additionalNote) &&
                Objects.equals(prefLabel, groupDTO.prefLabel) &&
                Objects.equals(type, groupDTO.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, contactPoint/*, member*/, location, additionalNote, prefLabel, type);
    }
}
