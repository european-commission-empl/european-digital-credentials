package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSScoreDTO {
    private String content;
    private String scoringSchemeID;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScoringSchemeID() {
        return scoringSchemeID;
    }

    public void setScoringSchemeID(String scoringSchemeID) {
        this.scoringSchemeID = scoringSchemeID;
    }
}
