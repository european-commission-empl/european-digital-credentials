package eu.europa.ec.empl.edci.issuer.common.model.open;

import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import org.springframework.web.multipart.MultipartFile;

public class PublicSealingDTO {

    private MultipartFile file;
    private String certPassword;
    private boolean signOnBehalf;

    private String filePath;
    private CredentialDTO credential;


    public PublicSealingDTO(MultipartFile file, String filePath, CredentialDTO credential, String certPassword, boolean signOnBehalf) {
        this.file = file;
        this.filePath = filePath;
        this.credential = credential;
        this.certPassword = certPassword;
        this.signOnBehalf = signOnBehalf;
    }

    @Deprecated
    public PublicSealingDTO(MultipartFile file, String certPassword, boolean signOnBehalf) {
        this.file = file;
        this.credential = credential;
        this.certPassword = certPassword;
        this.signOnBehalf = signOnBehalf;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public CredentialDTO getCredential() {
        return credential;
    }

    public void setCredential(CredentialDTO credential) {
        this.credential = credential;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public boolean isSignOnBehalf() {
        return signOnBehalf;
    }

    public void setSignOnBehalf(boolean signOnBehalf) {
        this.signOnBehalf = signOnBehalf;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
