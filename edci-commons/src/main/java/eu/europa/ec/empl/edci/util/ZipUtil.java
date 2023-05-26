package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.xml.bind.JAXBContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ZipUtil {

    protected static final Logger logger = LogManager.getLogger(ZipUtil.class);

    private JAXBContext europassCredentialJAXBContext;

    private JAXBContext verifiablePresentationJAXBContext;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIFileService edciFileService;

    public String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    /**
     * Generate a new zip file in the folder with the listed fiels
     *
     * @param folder     folder where the files to zip are
     * @param zipFile    generated file name
     * @param toZipFiles list of files to zip (already into the folder)
     * @return
     */
    public void addfilesToZIP(String folder, String zipFile, List<String> toZipFiles) {

        // the zip file name that we will create
        File zipFileName = this.getEdciFileService().getOrCreateFile(folder.concat(zipFile));

        if (zipFileName.exists()) {
            zipFileName.delete();
        }

        // open the zip stream in a try resource block, no finally needed
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFileName))) {

            // traverse every file in the selected directory and add them
            // to the zip file by calling addToZipFile(..)
            for (String toZip : toZipFiles) {
                File fileToZip = this.getEdciFileService().getOrCreateFile(folder.concat(toZip));
                addToZipFile(fileToZip, zipStream);
            }

        } catch (EDCIException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException(e).addDescription("Error creating zip file of the issued credentials");
        }

    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     *
     * @param file      file to be archived
     * @param zipStream archive to contain the file.
     */
    private void addToZipFile(File file, ZipOutputStream zipStream) {
        String inputFileName = file.getPath();
        try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            ZipEntry entry = new ZipEntry(file.getName());
            entry.setCreationTime(FileTime.fromMillis(file.lastModified()));
            zipStream.putNextEntry(entry);

            logger.info("Generated new entry for: ", () -> inputFileName);

            // Now we copy the existing file into the zip archive. To do
            // this we write into the zip stream, the call to putNextEntry
            // above prepared the stream, we now write the bytes for this
            // entry. For another source such as an in memory array, you'd
            // just change where you read the information from.
            byte[] readBuffer = new byte[2048];
            int amountRead;
            int written = 0;

            while ((amountRead = inputStream.read(readBuffer)) > 0) {
                zipStream.write(readBuffer, 0, amountRead);
                written += amountRead;
            }

        } catch (IOException e) {
            throw new EDCIException(e).addDescription("Unable to process " + inputFileName);
        }
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }

}
