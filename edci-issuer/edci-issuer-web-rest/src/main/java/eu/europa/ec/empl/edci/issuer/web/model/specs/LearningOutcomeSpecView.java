package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningOutcomeSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The type Global history View.
 */
public class LearningOutcomeSpecView extends LearningOutcomeSpecLiteView {

    private List<IdentifierDTView> identifier; //*

    @NotNull
    private TextDTView title; //1

    private NoteDTView description; //0..1

    private CodeDTView learningOutcomeType; //0..1

    private CodeDTView reusabilityLevel; //0..1

    private List<CodeDTView> relatedESCOSkill; //*

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public CodeDTView getLearningOutcomeType() {
        return learningOutcomeType;
    }

    public void setLearningOutcomeType(CodeDTView learningOutcomeType) {
        this.learningOutcomeType = learningOutcomeType;
    }

    public CodeDTView getReusabilityLevel() {
        return reusabilityLevel;
    }

    public void setReusabilityLevel(CodeDTView reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public List<CodeDTView> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public void setRelatedESCOSkill(List<CodeDTView> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }
}

