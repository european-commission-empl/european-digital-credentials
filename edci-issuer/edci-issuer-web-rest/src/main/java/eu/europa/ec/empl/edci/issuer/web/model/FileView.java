package eu.europa.ec.empl.edci.issuer.web.model;

import org.springframework.web.multipart.MultipartFile;

public class FileView {
    private MultipartFile file;
    private boolean isValid;

    public FileView(MultipartFile file) {
        this.setFile(file);
    }

    public FileView(){

    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }
}
