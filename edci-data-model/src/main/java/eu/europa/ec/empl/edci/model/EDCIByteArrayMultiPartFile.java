package eu.europa.ec.empl.edci.model;

import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class EDCIByteArrayMultiPartFile implements MultipartFile {

    private Logger logger = LogManager.getLogger(EDCIByteArrayMultiPartFile.class);
    private String fileName;
    private byte[] content;
    private ContentType contentType;

    public EDCIByteArrayMultiPartFile(String fileName, byte[] content, ContentType contentType) {
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return this.contentType.toString();
    }

    @Override
    public boolean isEmpty() {
        return this.content == null || this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(this.content);
        } catch (Exception e) {
            logger.error(String.format("Coud not tranfer multipartFile %s", file.getAbsolutePath()));
        }
    }
}
