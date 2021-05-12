package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.DownloadableEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "contentType", "contentEncoding", "contentSize", "contentUrl", "content"})
public class MediaObject implements DownloadableEntity {

    @XmlAttribute
    private URI id; //0..1
    private Code contentType; //1
    private Code contentEncoding; //0..1
    private Integer contentSize; //0..1
    private byte[] content; //1
    private URI contentUrl; //0..1

//    @Override
//    public String getIdentifiableName() {
//        return this.getIdentifiableNameFromFieldList(this, "contentUrl", "contentType", "contentEncoding", "id");
//    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public Code getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(Code contentType) {
        this.contentType = contentType;
    }

    @Override
    public Code getContentEncoding() {
        return contentEncoding;
    }

    @Override
    public void setContentEncoding(Code contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }
}