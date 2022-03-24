package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.EDCIResourceResolver;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.listener.EDCIJAXBMarshalListener;
import eu.europa.ec.empl.edci.datamodel.listener.EDCIJAXBUnmarshalListener;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class XmlUtil {

    protected static final Logger logger = Logger.getLogger(XmlUtil.class);

    private JAXBContext europassCredentialJAXBContext;

    private JAXBContext verifiablePresentationJAXBContext;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIJAXBUnmarshalListener edcijaxbUnmarshalListener;

    @Autowired
    private EDCIJAXBMarshalListener edciJAXBMarshalListener;

    @Autowired
    public XmlUtil() {
        try {
            this.europassCredentialJAXBContext = this.getJAXBContext(EuropassCredentialDTO.class);
            this.verifiablePresentationJAXBContext = this.getJAXBContext(EuropassPresentationDTO.class);
        } catch (JAXBException e) {
            logger.error("Could not preload JAXB contexts");
        }
    }

    public ValidationResult isValid(File file, SchemaLocation xsdurl, Class clazz) {
        ValidationResult validationResult = isValid(new StreamSource(file), xsdurl, clazz);
        //If it is an invalid result from URL, try getting local resource
        if (!validationResult.isValid() && xsdurl.isValidURL()) {
            xsdurl.setLocation(xsdurl.getLocation().substring(xsdurl.getLocation().lastIndexOf('/')));
            validationResult = isValid(new StreamSource(file), xsdurl, clazz);
        }
        return validationResult;
    }

    public ValidationResult isValid(InputStream inputStream, SchemaLocation xsdurl, Class clazz) {
        ValidationResult validationResult = isValid(new StreamSource(inputStream), xsdurl, clazz);
        //If it is an invalid result from URL, try getting local resource
        if (!validationResult.isValid() && xsdurl.isValidURL()) {
            xsdurl.setLocation(xsdurl.getLocation().substring(xsdurl.getLocation().lastIndexOf('/')));
            validationResult = isValid(new StreamSource(inputStream), xsdurl, clazz);
        }
        return validationResult;
    }

    private ValidationResult isValid(Source source, SchemaLocation xsdurl, Class clazz) {
        ValidationResult validationResult = new ValidationResult();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new EDCIResourceResolver());
        InputStream is = null;
        try {
            Schema xmlSchema = null;
            //If it is a valid URL, use it, otherwise check for local file
            if (xsdurl.isValidURL()) {
                schemaFactory.newSchema(xsdurl.getLocationURL());
            } else {
                is = clazz.getClassLoader().getResourceAsStream(xsdurl.getLocation());
                xmlSchema = schemaFactory.newSchema(new StreamSource(is));
            }
            javax.xml.validation.Validator validator = xmlSchema.newValidator();
            validator.validate(source);
            validationResult.setValid(true);
        } catch (SAXParseException e) {
            logger.error(String.format("Object of type [%s] is not a valid XML", clazz.getName()), e);
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage(String.format("%s [%s: %d/ %s: %d]",
                    e.getLocalizedMessage(),
                    edciMessageService.getMessage(EDCIMessageKeys.Exception.Global.GLOBAL_LINE),
                    e.getLineNumber(),
                    edciMessageService.getMessage(EDCIMessageKeys.Exception.Global.GLOBAL_COLUMN),
                    e.getColumnNumber()));

            validationResult.addValidationError(validationError);
            validationResult.setValid(false);
        } catch (SAXException | IOException e) {
            logger.error(e);
            validationResult.setValid(false);
            validationResult.addValidationError(new ValidationError(e.getLocalizedMessage()));
        } finally {
            if (is != null) {
                try {
                    is.close();

                } catch (Exception e) {
                    logger.error("Problem closing input stream");
                }
            }
        }
        return validationResult;
    }

    /*INPUT STREAM METHODS*/
    public <T> T fromInputStream(InputStream inputStream, Class<T> clazz) throws JAXBException, IOException {
        T object = null;
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8.toString());
            String originalXML = writer.toString();
            object = fromString(originalXML, clazz);

            if (object != null && !clazz.isAssignableFrom(object.getClass())) {
                throw new EDCIBadRequestException().addDescription("Unmarshalled object " + object.getClass() + " does not match the expected class " + clazz);
            }
        }
        return object;
    }

    /*BYTE METHODS*/
    public <T> T fromBytes(byte[] bytes, Class<T> clazz) throws JAXBException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return fromInputStream(byteArrayInputStream, clazz);
    }

    /*XML METHODS*/
    public <T> T fromString(String xmlString, Class<T> clazz) throws JAXBException {
        T object = null;
        Unmarshaller unmarshaller = this.getUnMarshaller(clazz);
        StringReader stringReader = new StringReader(xmlString);
        object = (T) unmarshaller.unmarshal(stringReader);
        return object;
    }

    public String toXML(Object object, Class clazz) throws JAXBException {
        String xml = null;
        try (StringWriter stringWriter = new StringWriter()) {
            Marshaller marshaller = this.getMarshaller(clazz);
            marshaller.marshal(object, stringWriter);
            xml = stringWriter.toString();
        } catch (IOException e) {
            throw new EDCIException();
        }
        return xml;
    }

    /*INNER UTIL METHODS*/
    //Get JAXBContext, if an EuropassCredential or EuropassPresentation JAXB Context already exists, use already existing one
    protected JAXBContext getJAXBContext(Class clazz) throws JAXBException {
        if (clazz.equals(EuropassCredentialDTO.class) && this.europassCredentialJAXBContext != null) {
            return this.europassCredentialJAXBContext;
        } else if (clazz.equals(EuropassPresentationDTO.class) && this.verifiablePresentationJAXBContext != null) {
            return this.verifiablePresentationJAXBContext;
        } else {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(MarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
            JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{clazz}, properties);
            return jaxbContext;
        }
    }

    public Marshaller getMarshallerWithSchemaLocation(Class clazz, String schemaLocation) throws JAXBException {
        Marshaller marshaller = this.getMarshaller(clazz);
        if (schemaLocation != null) {
            //TODO vp, EuropassCredentialDTO.class.getPackage() or clazz.getPackage()
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, EuropassCredentialDTO.class.getPackage().getAnnotation(XmlSchema.class).location().concat(schemaLocation));
        }
        return marshaller;
    }

    public Marshaller getMarshaller(Class clazz) throws JAXBException {
        JAXBContext jaxbContext = getJAXBContext(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setListener(edciJAXBMarshalListener);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return jaxbMarshaller;
    }

    public Unmarshaller getUnMarshaller(Class clazz) throws JAXBException {
        JAXBContext jaxbContext = getJAXBContext(clazz);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setListener(edcijaxbUnmarshalListener);
        return jaxbUnmarshaller;
    }

    public <T> byte[] toByteArray(T object) throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Marshaller jaxbMarshaller = getMarshaller(object.getClass());
        jaxbMarshaller.marshal(object, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public SchemaLocation getFirstSchemaLocation(byte[] xmlBytes) throws
            IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        List<SchemaLocation> schemaLocations = this.getSchemaLocations(xmlBytes);
        return !schemaLocations.isEmpty() ? schemaLocations.get(0) : null;
    }

    public SchemaLocation getUniqueSchemaLocation(byte[] xmlBytes) throws
            IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        List<SchemaLocation> schemaLocations = this.getSchemaLocations(xmlBytes);
        return schemaLocations.size() == 1 ? schemaLocations.get(0) : null;
    }

    public List<SchemaLocation> getSchemaLocations(byte[] xmlBytes) throws
            IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document credentialDocument = builder.parse(new ByteArrayInputStream(xmlBytes));
        XPathExpression expr = xpath.compile("//@schemaLocation");

        Node schemaLocationNode = (Node) expr.evaluate(credentialDocument, XPathConstants.NODE);
        String[] schemaLocationArray = schemaLocationNode.getNodeValue().split("\\s");

        List<SchemaLocation> schemaLocations = new ArrayList<>();

        for (int i = 1; i < schemaLocationArray.length; i = i + 2) {
            SchemaLocation schemaLocation = new SchemaLocation();
            schemaLocation.setNamespace(schemaLocationArray[i - 1]);
            schemaLocation.setLocation(schemaLocationArray[i]);
            schemaLocations.add(schemaLocation);
        }

        return schemaLocations;
    }
}
