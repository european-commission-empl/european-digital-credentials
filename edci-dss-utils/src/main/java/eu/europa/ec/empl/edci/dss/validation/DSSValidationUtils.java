package eu.europa.ec.empl.edci.dss.validation;

import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.CertificateValidator;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import eu.europa.esig.dss.validation.reports.Reports;

import java.util.Date;

public class DSSValidationUtils {


    public Reports validateXML(byte[] xmlBytes, CertificateVerifier cv) {
        DSSDocument dssDocument = new InMemoryDocument(xmlBytes, "temp.xml", MimeType.XML);
        SignedDocumentValidator documentValidator = SignedDocumentValidator.fromDocument(dssDocument);

        cv.setIncludeCertificateTokenValues(true);
        cv.setIncludeCertificateRevocationValues(true);
        cv.setIncludeTimestampTokenValues(true);
        //cv.setTrustedCertSource(trustedListsCertificateSource);
        documentValidator.setCertificateVerifier(cv);

        documentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);

        Reports reports = null;

        // TODO review policy
        /*try {
            InputStream is = getClass().getResourceAsStream(defaultPolicyPath);
            reports = documentValidator.validateDocument(is);
        } catch (Exception e) {
            logger.error("Unable to parse policy : " + e.getMessage(), e);
        }*/

        reports = documentValidator.validateDocument();
        return reports;
    }

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
}