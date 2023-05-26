package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.net.URI;

public class MediaObjectDTView extends DataTypeView {

    private CodeDTView contentType; //1

    private CodeDTView contentEncoding; //0..1

    private Integer contentSize; //0..1

    private String content; //1

    private URI contentUrl; //0..1

    public CodeDTView getContentType() {
        return contentType;
    }

    public void setContentType(CodeDTView contentType) {
        this.contentType = contentType;
    }

    public CodeDTView getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(CodeDTView contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }
}