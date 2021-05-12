package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.MessageKeys;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"content", "unit"})
public class Amount {
    @XmlValue
    @NotNull(message = MessageKeys.Validation.VALIDATION_AMOUNT_CONTENT_NOTNULL)
    private Float content; //1
    @XmlAttribute
    @NotNull(message = MessageKeys.Validation.VALIDATION_AMOUNT_UNIT_NONTNULL)
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
}
