package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:mediaObject:")
public class MediaObjectDTO extends JsonLdCommonDTO {

    private URI contentUrl;
    @NotNull
    private String content;
    @NotNull
    private ConceptDTO contentEncoding;
    @NotNull
    private ConceptDTO contentType;
    private Integer contentSize;
    private ConceptDTO attachmentType;
    private LiteralMap title;
    private LiteralMap description;

    public MediaObjectDTO() {
        super();
    }

    @JsonCreator
    public MediaObjectDTO(String uri) {
        super(uri);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ConceptDTO getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(ConceptDTO contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public ConceptDTO getContentType() {
        return contentType;
    }

    public void setContentType(ConceptDTO contentType) {
        this.contentType = contentType;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public ConceptDTO getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(ConceptDTO attachmentType) {
        this.attachmentType = attachmentType;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
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
        if (!(o instanceof MediaObjectDTO)) return false;
        if (!super.equals(o)) return false;
        MediaObjectDTO that = (MediaObjectDTO) o;
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
        if (this.content != null && this.contentType != null) {
            ControlledListsUtil controlledListsUtil = new ControlledListsUtil();

            String extension = controlledListsUtil.getMimeType(this.contentType.getId().toString());

            if (extension != null) {
                return "data:image/".concat(extension).concat(";base64,").concat(content);
            } else {
                return content;
            }
        } else {
            return content;
        }
    }
}
