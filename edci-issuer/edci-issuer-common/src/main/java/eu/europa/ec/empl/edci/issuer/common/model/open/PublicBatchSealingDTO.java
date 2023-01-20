package eu.europa.ec.empl.edci.issuer.common.model.open;

import org.springframework.web.multipart.MultipartFile;

public class PublicBatchSealingDTO {

    private MultipartFile[] files;
    private String password;
    private boolean signOnBehalf;

    public PublicBatchSealingDTO(MultipartFile[] files, String password, boolean signOnBehalf) {
        this.files = files;
        this.password = password;
        this.signOnBehalf = signOnBehalf;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSignOnBehalf() {
        return signOnBehalf;
    }

    public void setSignOnBehalf(boolean signOnBehalf) {
        this.signOnBehalf = signOnBehalf;
    }
}
