package eu.europa.ec.empl.edci.issuer.common.model.base;

import org.springframework.web.multipart.MultipartFile;

public class FileDTO {
    private MultipartFile file;
    private String email;
    private boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
