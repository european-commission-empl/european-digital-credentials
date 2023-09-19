package eu.europa.ec.empl.edci.issuer.common.model.open;

import org.springframework.web.multipart.MultipartFile;

public class PublicBatchSealingDTO {

    private MultipartFile[] files;
    private String certPassword;
    private boolean signOnBehalf;

    public PublicBatchSealingDTO(MultipartFile[] files, String password, boolean signOnBehalf) {
        this.files = files;
        this.certPassword = password;
        this.signOnBehalf = signOnBehalf;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
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
