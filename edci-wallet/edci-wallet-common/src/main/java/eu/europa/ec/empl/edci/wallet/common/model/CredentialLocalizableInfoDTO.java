package eu.europa.ec.empl.edci.wallet.common.model;

public class CredentialLocalizableInfoDTO {

    private Long pk;
    private String lang;
    private String title;
    private String credentialType;
    private String description;
    private CredentialDTO credentialDTO;

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CredentialDTO getCredentialDTO() {
        return credentialDTO;
    }

    public void setCredentialDTO(CredentialDTO credentialDTO) {
        this.credentialDTO = credentialDTO;
    }

}
