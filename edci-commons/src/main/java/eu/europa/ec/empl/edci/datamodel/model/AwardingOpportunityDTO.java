package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.Date;
import java.util.List;


@XmlRootElement(name = "awardingOpportunity")
@EDCIIdentifier(prefix = "urn:epass:awardingOpportunity:")
public class AwardingOpportunityDTO implements Identifiable, Nameable {
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_AWARDINGOPPORTUNITY_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @XmlElement(name = "organization")
    @XmlIDREF
    @XmlPath("awardingBody/@idref")
    private OrganizationDTO awardingBody; //*
    private Code location; //0..1
    @XmlElement(name = "startedAtTime")
    private Date startDate; //0..1
    @XmlElement(name = "endedAtTime")
    private Date endDate; //0..1


    public AwardingOpportunityDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "identifier", "id", "location", "awardedLearningSpecification");
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    @Override
    public String getPk() {
        return pk;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public OrganizationDTO getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(OrganizationDTO awardingBody) {
        this.awardingBody = awardingBody;
    }

    public Code getLocation() {
        return location;
    }

    public void setLocation(Code location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}