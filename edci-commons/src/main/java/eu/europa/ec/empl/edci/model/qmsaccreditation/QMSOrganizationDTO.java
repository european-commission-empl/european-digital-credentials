package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSOrganizationDTO {

    private URI registration;
    private List<QMSLegalIdentifierDTO> vatIdentifiers = new ArrayList<>();
    private List<QMSLegalIdentifierDTO> taxIdentifiers = new ArrayList<>();
    private List<QMSIdentifierDTO> identifiers = new ArrayList<>();
    private List<QMSCodeDTO> types = new ArrayList<>();
    private List<QMSLabelDTO> prefLabels = new ArrayList<>();
    private List<QMSLabelDTO> alternativeNames = new ArrayList<>();
    private List<QMSWebDocumentDTO> localizedHomepages = new ArrayList<>();
    private List<QMSLocationDTO> locations = new ArrayList<>();
    private List<QMSContactPointDTO> contactPoints = new ArrayList<>();
    private QMSOrganizationDTO unitOf;
    private List<QMSOrganizationDTO> hasUnits = new ArrayList<>();
    private QMSMediaObjectDTO logo;

    public QMSOrganizationDTO() {

    }

    public URI getRegistration() {
        return registration;
    }

    public void setRegistration(URI registration) {
        this.registration = registration;
    }

    public List<QMSLegalIdentifierDTO> getVatIdentifiers() {
        return vatIdentifiers;
    }

    public void setVatIdentifiers(List<QMSLegalIdentifierDTO> vatIdentifiers) {
        this.vatIdentifiers = vatIdentifiers;
    }

    public List<QMSLegalIdentifierDTO> getTaxIdentifiers() {
        return taxIdentifiers;
    }

    public void setTaxIdentifiers(List<QMSLegalIdentifierDTO> taxIdentifiers) {
        this.taxIdentifiers = taxIdentifiers;
    }

    public List<QMSIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<QMSIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }

    public List<QMSCodeDTO> getTypes() {
        return types;
    }

    public void setTypes(List<QMSCodeDTO> types) {
        this.types = types;
    }

    public List<QMSLabelDTO> getPrefLabels() {
        return prefLabels;
    }

    public void setPrefLabels(List<QMSLabelDTO> prefLabels) {
        this.prefLabels = prefLabels;
    }

    public List<QMSLabelDTO> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<QMSLabelDTO> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public List<QMSWebDocumentDTO> getLocalizedHomepages() {
        return localizedHomepages;
    }

    public void setLocalizedHomepages(List<QMSWebDocumentDTO> localizedHomepages) {
        this.localizedHomepages = localizedHomepages;
    }

    public List<QMSLocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<QMSLocationDTO> locations) {
        this.locations = locations;
    }

    public List<QMSContactPointDTO> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<QMSContactPointDTO> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public QMSOrganizationDTO getUnitOf() {
        return unitOf;
    }

    public void setUnitOf(QMSOrganizationDTO unitOf) {
        this.unitOf = unitOf;
    }

    public List<QMSOrganizationDTO> getHasUnits() {
        return hasUnits;
    }

    public void setHasUnits(List<QMSOrganizationDTO> hasUnits) {
        this.hasUnits = hasUnits;
    }

    public QMSMediaObjectDTO getLogo() {
        return logo;
    }

    public void setLogo(QMSMediaObjectDTO logo) {
        this.logo = logo;
    }

    public List<QMSIdentifierDTO> getAllAvailableIdentifiers() {
        List<QMSIdentifierDTO> qmsIdentifiers = new ArrayList<>();
        QMSIdentifierDTO registrationIdentifier = new QMSIdentifierDTO();
        registrationIdentifier.setValue(this.getRegistration());
        if (registrationIdentifier != null) qmsIdentifiers.add(registrationIdentifier);
        if (vatIdentifiers != null) qmsIdentifiers.addAll(vatIdentifiers);
        if (taxIdentifiers != null) qmsIdentifiers.addAll(taxIdentifiers);
        if (identifiers != null) qmsIdentifiers.addAll(identifiers);
        return qmsIdentifiers;
    }
}
