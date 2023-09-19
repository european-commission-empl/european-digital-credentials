package eu.europa.ec.empl.edci.wallet.service.utils;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.util.ExternalServicesUtil;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialStorageUtil {
    private static final Logger logger = LogManager.getLogger(CredentialStorageUtil.class);

    @Autowired
    private Validator validator;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private ExternalServicesUtil externalServicesUtil;

    @Autowired
    private CredentialRepository credentialRepository;

    public List<CredentialLocalizableInfoDTO> getLocalizableInfoDTOS(EuropeanDigitalCredentialDTO europassCredentialDTO, CredentialDTO credentialDTO) {

        LiteralMap title = europassCredentialDTO.getDisplayParameter().getTitle();
        LiteralMap description = europassCredentialDTO.getDisplayParameter().getDescription();
        List<ConceptDTO> typeNames = europassCredentialDTO.getCredentialProfiles();

        List<String> availableLanguages = controlledListCommonsService.searchLanguageISO639ByConcept(europassCredentialDTO.getDisplayParameter().getLanguage());

        List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS = new ArrayList<CredentialLocalizableInfoDTO>();

        for (String language : availableLanguages) {
            CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = new CredentialLocalizableInfoDTO();
            credentialLocalizableInfoDTO.setLang(language);
            credentialLocalizableInfoDTO.setTitle(MultilangFieldUtil.getLiteralStringOrAny(title, language));
            credentialLocalizableInfoDTO.getCredentialProfile().addAll(typeNames.stream().map(profile ->
                    MultilangFieldUtil.getLiteralStringOrAny(profile.getPrefLabel(), language)).collect(Collectors.toList()));
            credentialLocalizableInfoDTO.setCredential(credentialDTO);
            //description is optional
            if (validator.notEmpty(description)) {
                credentialLocalizableInfoDTO.setDescription(MultilangFieldUtil.getLiteralStringOrAny(description, language));
            }
            credentialLocalizableInfoDTOS.add(credentialLocalizableInfoDTO);
        }

        return credentialLocalizableInfoDTOS;
    }


    public File getOrCreateFolder(String folderName) {

        File folder = edciFileService.getOrCreateFile(folderName);

        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdir()) {
                logger.error("Wallet's folder couldn't be created: " + folderName);
                throw new EDCIException().addDescription("Wallet's folder couldn't be created");
            }
        }

        return folder;
    }

    public void deleteWalletStorage(List<WalletDAO> walletDAOS) {
        walletDAOS.stream().forEach(this::deleteWalletStorage);
    }

    public void deleteWalletStorage(String folder, List<CredentialDAO> credentialDAOList) {
        if (folder != null) {
            try {
                for(CredentialDAO cred : credentialDAOList) {
                    if(cred.getFile() != null) {
                        this.removeCredentialFromFileSystem(folder, cred.getFile());
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("Error removing credentials in wallet folder %s", folder), e);
            }
        }
    }

    public void deleteWalletStorage(WalletDAO walletDAO) {
        this.deleteWalletStorage(walletDAO.getFolder(), walletDAO.getCredentialDAOList());
    }

    public String createCredentialFileName() {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid.replaceAll("[^A-Za-z0-9]", "").concat(EDCIConstants.JSON.EXTENSION_JSON_LD);
        return fileName;
    }

    public byte[] getCredentialFromFileSystem(CredentialDTO credential) {
        if (credential.getFile() == null && credential.getCredential() != null) {
            return convertAndStoreXMLCredential(credential);
        }
        return getCredentialFromFileSystem(credential.getWallet().getFolder(), credential.getFile());
    }

    public byte[] getCredentialFromFileSystem(CredentialDAO credential) {
        if (credential.getFile() == null && credential.getCredential() != null) {
            return convertAndStoreXMLCredential(credential);
        }
        return getCredentialFromFileSystem(credential.getWallet().getFolder(), credential.getFile());
    }

    public void removeCredentialFromFileSystem(String walletFolder, String credentialFile) {
        try {
            File folder = getOrCreateFolder(walletService.getWalletPrivateFolderName(walletFolder));
            Path credentialPath = edciFileService.getFileAsPath(folder.getAbsolutePath().concat(EDCIConstants.StringPool.STRING_SLASH).concat(credentialFile));
            if (Files.exists(credentialPath)) {
                try {
                    Files.delete(credentialPath);
                } catch (IOException e) {
                    throw new EDCIException("Credential couldn't be removed from the file system").setCause(e);
                }
            }
        } catch (Exception e) {
            logger.error("Credential " + credentialFile + "doesn't exists in " + walletFolder, e);
        }
    }

    public byte[] getCredentialFromFileSystem(String walletFolder, String credentialFile) {

        byte[] credBytes = null;

        File folder = getOrCreateFolder(walletService.getWalletPrivateFolderName(walletFolder));
        Path credentialPath = edciFileService.getFileAsPath(folder.getAbsolutePath().concat(EDCIConstants.StringPool.STRING_SLASH).concat(credentialFile));
        if (Files.exists(credentialPath)) {
            try {
                credBytes = Files.readAllBytes(credentialPath);
            } catch (IOException e) {
                throw new EDCIException("Credential couldn't be retireved from the file system").setCause(e);
            }
        } else {
            throw new EDCIException("Credential can't be retireved");
        }

        return credBytes;
    }

    public String storeCredentialIntoFilesystem(String walletFolder, byte[] credential) {

        File credFile = null;
        try {

            if (StringUtils.isEmpty(walletFolder)) {
                throw new EDCIException(EDCIMessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE).addDescription("Wallet's folder not provided");
            }

            File folder = getOrCreateFolder(walletService.getWalletPrivateFolderName(walletFolder));
            credFile = edciFileService.getOrCreateFile(folder.getAbsolutePath().concat(File.separator).concat(createCredentialFileName()));
            Path credFilePath = Paths.get(credFile.getAbsolutePath());
            Files.deleteIfExists(credFilePath);
            Files.write(credFilePath, credential);
        } catch (IOException e) {
            throw new EDCIException(EDCIMessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE).setCause(e);
        }
        return credFile.getName();

    }

    public String replaceMailWildCards(String originalString, String fullName, String issuer, String credentialName, String europassURL) {
        return originalString.replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_FULLNAME), fullName)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_ISSUER), issuer)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_CREDENTIALNAME), credentialName)
                .replaceAll(Pattern.quote(EDCIWalletConstants.MAIL_WILDCARD_EUROPASSURL), europassURL);
    }


    /*UTIL METHODS*/
    public HttpHeaders prepareHttpHeadersForFile(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }

//    public EuropassPresentationDTO buildEuropassVerifiablePresentation(CredentialDTO credentialDTOS) {
//
//        return buildEuropassVerifiablePresentation(credentialDTOS.getCredentialXML());
//    }

//    public EuropassPresentationDTO buildEuropassVerifiablePresentation(byte[] credentialXMLs) {
//
//        EuropassPresentationDTO verifiablePresentationDTO = null;
//
//        try {
//
//            CredentialHolderDTO holder = edciCredentialModelUtil.fromByteArray(credentialXMLs);
//
//            //TODO vp, que passa amb el issuer de la VP si estem recuperant-ne una?
//            verifiablePresentationDTO = edciCredentialModelUtil.toVerifiablePresentation(holder, europassCredentialVerifyUtil.verifyCredential(credentialXMLs));
//        } catch (JAXBException e) {
//            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
//        } catch (IOException e) {
//            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
//        }
//
//        try {
//            if (logger.isTraceEnabled()) {
//                ObjectMapper mapper = new ObjectMapper();
//                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//                logger.trace(mapper.writeValueAsString(verifiablePresentationDTO));
//            }
//        } catch (Exception e) {
//            logger.error(e);
//        }
//
//        return verifiablePresentationDTO;
//    }

    public boolean validateExpiry(Date expirationDate) {
        Date today = new Date();
        if (logger.isDebugEnabled()) {
            logger.trace(String.format("Today: %s, Expiration Date: %s - ExpirationDate > Today: %s", today, expirationDate, expirationDate.after(today)));
        }
        return expirationDate.after(today);
    }

    public byte[] convertAndStoreXMLCredential(CredentialDTO credentialDTO) {
        return convertAndStoreXMLCredential(credentialRepository.getOne(credentialDTO.getPk()));
    }

    public byte[] convertAndStoreXMLCredential(CredentialDAO credentialDAO) {
        byte[] jsonLdCredential = externalServicesUtil.convertCredential(credentialDAO.getCredential());

        String walletFolder = !StringUtils.isEmpty(credentialDAO.getWallet().getFolder()) ? credentialDAO.getWallet().getFolder() :
                walletService.createWalletFolderIfNotCreated(credentialDAO.getWallet());
        String fileStored = storeCredentialIntoFilesystem(walletFolder, jsonLdCredential);

        if (StringUtils.isEmpty(fileStored)) {
            credentialDAO.setSigned(false);
            credentialDAO.setFile(fileStored);
            credentialRepository.save(credentialDAO);
        }

        return jsonLdCredential;
    }

}
