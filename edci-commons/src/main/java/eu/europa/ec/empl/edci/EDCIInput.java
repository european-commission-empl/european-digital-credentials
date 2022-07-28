package eu.europa.ec.empl.edci;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

public class EDCIInput implements LSInput {

    private InputStream is;
    private String publicId;
    private String systemId;

    public EDCIInput(String publicId, String systemId, InputStream is) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.is = is;
    }

    public Reader getCharacterStream() {
        return null;
    }

    public void setCharacterStream(Reader characterStream) {
    }

    public java.io.InputStream getByteStream() {
        return is;
    }

    public void setByteStream(InputStream byteStream) {
        is = byteStream;
    }

    public String getStringData() {
        return null;
    }

    public void setStringData(String stringData) {
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getBaseURI() {
        return null;
    }

    {
    }

    public void setBaseURI(String baseURI) {
    }

    public String getEncoding() {
        return null;
    }

    public void setEncoding(String encoding) {
    }

    public boolean getCertifiedText() {
        return false;
    }

    public void setCertifiedText(boolean certifiedText) {
    }

}
