package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletConfig;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CycleAvoidingMappingContext;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private static final Logger logger = LogManager.getLogger(WalletService.class);

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    EDCIMessageService edciMessageService;

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    protected static SecureRandom random = new SecureRandom();

    /*BUSINESS LOGIC METHODS*/

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO updateWalletByWalletAddress(String walletAddress, String email) {

        WalletDAO wallet = this.fetchWalletDAOByWalletAddress(walletAddress);

        wallet.setUserEmail(email);

        this.walletRepository.save(wallet);

        return walletMapper.toDTO(wallet, new CycleAvoidingMappingContext());

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<WalletDTO> createBulkWallet(List<String> walletEmailList) {

        List<WalletDTO> wallets = new ArrayList<>();

        for (String emailWallet : walletEmailList) {
            try {
                WalletDAO walletDAO = this.addBulkWalletEntity(emailWallet);
                wallets.add(walletMapper.toDTO(walletDAO, new CycleAvoidingMappingContext()));
            } catch (EDCIException e) {
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setErrorCode(e.getCode().toString());
                walletDTO.setErrorMsg(edciMessageService.getMessage(e.getMessageKey(), e.getMessageArgs()));
                wallets.add(walletDTO);
            }
        }

        wallets.removeIf(w -> w.getErrorCode() == null);

        return wallets;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteWalletByWalletAddress(String walletAddress) {
        if (!isValidWalletAddress(walletAddress)) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.address.not.extists.error", walletAddress);
        }
        WalletDAO walletDAO = this.fetchWalletDAOByWalletAddress(walletAddress);
        this.deleteWalletEntity(walletDAO);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOldTempWallets() {

        Integer numDays = walletConfigService.getInteger(EDCIWalletConstants.CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_DAYS, 0);

        if (numDays > 0) {
            List<WalletDAO> wallets = walletRepository.fetchOldTemporaryWallets(numDays);
            if (walletConfigService.getBoolean(EDCIWalletConstants.CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_WITH_NO_CRED, true)) {
                wallets = wallets.stream().filter(wallet -> wallet.getCredentialDAOList().isEmpty()).collect(Collectors.toList());
            }
            this.credentialStorageUtil.deleteWalletStorage(wallets);
            walletRepository.deleteAll(wallets);
        }

    }

    /*DB Access methods*/

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO fetchWalletByUserId(String userId) {
        validateWalletUserIdExists(userId);
        WalletDAO walletDAO = walletRepository.fetchByUserId(userId);
        return walletMapper.toDTO(walletDAO, new CycleAvoidingMappingContext());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO fetchWalletByWalletAddress(String walletAddress) {
        validateWalletWalletAddressExists(walletAddress);
        WalletDAO walletDAO = walletRepository.fetchByWalletAddress(walletAddress);
        return walletMapper.toDTO(walletDAO, new CycleAvoidingMappingContext());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDAO fetchWalletDAOByWalletAddress(String walletAddress) {
        validateWalletWalletAddressExists(walletAddress);
        WalletDAO walletDAO = walletRepository.fetchByWalletAddress(walletAddress);
        return walletDAO;
    }

    public File getOrCreateFolder(String folderName) {

        File folder = edciFileService.getOrCreateFile(folderName);

        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }

        return folder;
    }

    public String getWalletPrivateFolderName(String walletFolder) {
        return getWalletDataLocation()
                .concat(walletConfigService.getString(WalletConfig.Wallet.WALLETS_ROOT_FOLDER))
                .concat(walletFolder).concat(EDCIConstants.StringPool.STRING_SLASH);
    }

    protected String getWalletDataLocation() {
        return walletConfigService.getString(WalletConfig.Wallet.WALLETS_DATA_LOCATION);
    }

    protected void createWalletFolderInFileSystem(String walletFolder) {
        File folder = getOrCreateFolder(getWalletPrivateFolderName(walletFolder));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDAO fetchWalletDAOByUserEmail(String userEmail, boolean validateExists) {
        if (validateExists) {
            validateWalletExistsByEmail(userEmail);
        }
        List<WalletDAO> walletList = walletRepository.listByUserEmail(userEmail);

        return walletList != null && !walletList.isEmpty() ? walletList.get(0) : null;
    }

    public String createWalletFolderIfNotCreated(WalletDAO walletDAO) {
        String folder = null;
        if (StringUtils.isEmpty(walletDAO.getFolder())) {
            int num = random.nextInt(0x1000000);
            folder = String.format("%06x", num);
            walletDAO.setFolder(folder);
            createWalletFolderInFileSystem(folder);
            walletRepository.save(walletDAO);
        } else {
            folder = walletDAO.getFolder();
        }
        return folder;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteWalletEntity(WalletDAO walletDAO) {
        this.credentialStorageUtil.deleteWalletStorage(walletDAO.getFolder(), walletDAO.getCredentialDAOList());
        walletRepository.deleteById(walletDAO.getPk());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO createWallet(WalletDTO walletDTO) {

        String walletAddressPrefix = walletConfigService.getString("wallet.address.prefix");

        List<WalletDAO> walletList = walletRepository.listByUserEmail(walletDTO.getUserEmail());
        WalletDAO walletEmail = walletList != null && !walletList.isEmpty() ? walletList.get(0) : null;
        WalletDAO walletUserId = walletRepository.fetchByUserId(walletDTO.getUserId());

        //If wallet is temporary, and we are trying to create a definitive one, we overwrite the existing one with the new Ids
        if (walletUserId == null && walletEmail != null && walletEmail.getTemporary()) {
            walletEmail.setTemporary(false);
            walletEmail.setUserId(walletDTO.getUserId());
            walletEmail.setWalletAddress(walletAddressPrefix + "/" + walletDTO.getUserId());
            return walletMapper.toDTO(walletRepository.save(walletEmail), new CycleAvoidingMappingContext());
        }

        if (walletEmail == null && walletUserId == null) {
            walletDTO.setWalletAddress(walletAddressPrefix + "/" + walletDTO.getUserId());
            WalletDAO wallet = walletMapper.toDAO(walletDTO, new CycleAvoidingMappingContext());
            return walletMapper.toDTO(walletRepository.save(wallet), new CycleAvoidingMappingContext());
        } else {

            if (walletEmail == null) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_ID, "wallet.already.extists.id.error", walletDTO.getUserId());
            } else if (walletUserId == null) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_EMAIL, "wallet.already.extists.mail.error", walletDTO.getUserEmail());
            }

            if (walletEmail.getPk() != walletUserId.getPk()) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_ID, "wallet.already.extists.id.error", walletDTO.getUserId());
            }

            return walletMapper.toDTO(walletUserId, new CycleAvoidingMappingContext());
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDAO createOrRetrieveWalletByEmail(String userEmail) {
        WalletDAO wallet = null;

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isValid(userEmail, null)) {
            throw new EDCIBadRequestException().addDescription("Invalid email");
        }
        //Check if auto creation of temporal wallets is activated
        if (walletConfigService.getBoolean("wallet.create.sending.credential", true)) {
            WalletDAO existingWallet = fetchWalletDAOByUserEmail(userEmail, false);
            //If no wallet detected and auto-creation enabled, use addBulkWalletEntity
            if (existingWallet == null) {
                existingWallet = new WalletDAO();
                existingWallet.setUserEmail(userEmail);
                wallet = addBulkWalletEntity(userEmail);
            } else {
                wallet = existingWallet;
            }
        } else {
            //If auto creation not enabled, just try to fetch by email, an exception if thrown if it does not exist
            wallet = fetchWalletDAOByUserEmail(userEmail, false);
        }
        return wallet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDAO addBulkWalletEntity(String userEmail) {
        WalletDAO wallet = new WalletDAO();
        try {
            String walletAddressPrefix = walletConfigService.getString("wallet.address.prefix");

            int walletEmail = walletRepository.countByUserEmail(userEmail);

            if (walletEmail >= 1) {
                throw new EDCIException(ErrorCode.WALLET_EXISTS, userEmail);
            }

            wallet.setTemporary(true);
            wallet.setWalletAddress(walletAddressPrefix + "/" + "Temp_" + RandomStringUtils.random(10, true, true));
            wallet.setUserId(RandomStringUtils.random(10, true, true));
            wallet.setUserEmail(userEmail);
            wallet = walletRepository.save(wallet);
        } catch (EDCIException e) {
            throw e;
        } catch (Throwable e) {
            throw new EDCIException(e);
        }

        return wallet;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isValidWalletAddress(String walletAddress) {
        return walletRepository.countByWalletAddress(walletAddress) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateWalletUserIdExists(String userId) {
        if (!(walletRepository.countByUserId(userId) > 0)) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.not.extists.error", userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateWalletWalletAddressExists(String userId) {
        if (!(walletRepository.countByWalletAddress(userId) > 0)) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.address.not.extists.error", userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateWalletExistsByEmail(String userEmail) {
        List<WalletDAO> walletList = walletRepository.listByUserEmail(userEmail);

        if (walletList == null || walletList.isEmpty()) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.not.extists.email.error", userEmail);
        }
    }
}
