package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class NotationDTView extends DataTypeView {

    private String content; //1

    private String schemeId; //0..1

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }
}