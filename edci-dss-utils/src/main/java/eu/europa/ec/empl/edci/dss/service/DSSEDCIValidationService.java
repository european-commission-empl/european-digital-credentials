package eu.europa.ec.empl.edci.dss.service;

import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateValidator;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DSSEDCIValidationService {

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private TLValidationJob validationJob;


    public Reports validateXML(byte[] xmlBytes, boolean doRefresh) {
        if (doRefresh) this.getValidationJob().onlineRefresh();
        DSSDocument dssDocument = new InMemoryDocument(xmlBytes, "temp.xml", MimeType.XML);
        SignedDocumentValidator signedDocumentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        signedDocumentValidator.setCertificateVerifier(this.getCertificateVerifier());
        signedDocumentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        return signedDocumentValidator.validateDocument();
    }

   /* public Reports validateXML(byte[] xmlBytes, CertificateVerifier cv) {
        DSSDocument dssDocument = new InMemoryDocument(xmlBytes, "temp.xml", MimeType.XML);
        SignedDocumentValidator documentValidator = SignedDocumentValidator.fromDocument(dssDocument);
        documentValidator.setCertificateVerifier(cv);
        documentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        Reports reports = null;
        reports = documentValidator.validateDocument();
        return reports;
    }*/

    public CertificateReports validateCertificate(String base64Certificate, CertificateVerifier cv) {

        CertificateToken certificate = DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate);
        CertificateValidator certificateValidator = CertificateValidator.fromCertificate(certificate);

        certificateValidator.setCertificateVerifier(cv);
        certificateValidator.setValidationTime(new Date());

        CertificateReports reports = certificateValidator.validate();
        return reports;

    }

    public CertificateQualification getCertificateQualification(String base64Certificate, CertificateVerifier cv) {
        CertificateReports reports = validateCertificate(base64Certificate, cv);
        return reports.getSimpleReport().getQualificationAtCertificateIssuance();
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
}