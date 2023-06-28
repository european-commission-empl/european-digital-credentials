package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import java.util.List;

public class EntitlemSpecificationDCView extends DataContainerView {

    private List<IdentifierDTView> identifier; //*

    private CodeDTView dcType; //1

    private TextDTView title; //0..1

    private TextDTView altLabel; //*

    private CodeDTView status; //1

    private NoteDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private List<WebDocumentDCView> homePage; //*

    private List<WebDocumentDCView> supplementaryDocument; //*

    private List<CodeDTView> limitJurisdiction; //*

    private List<CodeDTView> limitOccupation; //*

    private List<CodeDTView> limitNationalOccupation; //*  OccupationAssociationDTView

    private List<LearningSpecificationDCView> mayResultFrom; //*

    private List<EntitlemSpecificationDCView> hasPart; //*

    private List<EntitlemSpecificationDCView> specialisationOf; //*

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public CodeDTView getDcType() {
        return dcType;
    }

    public void setDcType(CodeDTView dcType) {
        this.dcType = dcType;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public TextDTView getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTView altLabel) {
        this.altLabel = altLabel;
    }

    public CodeDTView getStatus() {
        return status;
    }

    public void setStatus(CodeDTView status) {
        this.status = status;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDCView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCView> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<CodeDTView> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTView> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public List<CodeDTView> getLimitOccupation() {
        return limitOccupation;
    }

    public void setLimitOccupation(List<CodeDTView> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }

    public List<CodeDTView> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public void setLimitNationalOccupation(List<CodeDTView> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public List<LearningSpecificationDCView> getMayResultFrom() {
        return mayResultFrom;
    }

    public void setMayResultFrom(List<LearningSpecificationDCView> mayResultFrom) {
        this.mayResultFrom = mayResultFrom;
    }

    public List<EntitlemSpecificationDCView> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<EntitlemSpecificationDCView> hasPart) {
        this.hasPart = hasPart;
    }

    public List<EntitlemSpecificationDCView> getSpecialisationOf() {
        return specialisationOf;
    }

    public void setSpecialisationOf(List<EntitlemSpecificationDCView> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }
}