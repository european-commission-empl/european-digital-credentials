package eu.europa.ec.empl.edci.issuer.common.model;

public class XMLCredentialDTO extends CredentialDTO{
    public String logoURL;
    public String issuer;
    public String description;

    public XMLCredentialDTO(){
        
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
