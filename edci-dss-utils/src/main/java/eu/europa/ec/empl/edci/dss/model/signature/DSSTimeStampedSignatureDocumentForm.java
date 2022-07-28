package eu.europa.ec.empl.edci.dss.model.signature;

import java.util.Date;

public class DSSTimeStampedSignatureDocumentForm extends DSSSignatureDocumentForm {

    private Date signingDate;

    public DSSTimeStampedSignatureDocumentForm() {

    }

    @Override
    public Date getSigningDate() {
        return signingDate;
    }

    public void setSigningDate(Date signingDate) {
        this.signingDate = signingDate;
    }
}
