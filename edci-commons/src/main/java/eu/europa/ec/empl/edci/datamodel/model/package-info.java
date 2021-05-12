@XmlSchema(
        elementFormDefault = XmlNsForm.UNQUALIFIED,
        namespace = "http://data.europa.eu/snb",
        location = "http://data.europa.eu/snb ",
        xmlns = {
                @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance"),
                @XmlNs(prefix = "", namespaceURI = "http://data.europa.eu/snb"),
                @XmlNs(prefix = "cred", namespaceURI = EuropassConstants.NAMESPACE_CRED_URI),
                @XmlNs(prefix = "ds", namespaceURI = "http://www.w3.org/2000/09/xmldsig#")
        }
)
package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.EuropassConstants;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;