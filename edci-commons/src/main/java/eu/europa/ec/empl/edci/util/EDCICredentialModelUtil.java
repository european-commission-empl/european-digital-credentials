package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.XML;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.Localizable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.DownloadableObject;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeUtility;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCICredentialModelUtil {

    protected static final Logger logger = Logger.getLogger(EDCICredentialModelUtil.class);

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private IConfigService iConfigService;

    public Locale guessCredentialLocale(EuropassCredentialDTO europassCredentialDTO) {
        Locale locale = null;
        //ToDo -> Trim values?
        //Try getting primary language
        if (europassCredentialDTO.getPrimaryLanguage() != null) {
            locale = LocaleUtils.toLocale(europassCredentialDTO.getPrimaryLanguage());
            //check for any available language
        } else if (europassCredentialDTO.getAvailableLanguages() != null && !europassCredentialDTO.getAvailableLanguages().isEmpty()) {
            locale = LocaleUtils.toLocale(europassCredentialDTO.getAvailableLanguages().stream().findFirst().orElse(null));
        } else {
            //return any context locale
            locale = LocaleContextHolder.getLocale();
        }
        return locale;
    }

    public String getFileName(CredentialHolderDTO credentialHolderDTO, String locale) {
        return this.getFileName(credentialHolderDTO.getCredential(), locale);
    }

    public String getFileName(EuropassCredentialDTO europassCredentialDTO, String locale) {
        String fileName = europassCredentialDTO.getCredentialSubject().getFullName().getLocalizedStringOrAny(locale)
                .concat(" - ")
                .concat(europassCredentialDTO.getTitle().getLocalizedStringOrAny(locale))
                .concat(XML.EXTENSION_XML);
        fileName = fileName.replaceAll("[:\\\\/*?|<>]", "_");
        return fileName;
    }

    public String getEncodedFileName(CredentialHolderDTO credentialHolderDTO, String locale) {
        return this.getEncodedFileName(credentialHolderDTO.getCredential(), locale);
    }

    public String getEncodedFileName(EuropassCredentialDTO europassCredentialDTO, String locale) {
        String fileName = this.getFileName(europassCredentialDTO, locale);
        try {
            fileName = MimeUtility.encodeText(fileName, StandardCharsets.UTF_8.name(), null);
        } catch (UnsupportedEncodingException e) {
            logger.error("error encodig attached fileName");
        }
        return fileName;
    }

    public String getPrimaryLanguage(byte[] xml) throws JAXBException, IOException {
        return this.fromByteArray(xml).getCredential().getPrimaryLanguage();
    }

    public String getSchemaLocation(Class<? extends CredentialHolderDTO> presentationClass, String type) {

        String schemaLocation = null;

        if (EuropassPresentationDTO.class.isAssignableFrom(presentationClass)) {
            schemaLocation = iConfigService.getString(EDCIConfig.VP_SCHEMA_LOCATION);
        } else {
            //TODO vp, what here?
            schemaLocation = iConfigService.getString(EDCIConfig.CREDENTIAL_SCHEMA_LOCATION);
        }

        return schemaLocation;

    }

    public Code getTypeCode(Class<? extends CredentialHolderDTO> presentationClass, String type) {

        Code credentialType = null;
        ControlledList clType = null;

        if (EuropassPresentationDTO.class.isAssignableFrom(presentationClass)) {
            clType = ControlledList.VERIFICATION_TYPE;
        } else {
            clType = ControlledList.CREDENTIAL_TYPE;
        }

        credentialType = controlledListCommonsService.searchConceptByUri(clType.getUrl(), type, LocaleContextHolder.getLocale().toString());

        return credentialType;

    }

    public CredentialHolderDTO fromXML(String europassCredentialXML) throws JAXBException {
        CredentialHolderDTO europassCredentialDTO = fromString(europassCredentialXML);
        europassCredentialDTO.getCredential().setOriginalXML(europassCredentialXML);
        return europassCredentialDTO;
    }

    public String toXML(CredentialHolderDTO credentialHolderDTO) throws JAXBException {
        if (credentialHolderDTO instanceof EuropassPresentationDTO) {
            return xmlUtil.toXML(credentialHolderDTO, EuropassPresentationDTO.class);
        } else {
            return xmlUtil.toXML(credentialHolderDTO, EuropassCredentialDTO.class);
        }
    }

    public String toXML(EuropassCredentialDTO europassCredentialDTO) throws JAXBException {
        return xmlUtil.toXML(europassCredentialDTO, EuropassCredentialDTO.class);
    }

    public String toXML(EuropassPresentationDTO europassPresentationDTO) throws JAXBException {
        return xmlUtil.toXML(europassPresentationDTO, EuropassPresentationDTO.class);
    }

    public List<String> generateXMLSubCreds(EuropassCredentialDTO europassCredentialDTO) {
        List<String> xmlStrings = new ArrayList<String>();
        if (europassCredentialDTO.getContains() != null) {
            for (DownloadableObject subCredObject : europassCredentialDTO.getContains()) {
                if (subCredObject.getContent() != null && subCredObject.getContent().length > 0) {
                    String xmlString = new String(subCredObject.getContent(), StandardCharsets.UTF_8);
                    xmlStrings.add(xmlString);
                }
            }
        }
        return xmlStrings;
    }

    public List<EuropassCredentialDTO> parseSubCredentials(List<String> subCredentialsXMLStrings) throws JAXBException {
        List<EuropassCredentialDTO> europassCredentialDTOS = new ArrayList<EuropassCredentialDTO>();
        if (subCredentialsXMLStrings != null) {
            for (String subCredentialXML : subCredentialsXMLStrings) {
                europassCredentialDTOS.add(fromXML(subCredentialXML).getCredential());
            }
        }
        return europassCredentialDTOS;
    }

    public CredentialHolderDTO fromFile(File file) throws JAXBException, IOException {

        return fromByteArray(FileUtils.readFileToByteArray(file));

    }

    public CredentialHolderDTO fromString(String stringCredential) throws JAXBException {

        CredentialHolderDTO cred = null;

        try {
            cred = xmlUtil.fromString(stringCredential, EuropassCredentialDTO.class);
        } catch (JAXBException exception) {
            cred = xmlUtil.fromString(stringCredential, EuropassPresentationDTO.class);
        }

        return cred;

    }

    public CredentialHolderDTO fromByteArray(byte[] byteCredential) throws JAXBException, IOException {

        CredentialHolderDTO cred = null;

        try {
            cred = xmlUtil.fromBytes(byteCredential, EuropassCredentialDTO.class);

            //TODO DELETE THIS as soon as Arhs downloads the credential by accepting a XML instead of Json
            ((EuropassCredentialDTO) cred).setOriginalXML(new String(byteCredential, StandardCharsets.UTF_8));

        } catch (JAXBException exception) {
            cred = xmlUtil.fromBytes(byteCredential, EuropassPresentationDTO.class);
        }

        return cred;

    }

    public CredentialHolderDTO fromInputStream(InputStream inputStream) throws JAXBException, IOException {

        CredentialHolderDTO cred = null;
        InputStream inputStreamAux = inputStream;

        //TODO: check the input comparing it to an XSD instead of using a try catch method
        try {
            try {

                if (inputStream != null && !inputStream.markSupported()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    org.apache.commons.io.IOUtils.copy(inputStream, baos);
                    byte[] bytes = baos.toByteArray();
                    inputStreamAux = new ByteArrayInputStream(bytes);
                }

                cred = xmlUtil.fromInputStream(inputStreamAux, EuropassCredentialDTO.class);
            } catch (JAXBException exception) {
                inputStreamAux.reset();
                cred = xmlUtil.fromInputStream(inputStreamAux, EuropassPresentationDTO.class);
            }
        } finally {
            try {
                inputStreamAux.close();
            } catch (IOException e) {
                throw new EDCIException().addDescription("[E] - error in final input stream close").setCause(e);
            }
        }

        return cred;
    }

    /*PERSENTATION METHODS*/
    public EuropassPresentationDTO toVerifiablePresentation(CredentialHolderDTO credentialHolder, List<VerificationCheckDTO> verificationDTOList) {
        //TODO vp confirm this when we have a VP. Do we return the VP immediatelly?
        EuropassPresentationDTO europassPresentationDTO = new EuropassPresentationDTO();
        europassPresentationDTO.setType(getTypeCode(EuropassPresentationDTO.class, ControlledListConcept.VERIFICATION_TYPE_SHARED.getUrl()));
        //TODO vp: when signing from the wallet, we will set the issuer with the certificate data
        europassPresentationDTO.setIssuer(credentialHolder instanceof EuropassPresentationDTO ? credentialHolder.getIssuer() : null);
        europassPresentationDTO.setVerifiableCredential(credentialHolder.getCredential());
        europassPresentationDTO.setVerifications(verificationDTOList == null ? new ArrayList<>() : verificationDTOList);
        europassPresentationDTO.setId(URI.create(europassPresentationDTO.getPrefix(europassPresentationDTO).concat(UUID.randomUUID().toString())));
        return europassPresentationDTO;
    }

    public String toCloneJSON(Object object, Class clazz) throws JAXBException {
        return jsonUtil.getGsonContext().toJson(object, clazz);
    }

    public <T> T fromCloneJSON(String xmlString, Class<T> clazz) throws JAXBException {
        return jsonUtil.getGsonContext().fromJson(xmlString, clazz);
    }

    /*CLONE METHODS*/
    public <T> T cloneModel(T model) throws JAXBException, IOException {
        String xml = toCloneJSON(model, model.getClass());
        T clonedModel = (T) fromCloneJSON(xml, model.getClass());
        return clonedModel;
    }

    /*CLONE ARRAY METHODS*/
    public <T> List<T> cloneArrayModel(T... models) throws JAXBException, IOException {
        List<T> list = new ArrayList<>();
        for (T instance : models) {
            list.add(cloneModel(instance));
        }
        return list;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public void setXmlUtil(XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
    }

    /**
     * DATATYPES UTIL METHODS
     **/
    public Set<String> getAllUniqueLanguages(Localizable... localizables) {
        Set<String> languages = Arrays.stream(localizables)
                .filter(localizable -> localizable != null)
                .map(localizable -> localizable.getContents())
                .flatMap(contents -> contents.stream())
                .map(content -> content.getLanguage())
                .collect(Collectors.toSet());
        return languages;
    }
}
