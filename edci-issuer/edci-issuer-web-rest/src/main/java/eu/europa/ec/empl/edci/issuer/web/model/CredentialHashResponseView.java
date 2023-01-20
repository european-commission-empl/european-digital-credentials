package eu.europa.ec.empl.edci.issuer.web.model;

public class CredentialHashResponseView {
    private String toBeSigned;

    public CredentialHashResponseView(){

    }

    public String getToBeSigned() {
        return toBeSigned;
    }

    public void setToBeSigned(String toBeSigned) {
        this.toBeSigned = toBeSigned;
    }

}
