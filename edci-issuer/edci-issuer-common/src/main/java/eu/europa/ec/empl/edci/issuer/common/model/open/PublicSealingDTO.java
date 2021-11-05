package eu.europa.ec.empl.edci.issuer.common.model.open;

import org.springframework.web.multipart.MultipartFile;

public class PublicSealingDTO {

    private MultipartFile file;
    private String certPassword;
    private boolean signOnBehalf;


    public PublicSealingDTO(MultipartFile file, String certPassword, boolean signOnBehalf) {
        this.file = file;
        this.certPassword = certPassword;
        this.signOnBehalf = signOnBehalf;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
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
}
