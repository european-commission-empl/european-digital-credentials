package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:mediaObject:")
public class QDRMediaObjectDTO extends QDRJsonLdCommonDTO {

    private URI contentUrl;
    @NotNull
    private String content;
    @NotNull
    private QDRConceptDTO contentEncoding;
    @NotNull
    private QDRConceptDTO contentType;
    private Integer contentSize;
    private QDRConceptDTO attachmentType;
    private String title;
    private String description;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QDRConceptDTO getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(QDRConceptDTO contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public QDRConceptDTO getContentType() {
        return contentType;
    }

    public void setContentType(QDRConceptDTO contentType) {
        this.contentType = contentType;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public QDRConceptDTO getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(QDRConceptDTO attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRMediaObjectDTO)) return false;
        if (!super.equals(o)) return false;
        QDRMediaObjectDTO that = (QDRMediaObjectDTO) o;
        return Objects.equals(contentUrl, that.contentUrl) &&
                Objects.equals(content, that.content) &&
                Objects.equals(contentEncoding, that.contentEncoding) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(contentSize, that.contentSize) &&
                Objects.equals(attachmentType, that.attachmentType) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentUrl, content, contentEncoding, contentType, contentSize, attachmentType, title, description);
    }

    @Override
    public String toString() {
        if(this.content != null && this.contentType != null) {
            ControlledListsUtil controlledListsUtil = new ControlledListsUtil();

            String extension = controlledListsUtil.getMimeType(this.contentType.getUri().toString());

            if(extension != null) {
                return "data:image/".concat(extension).concat(";base64,").concat(content);
            } else {
                return content;
            }
        } else {
            return content;
        }
    }
}
