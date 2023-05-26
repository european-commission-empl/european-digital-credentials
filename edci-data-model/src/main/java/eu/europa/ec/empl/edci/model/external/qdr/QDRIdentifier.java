package eu.europa.ec.empl.edci.model.external.qdr;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LegalIdentifier;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:identifier:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LegalIdentifier.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = QDRIdentifier.class)
public class QDRIdentifier extends QDRJsonLdCommonDTO {

    private QDRValue issued;
    @NotNull
    private String notation;
    private URI creator;
    private String schemeAgency;
    private String schemeName;
    private String schemeVersion;
    private URI schemeId;
    private List<QDRConceptDTO> type = new ArrayList<>();

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

    public String getSchemeAgency() {
        return schemeAgency;
    }

    public void setSchemeAgency(String schemeAgency) {
        this.schemeAgency = schemeAgency;
    }

    public QDRValue getIssued() {
        return issued;
    }

    public void setIssued(QDRValue issued) {
        this.issued = issued;
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

    public List<QDRConceptDTO> getType() {
        return type;
    }

    public void setType(List<QDRConceptDTO> type) {
        this.type = type;
    }

    public URI getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(URI schemeId) {
        this.schemeId = schemeId;
    }

    @Override
    public String toString() {
        return notation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRIdentifier)) return false;
        if (!super.equals(o)) return false;
        QDRIdentifier that = (QDRIdentifier) o;
        return Objects.equals(issued, that.issued) &&
                Objects.equals(notation, that.notation) &&
                Objects.equals(creator, that.creator) &&
                Objects.equals(schemeAgency, that.schemeAgency) &&
                Objects.equals(schemeName, that.schemeName) &&
                Objects.equals(schemeVersion, that.schemeVersion) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), issued, notation, creator, schemeAgency, schemeName, schemeVersion, type);
    }
}