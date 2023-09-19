package eu.europa.ec.empl.edci.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The type Controlled lists util, deals with mime types.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ControlledListsUtil {

    public static final Logger logger = LogManager.getLogger(ControlledListsUtil.class);

    /**
     * Gets mime type of a provided URI using the MimeType enum.
     * @see ControlledListsUtil.MimeType
     *
     * @param uri the uri
     * @return the mime type
     */
    public String getMimeType(String uri) {
        try {
            for (MimeType mimeType : MimeType.values()) {
                if (uri.toLowerCase().endsWith(mimeType.getExtension())) {
                    return mimeType.getValue();
                }
            }
        } catch (Exception e) {
            logger.debug(e);
        }
        return null;
    }

    /**
     * The list of available Mime types.
     */
    public enum MimeType {

        PNG("image/png", "png"),
        JPEG("image/jpeg", "jpeg"),
        JPG("image/jpeg", "jpg"),
        BMP("image/bmp", "bmp"),
        FIF("image/fif", "fif"),
        GIF("image/gif", "gif"),
        TIFF("image/tiff", "tiff");

        private final String mime;
        private final String extension;

        MimeType(String uri, String extension) {
            this.mime = uri;
            this.extension = extension;
        }

        public String getValue() {
            return mime;
        }

        public String getExtension() {
            return extension;
        }
    }

}
