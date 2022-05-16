package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.datamodel.model.base.DownloadableAsset;

import java.net.URI;
import java.util.Base64;

public class DownloadableObject implements DownloadableAsset {

    private byte[] content; //1
    private URI contentUrl; //0..1


    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public URI getContentUrl() {
        return contentUrl;
    }

    @Override
    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(this.content);
    }
}
