package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.upload.DeliveryDetailsDTO;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Map;
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
    private JsonLdUtil jsonLdUtil;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private BaseConfigService baseConfigService;

    @Autowired
    private EDCIFileService edciFileService;

    public String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    /**
     * This method generates a new zip file in the folder with the listed credential files.
     * The provided files are transformed from EuropeanDigitalCredential to EuropeanDigitalCredentialUpload, adding the delivery details.
     *
     * @param folder     folder where the files to zip are
     * @param zipFile    generated file name
     * @param toZipFiles Map of files to zip (already into the folder) <credential uuid, file>
     * @param deliveryDetailsMap Map of delivery details to be included <credential uuid, deliveryDetails>
     * @return
     */
    public void addFilesToZIP(String folder, String zipFile, Map<String, String> toZipFiles, Map<String, DeliveryDetailsDTO> deliveryDetailsMap) {

        // the zip file name that we will create
        File zipFileName = this.getEdciFileService().getOrCreateFile(folder.concat(zipFile));

        if (zipFileName.exists()) {
            zipFileName.delete();
        }

        // open the zip stream in a try resource block, no finally needed
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFileName); ZipOutputStream zipStream = new ZipOutputStream(fileOutputStream)) {

            // traverse every file in the selected directory and add them
            // to the zip file by calling addToZipFile(..)
            for (Map.Entry<String, String> toZip : toZipFiles.entrySet()) {
                File fileToZip = this.getEdciFileService().getOrCreateFile(folder.concat(toZip.getValue()));

                EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = this.credentialUtil.unMarshallCredential(this.readFileBytes(fileToZip));
                EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadDTO();
                europeanDigitalCredentialUploadDTO.setCredential(europeanDigitalCredentialDTO);
                europeanDigitalCredentialUploadDTO.setDeliveryDetails(deliveryDetailsMap.get(toZip.getKey()));

                try (InputStream credentialStream = this.jsonLdUtil.getJsonUtil().marshallAsStream(europeanDigitalCredentialUploadDTO)) {
                    addToZipFile(credentialStream, "upload_".concat(fileToZip.getName().replace(".jsonld", ".json")), zipStream);
                }

            }

        } catch (EDCIException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException(e).addDescription("Error creating zip file of the issued credentials");
        }

    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment. <br>
     * <strong>WARNING</strong> make sure to close the InputStream provided.
     *
     * @param inputStream the file inputStream
     * @param fileName the name of the file (extension included)
     * @param zipStream archive to contain the file.
     */
    private void addToZipFile(InputStream inputStream, String fileName, ZipOutputStream zipStream) {
        try {

            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            ZipEntry entry = new ZipEntry(fileName);
            entry.setCreationTime(FileTime.from(new Date().toInstant()));
            zipStream.putNextEntry(entry);

            logger.info("Generated new entry for: ", () -> fileName);

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
            throw new EDCIException(e).addDescription("Unable to process " + fileName);
        }
    }

    protected byte[] readFileBytes (File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }

}
