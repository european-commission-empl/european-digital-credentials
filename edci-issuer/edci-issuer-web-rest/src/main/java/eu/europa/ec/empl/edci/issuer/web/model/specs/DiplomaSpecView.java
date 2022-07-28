package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AccreditationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.ContactPointDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LocationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.WebDocumentDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.DiplomaSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.OrganizationSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class DiplomaSpecView extends DiplomaSpecLiteView {

    /* *************
     *   Fields    *
     ***************/

    @NotNull
    private String html; //1

    @NotNull
    private String format; //1

    private MediaObjectDTView background; //0..1

    private Set<LabelDTView> labels; //*

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public MediaObjectDTView getBackground() {
        return background;
    }

    public void setBackground(MediaObjectDTView background) {
        this.background = background;
    }

    public Set<LabelDTView> getLabels() {
        return labels;
    }

    public void setLabels(Set<LabelDTView> labels) {
        this.labels = labels;
    }
}