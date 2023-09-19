package eu.europa.ec.empl.edci.datamodel.view.dataType;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.view.base.JsonLdCommonView;

public class ConceptView extends JsonLdCommonView {

    private ConceptSchemeView inScheme;
    private LiteralMap prefLabel;
    private String notation;


    public ConceptSchemeView getInScheme() {
        return inScheme;
    }

    public void setInScheme(ConceptSchemeView inScheme) {
        this.inScheme = inScheme;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @Override
    public String toString() {
        return prefLabel != null ? prefLabel.toString() : this.getId() != null ? this.getId().toString() : "_blank";
    }
}
