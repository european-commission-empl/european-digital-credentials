package eu.europa.ec.empl.edci;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

public class EDCIResourceResolver implements LSResourceResolver {
    /*
     * TEMPORARY RESOLVER FOR HOSTED XSDS
     * */

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(systemId);
        return new EDCIInput(publicId, systemId, is);
    }
}
