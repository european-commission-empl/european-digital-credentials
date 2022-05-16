package eu.europa.ec.empl.edci.datamodel.model.base;

import java.net.URI;
import java.net.URISyntaxException;

public interface DownloadableAsset {

    abstract URI getContentUrl();

    abstract byte[] getContent();

    abstract void setContent(byte[] content);

    abstract void setContentUrl(URI uri);

    default void setContentUrl(String uriString) throws URISyntaxException {
        URI uri = new URI(uriString);
        this.setContentUrl(uri);

    }


}
