package eu.europa.ec.empl.edci.dss.service.validation;

import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.esig.dss.jades.validation.JWSSerializationDocumentValidator;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignaturePolicyProvider;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JadesValidationService {

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private TLValidationJob validationJob;

    @Autowired
    private FileService edciFileService;

    public JadesValidationService() {
    }

    protected Reports validateJson(byte[] jsonBytes, DataLoader dataLoader, boolean onlineRefresh) {
        if (onlineRefresh) {
            this.getValidationJob().onlineRefresh();
        }
        DSSDocument dssDocument = new InMemoryDocument(jsonBytes, "temp.json", MimeType.JSON);
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        signedDocumentValidator.setCertificateVerifier(this.getCertificateVerifier());
        signedDocumentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        SignaturePolicyProvider signaturePolicyProvider = new SignaturePolicyProvider();
        signaturePolicyProvider.setDataLoader(dataLoader);
        signedDocumentValidator.setSignaturePolicyProvider(signaturePolicyProvider);

        return signedDocumentValidator.validateDocument();
    }

    protected Reports validateJson(String jsonFilePath, DataLoader dataLoader, boolean onlineRefresh) {
        if (onlineRefresh) {
            this.getValidationJob().onlineRefresh();
        }
        DSSDocument dssDocument = new FileDocument(this.getEdciFileService().getOrCreateFile(jsonFilePath));
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        signedDocumentValidator.setCertificateVerifier(this.getCertificateVerifier());
        signedDocumentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        SignaturePolicyProvider signaturePolicyProvider = new SignaturePolicyProvider();
        signaturePolicyProvider.setDataLoader(dataLoader);
        signedDocumentValidator.setSignaturePolicyProvider(signaturePolicyProvider);
        return signedDocumentValidator.validateDocument();
    }

    public String getPayload(String signedDocument) {
        return getPayload(signedDocument.getBytes());
    }

    public String getPayload(byte[] signedDocument) {
        //TODO: Review if it would be advisable to switch to Nimbu library
        DSSDocument dssDocument = new InMemoryDocument(signedDocument, "temp.json", MimeType.JSON);
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        String jsonContent = null;

        if (signedDocumentValidator != null) {
            JWSSerializationDocumentValidator content = (JWSSerializationDocumentValidator) signedDocumentValidator;
            if (content != null && content.getJwsJsonSerializationObject() != null) {
                jsonContent = content.getJwsJsonSerializationObject().getPayload();
            }
        }

        return jsonContent;
    }

    public String getCredentialOrPayload(byte[] document) {
        String payload = this.getPayload(document);
        return (payload == null || StringUtils.isEmpty(payload)) ? new String(document, StandardCharsets.UTF_8) : payload;
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