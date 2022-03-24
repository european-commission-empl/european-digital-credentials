package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateTimeAdapter;
import eu.europa.ec.empl.edci.datamodel.validation.AfterSealing;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
@EDCIIdentifier(prefix = "urn:credential:")
public abstract class VerifiableCredentialDTO implements RootEntity, Nameable {


    @XmlTransient
    private Boolean valid = Boolean.TRUE;
    @XmlTransient
    private Boolean sealed;
    @XmlTransient
    private List<String> validationErrors = new ArrayList<String>();
    @XmlTransient
    private String pk;

    @XmlAttribute
    private String xsdVersion = "0.10.0";
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_ID_NOTNULL)
    @XmlAttribute(namespace = EDCIConstants.NAMESPACE_CRED_URI)
    private URI id; //1
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(namespace = EDCIConstants.NAMESPACE_CRED_URI)
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_ISSUED_NOTNULL, groups = AfterSealing.class)
    private Date issued; //1
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_ISSUANCEDATE_NOTNULL)
    @XmlElement(name = "validFrom", namespace = EDCIConstants.NAMESPACE_CRED_URI)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date issuanceDate; //1
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = "validUntil", namespace = EDCIConstants.NAMESPACE_CRED_URI)
    private Date expirationDate; //0..1

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getSealed() {
        return sealed;
    }

    public void setSealed(Boolean sealed) {
        this.sealed = sealed;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
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
    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public String getPk() {
        return pk;
    }


}