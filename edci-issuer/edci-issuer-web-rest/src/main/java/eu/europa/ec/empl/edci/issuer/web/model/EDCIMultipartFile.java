package eu.europa.ec.empl.edci.issuer.web.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EDCIMultipartFile implements MultipartFile {
    private String name;
    private String originalFilename;
    private String contentType;
    private boolean isEmpty;
    private long size;
    private byte[] bytes;

    public EDCIMultipartFile(){

    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    @Override
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public InputStream getInputStream() {
        return   new ByteArrayInputStream(this.getBytes());
    }


    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        //Not yet available
    }
}
