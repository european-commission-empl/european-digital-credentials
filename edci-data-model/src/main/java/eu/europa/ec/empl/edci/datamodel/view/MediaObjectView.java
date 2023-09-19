package eu.europa.ec.empl.edci.datamodel.view;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.view.base.JsonLdCommonView;
import eu.europa.ec.empl.edci.datamodel.view.dataType.ConceptView;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class MediaObjectView extends JsonLdCommonView {

    private URI contentUrl;
    @NotNull
    private String content;
    @NotNull
    private ConceptView contentEncoding;
    @NotNull
    private ConceptView contentType;
    private Integer contentSize;
    private ConceptView attachmentType;
    private LiteralMap title;
    private LiteralMap description;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ConceptView getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(ConceptView contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public ConceptView getContentType() {
        return contentType;
    }

    public void setContentType(ConceptView contentType) {
        this.contentType = contentType;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public ConceptView getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(ConceptView attachmentType) {
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
