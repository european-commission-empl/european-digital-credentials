package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"content", "unit"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Measure {

    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_MEASURE_CONTENT_NOTNULL)
    private Float content; //1
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_MEASURE_UNIT_NOTNULL)
    private String unit; //1

    public Float getContent() {
        return content;
    }

    public void setContent(Float content) {
        this.content = content;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return content + " " + unit;
    }
}