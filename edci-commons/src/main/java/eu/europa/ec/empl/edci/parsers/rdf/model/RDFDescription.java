package eu.europa.ec.empl.edci.parsers.rdf.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class RDFDescription implements Serializable {

    @XmlAttribute(namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", name = "about")
    private String uri;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "prefLabel")
    private List<RDFLabel> targetName; //targetFramework on Root level

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "definition")
    private List<RDFLabel> targetDescription;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "inScheme")
    private RDFResource targetFrameworkURI;

    @XmlElement(namespace = "http://publications.europa.eu/ontology/authority/", name = "deprecated")
    private Boolean deprecated;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "narrower")
    private List<RDFResource> narrower;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "external")
    private RDFResource external;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#", name = "hasTopconcept")
    private RDFResource hasTopconcept;

    //Only used on root level (Informed manually when every element is recovered)
//    @XmlElement(namespace = "http://publications.europa.eu/ontology/authority/", name = "table.id")
    private String targetNotation;

    private List<RDFLabel> targetFramework;

    public RDFDescription() {
    }

    public RDFResource getHasTopconcept() {
        return hasTopconcept;
    }

    public void setHasTopconcept(RDFResource hasTopconcept) {
        this.hasTopconcept = hasTopconcept;
    }

    public String getUri() {
        return uri;
    }

    public List<RDFResource> getNarrower() {
        return narrower;
    }

    public void setNarrower(List<RDFResource> narrower) {
        this.narrower = narrower;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<RDFLabel> getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(List<RDFLabel> targetFramework) {
        this.targetFramework = targetFramework;
    }

    public List<RDFLabel> getTargetName() {
        return targetName;
    }

    public void setTargetName(List<RDFLabel> targetName) {
        this.targetName = targetName;
    }

    public List<RDFLabel> getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(List<RDFLabel> targetDescription) {
        this.targetDescription = targetDescription;
    }

    public RDFResource getTargetFrameworkURI() {
        return targetFrameworkURI;
    }

    public void setTargetFrameworkURI(RDFResource targetFrameworkURI) {
        this.targetFrameworkURI = targetFrameworkURI;
    }

    public String getTargetNotation() {
        return targetNotation;
    }

    public void setTargetNotation(String targetNotation) {
        this.targetNotation = targetNotation;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public RDFResource getExternal() {
        return external;
    }

    public void setExternal(RDFResource external) {
        this.external = external;
    }

    //    public RDFResource getIsReplacedBy() {
//        return isReplacedBy;
//    }
//
//    public void setIsReplacedBy(RDFResource isReplacedBy) {
//        this.isReplacedBy = isReplacedBy;
//    }
//
//    public RDFResource getReplaces() {
//        return replaces;
//    }
//
//    public void setReplaces(RDFResource replaces) {
//        this.replaces = replaces;
//    }
}