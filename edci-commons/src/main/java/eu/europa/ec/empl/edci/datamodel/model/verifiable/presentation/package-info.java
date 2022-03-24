@XmlSchema(
        elementFormDefault = XmlNsForm.UNQUALIFIED,
        namespace = "http://data.europa.eu/snb",
        location = "http://data.europa.eu/snb/vp",
        xmlns = {

                @XmlNs(prefix = "vp", namespaceURI = EDCIConstants.NAMESPACE_VP_DEFAULT),

                @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance"),
                @XmlNs(prefix = "", namespaceURI = "http://data.europa.eu/snb"),
                @XmlNs(prefix = "ds", namespaceURI = "http://www.w3.org/2000/09/xmldsig#"),
                @XmlNs(prefix = "cred", namespaceURI = EDCIConstants.NAMESPACE_CRED_URI)
        }
)
package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import eu.europa.ec.empl.edci.constants.EDCIConstants;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;