package eu.europa.ec.empl.edci.dss.service.validation;

import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.esig.dss.jades.validation.JWSSerializationDocumentValidator;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.validation.ValidationResult;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignaturePolicyProvider;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import org.everit.json.schema.SchemaLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class XadesValidationService {

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private TLValidationJob validationJob;

    @Autowired
    private FileService edciFileService;

    public XadesValidationService() {
    }

    protected Reports validateXml(byte[] xmlBytes, boolean onlineRefresh) {
        if (onlineRefresh) {
            this.getValidationJob().onlineRefresh();
        }
        DSSDocument dssDocument = new InMemoryDocument(xmlBytes, "temp.xml", MimeType.XML);
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        signedDocumentValidator.setCertificateVerifier(this.getCertificateVerifier());
        signedDocumentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);

        return signedDocumentValidator.validateDocument();
    }

    protected Reports validateXml(String xmlFilePath, boolean onlineRefresh) {
        if (onlineRefresh) {
            this.getValidationJob().onlineRefresh();
        }
        DSSDocument dssDocument = new FileDocument(this.getEdciFileService().getOrCreateFile(xmlFilePath));
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        signedDocumentValidator.setCertificateVerifier(this.getCertificateVerifier());
        signedDocumentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);

        return signedDocumentValidator.validateDocument();
    }


    public CertificateVerifier getCertificateVerifier() {
        return certificateVerifier;
    }

    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    public TLValidationJob getValidationJob() {
        return validationJob;
    }

    public void setValidationJob(TLValidationJob validationJob) {
        this.validationJob = validationJob;
    }

    public FileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(FileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}