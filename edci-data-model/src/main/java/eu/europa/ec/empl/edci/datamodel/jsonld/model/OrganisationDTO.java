package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.WebResourceDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EDCIIdentifier(prefix = "urn:epass:org:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = OrganisationDTO.class)
public class OrganisationDTO extends AgentDTO {

    private List<LegalIdentifier> vatIdentifier = new ArrayList<>();
    private List<AccreditationDTO> accreditation = new ArrayList<>();
    private List<OrganisationDTO> hasSubOrganization = new ArrayList<>();
    private LegalIdentifier eIDASIdentifier;
    private List<PersonDTO> member = new ArrayList<>();
    private List<WebResourceDTO> homepage = new ArrayList<>();
    @NotNull
    private LiteralMap legalName;
    @NotNull
    private List<LocationDTO> location = new ArrayList<>();
    private MediaObjectDTO logo;
    //commented, can cause issues with loopchecker @JsonBackReference
    @JsonIgnore
    private OrganisationDTO subOrganizationOf;
    private LegalIdentifier registration;
    private List<LegalIdentifier> taxIdentifier = new ArrayList<>();


    public OrganisationDTO() {
        super();
    }

    @JsonCreator
    public OrganisationDTO(String uri) {
        super(uri);
    }

    public List<Identifier> getAllAvailableIdentifiers() {
        List<Identifier> identifiers = Stream.concat(super.getIdentifier().stream(), this.getVatIdentifier().stream()).collect(Collectors.toList());
        if (taxIdentifier.isEmpty()) identifiers.addAll(taxIdentifier);
        if (registration != null) identifiers.add(registration);
        if (eIDASIdentifier != null) identifiers.add(eIDASIdentifier);
        return identifiers;
    }

    public LiteralMap getLegalName() {
        return legalName;
    }

    public void setLegalName(LiteralMap legalName) {
        this.legalName = legalName;
    }

    public List<AccreditationDTO> getAccreditation() {
        return accreditation;
    }

    public List<OrganisationDTO> getHasSubOrganization() {
        return hasSubOrganization;
    }

    public void setHasSubOrganization(List<OrganisationDTO> hasSubOrganization) {
        this.hasSubOrganization = hasSubOrganization;
    }

    public LegalIdentifier getRegistration() {
        return registration;
    }

    public void setRegistration(LegalIdentifier registration) {
        this.registration = registration;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public List<LegalIdentifier> getVatIdentifier() {
        return vatIdentifier;
    }

    public LegalIdentifier geteIDASIdentifier() {
        return eIDASIdentifier;
    }

    public void seteIDASIdentifier(LegalIdentifier eIDASIdentifier) {
        this.eIDASIdentifier = eIDASIdentifier;
    }

    public List<PersonDTO> getHasMember() {
        return member;
    }

    public List<WebResourceDTO> getHomepage() {
        return homepage;
    }

    public MediaObjectDTO getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectDTO logo) {
        this.logo = logo;
    }

    public OrganisationDTO getSubOrganizationOf() {
        return subOrganizationOf;
    }

    public void setSubOrganizationOf(OrganisationDTO subOrganizationOf) {
        this.subOrganizationOf = subOrganizationOf;
    }

    public List<LegalIdentifier> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setVatIdentifier(List<LegalIdentifier> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public void setAccreditation(List<AccreditationDTO> accreditation) {
        this.accreditation = accreditation;
    }

    public void setMember(List<PersonDTO> member) {
        this.member = member;
    }

    public void setHomepage(List<WebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    @Override
    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public void setTaxIdentifier(List<LegalIdentifier> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganisationDTO)) return false;
        if (!super.equals(o)) return false;
        OrganisationDTO that = (OrganisationDTO) o;
        return Objects.equals(vatIdentifier, that.vatIdentifier) &&
                Objects.equals(accreditation, that.accreditation) &&
                Objects.equals(hasSubOrganization, that.hasSubOrganization) &&
                Objects.equals(eIDASIdentifier, that.eIDASIdentifier) &&
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
        return Objects.hash(super.hashCode(), vatIdentifier, accreditation, hasSubOrganization, eIDASIdentifier, member, homepage, legalName, location, logo, registration, taxIdentifier);
    }
}
