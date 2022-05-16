package eu.europa.ec.empl.edci.datamodel.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "europassCredentials")
@XmlAccessorType(XmlAccessType.FIELD)
public class MultipleEuropassCredentialDTO {

    private List<EuropassCredentialDTO> europassCredential = new ArrayList<>();

    public List<EuropassCredentialDTO> getEuropassCredential() {
        return europassCredential;
    }

    public void setEuropassCredential(List<EuropassCredentialDTO> europassCredential) {
        this.europassCredential = europassCredential;
    }

}