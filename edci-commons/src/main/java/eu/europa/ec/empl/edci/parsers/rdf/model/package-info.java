@XmlSchema(
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        xmlns = {@XmlNs(prefix = "rdf",
                namespaceURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                @XmlNs(prefix = "dc",
                        namespaceURI = "http://purl.org/dc/elements/1.1/"),
                @XmlNs(prefix = "skos",
                        namespaceURI = "http://www.w3.org/2004/02/skos/core#"),
                @XmlNs(prefix = "skosXL",
                        namespaceURI = "http://www.w3.org/2008/05/skos-xl#"),
                @XmlNs(prefix = "nsTerms",
                        namespaceURI = "http://purl.org/dc/terms/"),
                @XmlNs(prefix = "nsOnthAuth",
                        namespaceURI = "http://publications.europa.eu/ontology/authority/"),
                @XmlNs(prefix = "nsResAuth",
                        namespaceURI = "http://publications.europa.eu/resource/authority/")}
)
package eu.europa.ec.empl.edci.parsers.rdf.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
