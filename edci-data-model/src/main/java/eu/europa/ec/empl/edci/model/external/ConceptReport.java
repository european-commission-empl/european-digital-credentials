package eu.europa.ec.empl.edci.model.external;


import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import java.net.URI;

public class ConceptReport {
    //uri = notation, name = targetName, type = targetFramework && targetFrameworkUri

    public URI id;
    private ConceptSchemeReport inScheme;
    private LiteralMap prefLabel;
    private String notation;

    public ConceptSchemeReport getInScheme() {
        return inScheme;
    }

    public void setInScheme(ConceptSchemeReport inScheme) {
        this.inScheme = inScheme;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }
}
