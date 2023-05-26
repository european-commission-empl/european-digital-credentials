package eu.europa.ec.empl.edci.model.external.qdr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:org:")
public class QDROrganisationDTO extends QDRAgentDTO {

    private List<QDRLegalIdentifier> vatIdentifier = new ArrayList<>();
    private List<QDRAccreditationDTO> accreditation = new ArrayList<QDRAccreditationDTO>();
    //@JsonManagedReference
    private List<QDROrganisationDTO> hasSubOrganization = new ArrayList<>();
    private QDRLegalIdentifier eidasLegalIdentifier;
    private List<QDRPersonDTO> member = new ArrayList<>();
    private List<QDRWebResourceDTO> homepage = new ArrayList<>();
    @NotNull
    private String legalName;
    @NotNull
    private List<QDRLocationDTO> location = new ArrayList<>();
    private QDRMediaObjectDTO logo;
    //commented, can cause issues with loopchecker @JsonBackReference
    @JsonIgnore
    private QDROrganisationDTO subOrganizationOf;
    private QDRLegalIdentifier registration;
    private List<QDRLegalIdentifier> taxIdentifier = new ArrayList<>();


    /*public OrganisationDTO() {

    }

    @JsonCreator
    public OrganisationDTO(String id) {
        try {
            this.setId(new URI(id));
        } catch (URISyntaxException e) {
            throw new EDCIException();
        }
    }*/

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public List<QDRAccreditationDTO> getAccreditation() {
        return accreditation;
    }

    public List<QDROrganisationDTO> getHasSubOrganization() {
        return hasSubOrganization;
    }

    public void setHasSubOrganization(List<QDROrganisationDTO> hasSubOrganization) {
        this.hasSubOrganization = hasSubOrganization;
    }

    public QDRLegalIdentifier getRegistration() {
        return registration;
    }

    public void setRegistration(QDRLegalIdentifier registration) {
        this.registration = registration;
    }

    public List<QDRLocationDTO> getLocation() {
        return location;
    }

    public List<QDRLegalIdentifier> getVatIdentifier() {
        return vatIdentifier;
    }

    public QDRLegalIdentifier getEidasLegalIdentifier() {
        return eidasLegalIdentifier;
    }

    public void setEidasLegalIdentifier(QDRLegalIdentifier eidasLegalIdentifier) {
        this.eidasLegalIdentifier = eidasLegalIdentifier;
    }

    public List<QDRPersonDTO> getHasMember() {
        return member;
    }

    public List<QDRWebResourceDTO> getHomepage() {
        return homepage;
    }

    public QDRMediaObjectDTO getLogo() {
        return logo;
    }

    public void setLogo(QDRMediaObjectDTO logo) {
        this.logo = logo;
    }

    public QDROrganisationDTO getSubOrganizationOf() {
        return subOrganizationOf;
    }

    public void setSubOrganizationOf(QDROrganisationDTO subOrganizationOf) {
        this.subOrganizationOf = subOrganizationOf;
    }

    public List<QDRLegalIdentifier> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setVatIdentifier(List<QDRLegalIdentifier> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public void setAccreditation(List<QDRAccreditationDTO> accreditation) {
        this.accreditation = accreditation;
    }

    public void setMember(List<QDRPersonDTO> member) {
        this.member = member;
    }

    public void setHomepage(List<QDRWebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    @Override
    public void setLocation(List<QDRLocationDTO> location) {
        this.location = location;
    }

    public void setTaxIdentifier(List<QDRLegalIdentifier> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDROrganisationDTO)) return false;
        if (!super.equals(o)) return false;
        QDROrganisationDTO that = (QDROrganisationDTO) o;
        return Objects.equals(vatIdentifier, that.vatIdentifier) &&
                Objects.equals(accreditation, that.accreditation) &&
                Objects.equals(hasSubOrganization, that.hasSubOrganization) &&
                Objects.equals(eidasLegalIdentifier, that.eidasLegalIdentifier) &&
                Objects.equals(member, that.member) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(legalName, that.legalName) &&
                Objects.equals(location, that.location) &&
                Objects.equals(logo, that.logo) &&
                Objects.equals(registration, that.registration) &&
                Objects.equals(taxIdentifier, that.taxIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vatIdentifier, accreditation, hasSubOrganization, eidasLegalIdentifier, member, homepage, legalName, location, logo, registration, taxIdentifier);
    }
}
