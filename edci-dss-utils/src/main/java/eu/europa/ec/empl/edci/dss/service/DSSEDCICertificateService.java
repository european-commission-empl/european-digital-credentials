package eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class DSSEDCICertificateService {

    private static final Logger logger = Logger.getLogger(DSSEDCICertificateService.class);

    /**
     * Reads the Certificate info (c,cn,o.oid) and adds it to a Map
     *
     * @param certPath the cert path
     * @param password the password for the certificate
     * @return a Map with the information
     */
    public Map<String, String> getCertificateInfo(String certPath, String password) {
        X509Certificate x509Certificate = this.getCertificate(certPath, password);
        Map<String, String> certInfo = new HashMap<String, String>();
        try {
            X500Name x500Name = new JcaX509CertificateHolder(x509Certificate).getSubject();
            RDN c = x500Name.getRDNs(BCStyle.C)[0];
            RDN cn = x500Name.getRDNs(BCStyle.CN)[0];
            RDN o = x500Name.getRDNs(BCStyle.O)[0];
            RDN oid = x500Name.getRDNs(BCStyle.ORGANIZATION_IDENTIFIER)[0];
            String country = IETFUtils.valueToString(c.getFirst().getValue());
            String commonName = IETFUtils.valueToString(cn.getFirst().getValue());
            String organization = IETFUtils.valueToString(o.getFirst().getValue());
            String organizationID = IETFUtils.valueToString(oid.getFirst().getValue());
            certInfo.put(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME, country);
            certInfo.put(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME, commonName);
            certInfo.put(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION, organization);
            certInfo.put(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER, organizationID);
        } catch (CertificateEncodingException e) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE).setCause(e);
        }
        return certInfo;
    }

    /**
     * Reads the certificate info directly from a certificate string (used in first nexu step)
     *
     * @param certificate the certificate string
     * @return
     */
    public Map<String, String> getCertificateInfo(String certificate) {

        Map<String, String> issuerAttributes = new HashMap<>();

        try {
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificate.getBytes("utf-8")));
            X500Principal subject = cert.getSubjectX500Principal();

            Map<String, String> oidMap = new HashMap<>();
            oidMap.put("2.5.4.97", EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER);
            String issuerName = subject.getName(X500Principal.RFC2253, oidMap);

            List<String> values = Arrays.asList(issuerName.split(EDCIConstants.StringPool.STRING_COMMA));
            for (String value : values) {
                String[] attributeNameAndValue = value.split(EDCIConstants.StringPool.STRING_EQUALS);
                issuerAttributes.put(attributeNameAndValue[0], attributeNameAndValue[1]);
            }

        } catch (CertificateException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return issuerAttributes;
    }

    /**
     * Checks a certificate, will throw EDCIException if any error occurs
     *
     * @param certPath the certificate path
     * @param password the certificate password
     * @return true if the check is correct
     */
    public boolean checkCertificate(String certPath, String password) {
        this.getCertificate(certPath, password);
        return true;
    }

    /**
     * get a locally stored X509Certificate
     *
     * @param certPath the certificate path
     * @param password the certificate password
     * @return
     */
    public X509Certificate getCertificate(String certPath, String password) {
        try {
            KeyStore keystore = KeyStore.getInstance("pkcs12");
            keystore.load(new FileInputStream(certPath.toString()), password.toCharArray());
            if (keystore.size() > 1) {
                throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
            }
            Enumeration<String> aliases = keystore.aliases();
            String alias = aliases.nextElement();
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);

            return certificate;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE).setCause(e);
        } catch (FileNotFoundException e) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.LOCAL_CERTIFICATE_NOT_FOUND, EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_NOT_FOUND, certPath).setCause(e);
        } catch (IOException e) {
            throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.LOCAL_CERTIFICATE_BAD_PASSWORD, EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_BAD_PASSWORD).setCause(e);
        }
    }

    /**
     * Gets a Certificate Signature Token, used for signing process
     *
     * @param certPath the certificate Path
     * @param password the certificate password
     * @return
     */
    public SignatureTokenConnection getCertificateSignatureToken(String certPath, String password) {
        Pkcs12SignatureToken jksSignatureToken = null;
        try {
            InputStream is = new FileInputStream(certPath);
            jksSignatureToken = new Pkcs12SignatureToken(is, new KeyStore.PasswordProtection(password.toCharArray()));
        } catch (FileNotFoundException e) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.LOCAL_CERTIFICATE_NOT_FOUND, EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_NOT_FOUND, certPath).setCause(e);
        } catch (DSSException e) {
            throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.LOCAL_CERTIFICATE_BAD_PASSWORD, EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_BAD_PASSWORD).setCause(e);
        }
        return jksSignatureToken;
    }


}
