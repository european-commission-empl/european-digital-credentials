package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class LearningOutcomeFieldView {

    private String name;
    private String description;
    private String type;
    private String reusabilityLevel;
    private List<LinkFieldView> relatedESCOSkill; //*
    private List<String> relatedSkill;
    private List<IdentifierFieldView> identifier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReusabilityLevel() {
        return reusabilityLevel;
    }

    public List<String> getRelatedSkill() {
        return relatedSkill;
    }

    public void setRelatedSkill(List<String> relatedSkill) {
        this.relatedSkill = relatedSkill;
    }

    public void setReusabilityLevel(String reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public List<LinkFieldView> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public void setRelatedESCOSkill(List<LinkFieldView> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }
}

