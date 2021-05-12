package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Score;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"percentageLower", "percentageEqual", "percentageHigher"})
public class ShortenedGradingDTO {

    @NotNull(message = MessageKeys.Validation.VALIDATION_SHORTENEDGRADING_LOWER_NOTNULL)
    @Valid
    private Score percentageLower; //1
    @NotNull(message = MessageKeys.Validation.VALIDATION_SHORTENEDGRADING_EQUAL_NOTNULL)
    @Valid
    private Score percentageEqual; //1
    @NotNull(message = MessageKeys.Validation.VALIDATION_SHORTENEDGRADING_HIGHER_NOTNULL)
    @Valid
    private Score percentageHigher; //1

    public Score getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(Score percentageLower) {
        this.percentageLower = percentageLower;
    }

    public Score getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(Score percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public Score getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(Score percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}