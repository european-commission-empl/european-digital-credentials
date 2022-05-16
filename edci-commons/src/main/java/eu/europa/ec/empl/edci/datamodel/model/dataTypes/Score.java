package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"content", "scoringSchemeId"})
public class Score implements Nameable {

    @XmlValue
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_SCORE_CONTENT_NOTNULL)
    private String content; //1
    @XmlAttribute(name = "schemeID")
    // @NotNull(message = Message.VALIDATION_SCORE_SCORINGSCHEMEID_NOTNULL)
    private String scoringSchemeId; //0..1

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "scoringSchemeId", "content");
    }

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

    //XML Getters

    public String getSchemeID() {
        return this.scoringSchemeId;
    }

    @Override
    public String toString() {
        return this.content;
    }
}