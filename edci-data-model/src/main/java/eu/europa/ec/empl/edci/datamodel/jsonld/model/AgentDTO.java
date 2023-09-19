package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EDCIIdentifier(prefix = "urn:epass:agent:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrganisationDTO.class, name = "Organisation"),
        @JsonSubTypes.Type(value = PersonDTO.class, name = "Person")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true, defaultImpl = AgentDTO.class)
public class AgentDTO extends JsonLdCommonDTO {

    private LiteralMap altLabel;
    private List<ContactPointDTO> contactPoint = new ArrayList<>();
    private List<GroupDTO> groupMemberOf = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    private ZonedDateTime dateModified;
    private List<LocationDTO> location = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private LiteralMap prefLabel;

    public AgentDTO() {
        super();
    }
    
    @JsonCreator
    public AgentDTO(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return this.getNameFromFieldList(this, false, "prefLabel", "altLabel", "id", "type");
    }

    public List<Identifier> getAllAvailableIdentifiers() {
        return new ArrayList<>(this.getIdentifier());
    }

    public void setContactPoint(List<ContactPointDTO> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public void setGroupMemberOf(List<GroupDTO> groupMemberOf) {
        this.groupMemberOf = groupMemberOf;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public ZonedDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(ZonedDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public LiteralMap getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(LiteralMap altLabel) {
        this.altLabel = altLabel;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<ContactPointDTO> getContactPoint() {
        return contactPoint;
    }

    public List<GroupDTO> getGroupMemberOf() {
        return groupMemberOf;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentDTO)) return false;
        if (!super.equals(o)) return false;
        AgentDTO agentDTO = (AgentDTO) o;
        return Objects.equals(altLabel, agentDTO.altLabel) &&
                Objects.equals(contactPoint, agentDTO.contactPoint) &&
                Objects.equals(groupMemberOf, agentDTO.groupMemberOf) &&
                Objects.equals(identifier, agentDTO.identifier) &&
                Objects.equals(dateModified, agentDTO.dateModified) &&
                Objects.equals(location, agentDTO.location) &&
                Objects.equals(additionalNote, agentDTO.additionalNote) &&
                Objects.equals(prefLabel, agentDTO.prefLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, contactPoint, groupMemberOf, identifier, dateModified, location, additionalNote, prefLabel);
    }
}
