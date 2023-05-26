package eu.europa.ec.empl.edci.issuer.common.model;

public class EuropeanDigitalCredentialUploadResultDTO extends EuropeanDigitalCredentialUploadDTO {

    String fileName;
    boolean signed = false;
    boolean badFormat = false;
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
}
