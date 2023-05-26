package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;

import java.util.List;

public class AwardingOpportunityDCView extends DataContainerView {

    private List<IdentifierDTView> identifier; //*

    private LearningSpecificationDCView learningAchievementSpecification; //1

    private CodeDTView location; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String startedAtTime; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String endedAtTime; //0..1

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public LearningSpecificationDCView getLearningAchievementSpecification() {
        return learningAchievementSpecification;
    }

    public void setLearningAchievementSpecification(LearningSpecificationDCView learningAchievementSpecification) {
        this.learningAchievementSpecification = learningAchievementSpecification;
    }

    public CodeDTView getLocation() {
        return location;
    }

    public void setLocation(CodeDTView location) {
        this.location = location;
    }

    public String getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(String startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public String getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(String endedAtTime) {
        this.endedAtTime = endedAtTime;
    }
}