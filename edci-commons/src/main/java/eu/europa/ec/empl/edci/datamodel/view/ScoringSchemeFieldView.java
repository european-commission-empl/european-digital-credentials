package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class ScoringSchemeFieldView {

    private List<IdentifierFieldView> identifier; //*
    private String title; //0..1
    private String description; //0..1
    private List<LinkFieldView> supplementaryDocument; //*

    public ScoringSchemeFieldView() {

    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}