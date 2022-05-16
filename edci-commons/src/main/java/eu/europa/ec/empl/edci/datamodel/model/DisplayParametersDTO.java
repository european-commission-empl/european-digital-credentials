package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.Resize;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Label;
import eu.europa.ec.empl.edci.util.ImageUtil;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"summaryDisplay", "background", "html", "template", "labels"})
public class DisplayParametersDTO implements Nameable, Identifiable {

    private String summaryDisplay;

    @Valid
    private MediaObject background; //0..1

    @XmlCDATA
    private String html;

    @XmlCDATA
    private String template;

    @XmlElementWrapper(name = "labels")
    @XmlElement(name = "prefLabel")
    List<Label> labels = new ArrayList<>();

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "summaryDisplay");
    }

    public String getSummaryDisplay() {
        return summaryDisplay;
    }

    public void setSummaryDisplay(String summaryDisplay) {
        this.summaryDisplay = summaryDisplay;
    }

    @Resize(height = ImageUtil.BACKGROUND_HEIGHT, width = ImageUtil.BACKGROUND_WIDTH)
    public MediaObject getBackground() {
        return background;
    }

    public void setBackground(MediaObject background) {
        this.background = background;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public URI getId() {
        return null;
    }

    @Override
    public void setId(URI id) {

    }

    @Override
    public void setPk(String pk) {

    }

    @Override
    public String getPk() {
        return null;
    }

    //XML Getters
    public List<Label> getPrefLabel() {
        return this.labels;
    }

    public List<Label> getPrefLabels() {
        return this.labels;
    }
}