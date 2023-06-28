package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataContainers.ContactPointDCDAO;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


public class GroupDCView extends DataContainerView {

    @NotNull
    private TextDTView prefLabel; //1

    private List<ContactPointDCView> contactPoint; //*

    public TextDTView getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(TextDTView prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<ContactPointDCView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPointDCView> contactPoint) {
        this.contactPoint = contactPoint;
    }
}