package eu.europa.ec.empl.edci.model.external;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EDCISignatureSimpleReport {

    private String signatureId;
    private String signatureLevel;
    private List<String> expectedSignatureLevel;
    private String digestAlgorithm;
    private List<String> expectedDigestAlgorithm;
    private String signedBy;
    private Date signingTime;
    private boolean valid;
    private Map<String, String> adesValidationInfo;
    private Map<String, String> adesValidationWarning;
    private Map<String, String> adesValidationErrors;

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public String getSignatureLevel() {
        return signatureLevel;
    }

    public void setSignatureLevel(String signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    public List<String> getExpectedSignatureLevel() {
        return expectedSignatureLevel;
    }

    public void setExpectedSignatureLevel(List<String> expectedSignatureLevel) {
        this.expectedSignatureLevel = expectedSignatureLevel;
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public List<String> getExpectedDigestAlgorithm() {
        return expectedDigestAlgorithm;
    }

    public void setExpectedDigestAlgorithm(List<String> expectedDigestAlgorithm) {
        this.expectedDigestAlgorithm = expectedDigestAlgorithm;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public Date getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(Date signingTime) {
        this.signingTime = signingTime;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Map<String, String> getAdesValidationInfo() {
        return adesValidationInfo;
    }

    public void setAdesValidationInfo(Map<String, String> adesValidationInfo) {
        this.adesValidationInfo = adesValidationInfo;
    }

    public Map<String, String> getAdesValidationWarning() {
        return adesValidationWarning;
    }

    public void setAdesValidationWarning(Map<String, String> adesValidationWarning) {
        this.adesValidationWarning = adesValidationWarning;
    }

    public Map<String, String> getAdesValidationErrors() {
        return adesValidationErrors;
    }

    public void setAdesValidationErrors(Map<String, String> adesValidationErrors) {
        this.adesValidationErrors = adesValidationErrors;
    }

}
