package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.MessageKeys;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class QualificationAwardDTO extends LearningAchievementDTO {
    @NotNull(message = MessageKeys.Validation.VALIDATION_QUALIFICATIONAWARD_SPECIFIEDBY_NOTNULL)
    private QualificationDTO specifiedBy;

    @Override
    public QualificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(QualificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }
}
