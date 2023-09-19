package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:credential:")
public class EuropeanDigitalCredentialDTO extends VerifiableCredentialDTO {

    private List<MediaObjectDTO> attachment = new ArrayList<>();
    private List<ConceptDTO> credentialProfiles = new ArrayList<>();
    @NotNull
    private DisplayParameterDTO displayParameter;
    private List<Identifier> identifier = new ArrayList<>();

    public EuropeanDigitalCredentialDTO() {
    }

    @JsonCreator
    public EuropeanDigitalCredentialDTO(String uri) {
        this.getType().add(this.getClass().getSimpleName().replaceAll("DTO$", ""));
        this.setId(URI.create(uri));
    }

    @Override
    public String getName() {
        return this.getNameFromFieldList(this, "identifier", "credentialLabel", "type", "id");
    }

    public List<MediaObjectDTO> getAttachment() {
        return attachment;
    }

    public DisplayParameterDTO getDisplayParameter() {
        return displayParameter;
    }

    public void setDisplayParameter(DisplayParameterDTO displayParameter) {
        this.displayParameter = displayParameter;
    }

    public List<ConceptDTO> getCredentialProfiles() {
        return credentialProfiles;
    }

    public void setCredentialProfiles(List<ConceptDTO> credentialProfiles) {
        this.credentialProfiles = credentialProfiles;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setAttachment(List<MediaObjectDTO> attachment) {
        this.attachment = attachment;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EuropeanDigitalCredentialDTO)) return false;
        EuropeanDigitalCredentialDTO that = (EuropeanDigitalCredentialDTO) o;
        return Objects.equals(attachment, that.attachment) &&
                Objects.equals(credentialProfiles, that.credentialProfiles) &&
                Objects.equals(displayParameter, that.displayParameter) &&
                Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachment, credentialProfiles, displayParameter, identifier);
    }
}
