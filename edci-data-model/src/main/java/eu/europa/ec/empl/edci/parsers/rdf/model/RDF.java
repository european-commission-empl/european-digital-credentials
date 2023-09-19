package eu.europa.ec.empl.edci.parsers.rdf.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;


@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType(XmlAccessType.FIELD)
public class RDF implements Serializable {

    //https://stackoverflow.com/questions/21351980/jaxb-unmarshaller-cannot-find-element-with-namespace-and-prefix
    //https://stackoverflow.com/questions/28110109/xml-element-with-attribute-has-in-jaxb

    @XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private List<RDFDescription> list;

    //Field used to identify an error row
    private boolean error = false;

    public RDF() {
    }

    public List<RDFDescription> getList() {
        return list;
    }

    public void setList(List<RDFDescription> list) {
        this.list = list;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
