package eu.europa.ec.empl.edci.model.external.qdr;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QDRAgentDTO extends QDRJsonLdCommonDTO {

    private List<String> altLabel;
    private List<QDRContactPointDTO> contactPoint = new ArrayList<>();
    private List<QDRGroupDTO> groupMemberOf = new ArrayList<>();
    private List<QDRIdentifier> identifier = new ArrayList<>();
    private QDRValue modified;
    private List<QDRLocationDTO> location = new ArrayList<>();
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    private String prefLabel;

    public List<QDRIdentifier> getAllAvailableIdentifiers() {
        return new ArrayList<>(this.getIdentifier());
    }

    public void setContactPoint(List<QDRContactPointDTO> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public void setGroupMemberOf(List<QDRGroupDTO> groupMemberOf) {
        this.groupMemberOf = groupMemberOf;
    }

    public void setIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setLocation(List<QDRLocationDTO> location) {
        this.location = location;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public QDRValue getModified() {
        return modified;
    }

    public void setModified(QDRValue modified) {
        this.modified = modified;
    }

    public List<String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<QDRContactPointDTO> getContactPoint() {
        return contactPoint;
    }

    public List<QDRGroupDTO> getGroupMemberOf() {
        return groupMemberOf;
    }

    public List<QDRIdentifier> getIdentifier() {
        return identifier;
    }

    public List<QDRLocationDTO> getLocation() {
        return location;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, contactPoint, groupMemberOf, identifier, modified, location, additionalNote, prefLabel);
    }
}
