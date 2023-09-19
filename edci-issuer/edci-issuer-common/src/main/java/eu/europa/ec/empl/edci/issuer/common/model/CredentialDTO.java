package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.datamodel.upload.DisplayDetailsDTO;

import java.util.ArrayList;
import java.util.List;

public class CredentialDTO {
    private String studentName;
    private List<String> email = new ArrayList<>();;
    private List<String> walletAddress = new ArrayList<>();;
    private DisplayDetailsDTO displayDetails;
    private String type;
    private String course;
    private String certPassword;
    private String uuid;
    private String primaryLanguage;
    private Boolean sealed;
    private Boolean sent;
    private Boolean received;
    private Boolean valid;
    private String issuerName;
    private List<String> validationErrors = new ArrayList<String>();
    private List<String> sealingErrors = new ArrayList<>();
    private List<String> sendErrors = new ArrayList<String>();
    private List<String> receivedErrors = new ArrayList<String>();


    public CredentialDTO() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public Boolean isSealed() {
        return sealed;
    }

    public void setSealed(Boolean sealed) {
        this.sealed = sealed;
    }

    public Boolean isSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Boolean isRecieved() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(List<String> walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<String> getSealingErrors() {
        return sealingErrors;
    }

    public void setSealingErrors(List<String> sealingErrors) {
        this.sealingErrors = sealingErrors;
    }

    public List<String> getSendErrors() {
        return sendErrors;
    }

    public void setSendErrors(List<String> sendErrors) {
        this.sendErrors = sendErrors;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public Boolean getSealed() {
        return sealed;
    }

    public Boolean getSent() {
        return sent;
    }

    public Boolean getReceived() {
        return received;
    }

    public Boolean getValid() {
        return valid;
    }

    public List<String> getReceivedErrors() {
        return receivedErrors;
    }

    public void setReceivedErrors(List<String> receivedErrors) {
        this.receivedErrors = receivedErrors;
    }

    public DisplayDetailsDTO getDisplayDetails() {
        return displayDetails;
    }

    public void setDisplayDetails(DisplayDetailsDTO displayDetails) {
        this.displayDetails = displayDetails;
    }
}
