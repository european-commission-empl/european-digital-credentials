package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.WebDocumentDCView;

import java.util.List;

public class StandardDTView extends DataTypeView {

    List<IdentifierDTView> identifier; //*

    TextDTView title; //0..1

    NoteDTView description; //0..1

    List<WebDocumentDCView> supplementaryDocument; //*

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

    public List<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}