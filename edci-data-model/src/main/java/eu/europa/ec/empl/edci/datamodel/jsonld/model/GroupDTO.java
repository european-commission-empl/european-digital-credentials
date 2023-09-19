package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:group:")
public class GroupDTO extends JsonLdCommonDTO {

    private LiteralMap altLabel;
    private List<ContactPointDTO> contactPoint = new ArrayList<>();
    private List<AgentDTO> member = new ArrayList<>();
    private List<LocationDTO> location = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    @NotNull
    private LiteralMap prefLabel;
    private List<ConceptDTO> dcType = new ArrayList<>();

    public GroupDTO() {
        super();
    }

    @JsonCreator
    public GroupDTO(String uri) {
        super(uri);
    }

    public LiteralMap getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(LiteralMap altLabel) {
        this.altLabel = altLabel;
    }

    public List<ContactPointDTO> getContactPoint() {
        return contactPoint;
    }

    public List<AgentDTO> getMember() {
        return member;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<ConceptDTO> getDcType() {
        return dcType;
    }

    public void setContactPoint(List<ContactPointDTO> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public void setMember(List<AgentDTO> member) {
        this.member = member;
    }

    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setDcType(List<ConceptDTO> dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupDTO)) return false;
        if (!super.equals(o)) return false;
        GroupDTO groupDTO = (GroupDTO) o;
        return Objects.equals(altLabel, groupDTO.altLabel) &&
                Objects.equals(contactPoint, groupDTO.contactPoint) &&
                Objects.equals(member, groupDTO.member) &&
                Objects.equals(location, groupDTO.location) &&
                Objects.equals(additionalNote, groupDTO.additionalNote) &&
                Objects.equals(prefLabel, groupDTO.prefLabel) &&
                Objects.equals(dcType, groupDTO.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, contactPoint, member, location, additionalNote, prefLabel, dcType);
    }
}
