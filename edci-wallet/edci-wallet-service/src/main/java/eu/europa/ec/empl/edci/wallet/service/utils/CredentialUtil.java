package eu.europa.ec.empl.edci.wallet.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialUtil {
    private static final Logger logger = Logger.getLogger(CredentialUtil.class);

    @Autowired
    private Validator validator;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    public List<CredentialLocalizableInfoDTO> getLocalizableInfoDTOS(EuropassCredentialDTO europassCredentialDTO, CredentialDTO credentialDTO) {

        Text title = europassCredentialDTO.getTitle();
        Note description = europassCredentialDTO.getDescription();
        Text typeNames = europassCredentialDTO.getType().getTargetName();

        Set<String> availableLanguages = edciCredentialModelUtil.getAllUniqueLanguages(title, description, typeNames);

        List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS = new ArrayList<CredentialLocalizableInfoDTO>();

        for (String language : availableLanguages) {
            CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = new CredentialLocalizableInfoDTO();
            credentialLocalizableInfoDTO.setLang(language);
            credentialLocalizableInfoDTO.setTitle(title.getStringContent(language));
            credentialLocalizableInfoDTO.setCredentialType(typeNames.getStringContent(language));
            credentialLocalizableInfoDTO.setCredentialDTO(credentialDTO);
            //description is optional
            if (validator.notEmpty(description))
                credentialLocalizableInfoDTO.setDescription(description.getStringContent(language));
            credentialLocalizableInfoDTOS.add(credentialLocalizableInfoDTO);
        }

        return credentialLocalizableInfoDTOS;
    }

    public String replaceMailWildCards(String originalString, String fullName, String issuer, String credentialName, String europassURL) {
        return originalString.replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_FULLNAME), fullName)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_ISSUER), issuer)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_CREDENTIALNAME), credentialName)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_EUROPASSURL), europassURL);
    }


    /*UTIL METHODS*/
    public HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }

    public EuropassPresentationDTO buildEuropassVerifiablePresentation(CredentialDTO credentialDTOS, boolean onlyRetrieveVP) {

        return buildEuropassVerifiablePresentation(credentialDTOS.getCredentialXML(), onlyRetrieveVP);
    }

    public EuropassPresentationDTO buildEuropassVerifiablePresentation(byte[] credentialXMLs, boolean onlyRetrieveVP) {

        EuropassPresentationDTO verifiablePresentationDTO = null;

        try {

            CredentialHolderDTO holder = edciCredentialModelUtil.fromByteArray(credentialXMLs);

            if (holder instanceof EuropassPresentationDTO && onlyRetrieveVP) {
                verifiablePresentationDTO = (EuropassPresentationDTO) holder;
            } else {
                //TODO vp, que passa amb el issuer de la VP si estem recuperant-ne una?
                verifiablePresentationDTO = edciCredentialModelUtil.toVerifiablePresentation(holder, europassCredentialVerifyUtil.verifyCredential(credentialXMLs));
            }
        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        try {
            if (logger.isTraceEnabled()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                logger.trace(mapper.writeValueAsString(verifiablePresentationDTO));
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return verifiablePresentationDTO;
    }


    /**
     * Get a PKCS12 signature token stored at resources/signCredential/keystore.p12.
     *
     * @return SignatureTokenConnection
     */
    protected SignatureTokenConnection getPkcs12Token() {

        Pkcs12SignatureToken jksSignatureToken = null;

        try {
            InputStream is = servletContext.getResourceAsStream(
                    walletConfigService.getString("signature.cert.keystore.file",
                            "/WEB-INF/keystores/dummyKeystore.p12"));
            jksSignatureToken = new Pkcs12SignatureToken(is, new KeyStore.PasswordProtection(
                    walletConfigService.getString("signature.cert.keystore.password",
                            "test").toCharArray()));
        } catch (Exception e) {
            throw new EDCIException().addDescription("Could not load the signature keystore").setCause(e);
        }

        return jksSignatureToken;
    }

    public boolean validateExpiry(Date expirationDate) {
        Date today = new Date();
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("Today: %s, Expiration Date: %s - ExpirationDate > Today: %s", today, expirationDate, expirationDate.after(today)));
        }
        return expirationDate.after(today);
    }

}
