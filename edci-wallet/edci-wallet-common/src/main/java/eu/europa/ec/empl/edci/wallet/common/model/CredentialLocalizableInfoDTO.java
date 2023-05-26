package eu.europa.ec.empl.edci.wallet.common.model;

import java.util.ArrayList;
import java.util.List;

public class CredentialLocalizableInfoDTO {

    private Long pk;
    private String lang;
    private String title;
    private String description;
    private List<String> credentialProfile = new ArrayList<>();
    private CredentialDTO credential;

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

    public List<String> getCredentialProfile() {
        return credentialProfile;
    }

    public void setCredentialProfile(List<String> credentialProfile) {
        this.credentialProfile = credentialProfile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CredentialDTO getCredential() {
        return credential;
    }

    public void setCredential(CredentialDTO credential) {
        this.credential = credential;
    }

}
