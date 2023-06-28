package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:identifier:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LegalIdentifier.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = Identifier.class)
public class Identifier extends JsonLdCommonDTO {

    private ZonedDateTime dateIssued;
    @NotNull
    private String notation;
    private URI creator;
    private LiteralMap schemeAgency;
    private String schemeName;
    private String schemeVersion;
    private List<ConceptDTO> dcType = new ArrayList<>();

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getSchemeVersion() {
        return schemeVersion;
    }

    public void setSchemeVersion(String schemeVersion) {
        this.schemeVersion = schemeVersion;
    }

    public LiteralMap getSchemeAgency() {
        return schemeAgency;
    }

    public void setSchemeAgency(LiteralMap schemeAgency) {
        this.schemeAgency = schemeAgency;
    }

    public ZonedDateTime getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(ZonedDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }

    public URI getCreator() {
        return creator;
    }

    public void setCreator(URI creator) {
        this.creator = creator;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public List<ConceptDTO> getDcType() {
        return dcType;
    }

    public void setDcType(List<ConceptDTO> dcType) {
        this.dcType = dcType;
    }

    @Override
    public String toString() {
        return notation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        if (!super.equals(o)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(dateIssued, that.dateIssued) &&
                Objects.equals(notation, that.notation) &&
                Objects.equals(creator, that.creator) &&
                Objects.equals(schemeAgency, that.schemeAgency) &&
                Objects.equals(schemeName, that.schemeName) &&
                Objects.equals(schemeVersion, that.schemeVersion) &&
                Objects.equals(dcType, that.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateIssued, notation, creator, schemeAgency, schemeName, schemeVersion, dcType);
    }
}