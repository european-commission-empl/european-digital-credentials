package eu.europa.ec.empl.edci.datamodel.view;

public class IdentifierFieldView {

    private String content; //1

    private String identifierSchemeId; //0..1

    //From LegalIdentifier
    private String spatialId; //0..1

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(String identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

    public String getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(String spatialId) {
        this.spatialId = spatialId;
    }
}