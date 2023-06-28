package eu.europa.ec.empl.edci.model.view.fields;

import java.util.ArrayList;
import java.util.List;

public class GrantFieldView {
    private String contentUrl;
    private String description;
    private List<LinkFieldView> supplementaryDocument = new ArrayList<>();
    private String title;
    private String dcType;

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDcType() {
        return dcType;
    }

    public void setDcType(String dcType) {
        this.dcType = dcType;
    }
}
