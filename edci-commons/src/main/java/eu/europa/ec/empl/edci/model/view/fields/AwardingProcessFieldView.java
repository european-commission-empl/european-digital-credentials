package eu.europa.ec.empl.edci.model.view.fields;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ClaimView;
import eu.europa.ec.empl.edci.model.view.tabs.AssessmentTabView;

import java.util.ArrayList;
import java.util.List;

public class AwardingProcessFieldView {
    private List<AssessmentTabView> used = new ArrayList<>();
    private List<AgentView> awardingBody = new ArrayList<>();
    private String awardingDate;
    private List<ClaimView> awards = new ArrayList<>();
    private String description;
    private String educationalSystemNote;
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private LocationFieldView location;
    private List<NoteFieldView> additionalNote = new ArrayList<>();

    public List<AssessmentTabView> getUsed() {
        return used;
    }

    public void setUsed(List<AssessmentTabView> used) {
        this.used = used;
    }

    public List<AgentView> getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(List<AgentView> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public String getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(String awardingDate) {
        this.awardingDate = awardingDate;
    }

    public List<ClaimView> getAwards() {
        return awards;
    }

    public void setAwards(List<ClaimView> awards) {
        this.awards = awards;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEducationalSystemNote() {
        return educationalSystemNote;
    }

    public void setEducationalSystemNote(String educationalSystemNote) {
        this.educationalSystemNote = educationalSystemNote;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public LocationFieldView getLocation() {
        return location;
    }

    public void setLocation(LocationFieldView location) {
        this.location = location;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }
}
