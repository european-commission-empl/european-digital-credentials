package eu.europa.ec.empl.edci.dss.model.signature;

import eu.europa.esig.dss.model.ToBeSigned;

import java.util.Date;

public class DSSEDCIToBeSignedDTO extends ToBeSigned {

    private Date signingDate;
    private DSSSignatureDocumentForm signatureDocumentForm;

    public DSSEDCIToBeSignedDTO(ToBeSigned toBeSigned) {
        super(toBeSigned.getBytes());
    }

    public Date getSigningDate() {
        return signingDate;
    }

    public void setSigningDate(Date signingDate) {
        this.signingDate = signingDate;
    }

    public DSSSignatureDocumentForm getSignatureDocumentForm() {
        return signatureDocumentForm;
    }

    public void setSignatureDocumentForm(DSSSignatureDocumentForm signatureDocumentForm) {
        this.signatureDocumentForm = signatureDocumentForm;
    }
}
