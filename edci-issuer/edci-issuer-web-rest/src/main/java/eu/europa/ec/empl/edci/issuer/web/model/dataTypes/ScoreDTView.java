package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class ScoreDTView extends DataTypeView {

    private String content; //1

    private String scoringSchemeId; //1

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScoringSchemeId() {
        return scoringSchemeId;
    }

    public void setScoringSchemeId(String scoringSchemeId) {
        this.scoringSchemeId = scoringSchemeId;
    }
}