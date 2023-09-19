package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;

public class EuropeanDigitalCredentialUploadResultDTO extends EuropeanDigitalCredentialUploadDTO {

    String fileName;
    boolean signed = false;
    boolean badFormat = false;
    boolean errorAddress = false;
    String badFormatDesc;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public boolean isBadFormat() {
        return badFormat;
    }

    public void setBadFormat(boolean badFormat) {
        this.badFormat = badFormat;
    }

    public String getBadFormatDesc() {
        return badFormatDesc;
    }

    public void setBadFormatDesc(String badFormatDesc) {
        this.badFormatDesc = badFormatDesc;
    }

    public boolean isErrorAddress() {
        return errorAddress;
    }

    public void setErrorAddress(boolean errorAddress) {
        this.errorAddress = errorAddress;
    }
}
