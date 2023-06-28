package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class GradingSchemeFieldView {

    private List<IdentifierFieldView> identifier; //*
    private List<String> title; //0..1
    private List<String> description; //0..1
    private List<LinkFieldView> supplementaryDocument; //*

    public GradingSchemeFieldView() {

    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}