package eu.europa.ec.empl.edci.issuer.web.model;

/**
 * The type User view.
 */
public class UploadView {

    private EDCIMultipartFile file;
    public UploadView() {
    }

    public EDCIMultipartFile getFile() {
        return file;
    }

    public void setFile(EDCIMultipartFile file) {
        this.file = file;
    }
}
