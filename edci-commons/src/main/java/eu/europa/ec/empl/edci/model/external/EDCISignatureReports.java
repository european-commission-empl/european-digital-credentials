package eu.europa.ec.empl.edci.model.external;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class EDCISignatureReports {

    private int validSignaturesCount;
    private int signaturesCount;
    private List<String> usedCertificatesCommonNames;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private Date expiryDate;

    private List<EDCISignatureSimpleReport> reports;

    public int getValidSignaturesCount() {
        return validSignaturesCount;
    }

    public void setValidSignaturesCount(int validSignaturesCount) {
        this.validSignaturesCount = validSignaturesCount;
    }

    public int getSignaturesCount() {
        return signaturesCount;
    }

    public void setSignaturesCount(int signaturesCount) {
        this.signaturesCount = signaturesCount;
    }

    public List<EDCISignatureSimpleReport> getReports() {
        return reports;
    }

    public void setReports(List<EDCISignatureSimpleReport> reports) {
        this.reports = reports;
    }

    public List<String> getUsedCertificatesCommonNames() {
        return usedCertificatesCommonNames;
    }

    public void setUsedCertificatesCommonNames(List<String> usedCertificatesCommonNames) {
        this.usedCertificatesCommonNames = usedCertificatesCommonNames;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
