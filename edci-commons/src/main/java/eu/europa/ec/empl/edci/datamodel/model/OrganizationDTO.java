package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.Resize;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.AgentDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.validation.AfterSealing;
import eu.europa.ec.empl.edci.util.ImageUtil;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "organisation")
@EDCIIdentifier(prefix = "urn:epass:org:")
@XmlType(propOrder = {"legalIdentifier", "vatIdentifier", "taxIdentifier", "preferredName", "alternativeName", "homepage", "hasLocation", "hasAccreditation", "unitOf", "logo"})
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationDTO extends AgentDTO {

    /*@XmlID
    @XmlIDExtension
    @XmlAttribute*/
    //private URI id; //1
    @Valid
    @XmlElement(name = "prefLabel")
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_PREFERREDNAME_NOTNULL, groups = AfterSealing.class)
    private Text preferredName; //1
    @XmlElement(name = "altLabel")
    @Valid
    private List<Text> alternativeName = new ArrayList<>(); //*
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_LOCATION_MIN, groups = AfterSealing.class)
    @Size(min = 1, message = EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_LOCATION_MIN, groups = AfterSealing.class)
    private List<LocationDTO> hasLocation; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_LEGALIDENTIFIER_NOTNULL, groups = AfterSealing.class)
    @Valid
    @XmlElements({
            @XmlElement(name = "registration", type = Identifier.class),
            @XmlElement(name = "registration", type = LegalIdentifier.class),
    })
    private Identifier legalIdentifier; //1
    @Valid
    private List<LegalIdentifier> vatIdentifier; //*
    @Valid
    private List<LegalIdentifier> taxIdentifier; //*
    @Valid
    private List<WebDocumentDTO> homepage; //*
    @XmlIDREF
    @XmlPath("hasAccreditation/@idref")
    @Valid
    private List<AccreditationDTO> hasAccreditation;
    //private List<LocationDTO> hasLocation; //TODO review most recent datamodel version
    @XmlIDREF
    @XmlPath("unitOf/@idref")
    @Valid
    private OrganizationDTO unitOf;
    @Valid
    private MediaObject logo; //0..1

    public OrganizationDTO() {
        this.initIdentifiable();
    }

    @Resize(height = ImageUtil.LOGO_HEIGHT, width = ImageUtil.LOGO_WIDTH)
    public MediaObject getLogo() {
        return logo;
    }

    public void setLogo(MediaObject logo) {
        this.logo = logo;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "preferredName", "alternativeName", "legalIdentifier", "identifier", "id");
    }

    public Identifier getLegalIdentifier() {
        return legalIdentifier;
    }

    public void setLegalIdentifier(Identifier legalIdentifier) {
        this.legalIdentifier = legalIdentifier;
    }

    public List<LegalIdentifier> getVatIdentifier() {
        return vatIdentifier;
    }

    public void setVatIdentifier(List<LegalIdentifier> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public List<LegalIdentifier> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(List<LegalIdentifier> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }


    public List<WebDocumentDTO> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<WebDocumentDTO> homepage) {
        this.homepage = homepage;
    }

    public List<AccreditationDTO> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationDTO> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public OrganizationDTO getUnitOf() {
        return unitOf;
    }

    public void setUnitOf(OrganizationDTO unitOf) {
        this.unitOf = unitOf;
    }

    public Text getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(Text preferredName) {
        this.preferredName = preferredName;
    }

    public List<Text> getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(List<Text> alternativeName) {
        this.alternativeName = alternativeName;
    }

    public List<LocationDTO> getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(List<LocationDTO> hasLocation) {
        this.hasLocation = hasLocation;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    //XML Getter
    public Text getPrefLabel() {
        return this.preferredName;
    }

    public List<Text> getAltLabel() {
        return this.alternativeName;
    }

    public List<Text> getAltLabels() {
        return this.alternativeName;
    }

    public Identifier getRegistration() {
        return this.legalIdentifier;
    }

    @Override
    public List<Identifier> getAllAvailableIdentifiers() {
        List<Identifier> identifiers = new ArrayList<>();
        if (super.getAllAvailableIdentifiers() != null) identifiers.addAll(super.getAllAvailableIdentifiers());
        if (this.getLegalIdentifier() != null) identifiers.add(this.getLegalIdentifier());
        if (this.getTaxIdentifier() != null) identifiers.addAll(this.getTaxIdentifier());
        if (this.getVatIdentifier() != null) identifiers.addAll(this.getVatIdentifier());
        return identifiers;
    }

}