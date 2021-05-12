package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WalletService {

    private static final Logger logger = Logger.getLogger(WalletService.class);

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    EDCIMessageService edciMessageService;

    /*BUSINESS LOGIC METHODS*/

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO createWallet(WalletDTO walletDTO) {

        WalletDTO wallet = this.addWalletEntity(walletDTO);

        return wallet;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<WalletDTO> createBulkWallet(List<WalletDTO> walletDTOList) {

        List<WalletDTO> wallets = new ArrayList<>();

        for (WalletDTO walletDTO : walletDTOList) {

            wallets.add(this.addBulkWalletEntity(walletDTO));

        }

        wallets.removeIf(w -> w.getErrorCode() == null);

        return wallets;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteWallet(String userId) {
        if (isValidUserId(userId)) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.not.extists.error", userId);
        }
        this.deleteWalletEntity(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOldTempWallets() {

        Integer numDays = walletConfigService.getInteger(EDCIWalletConstants.CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_DAYS, 0);

        if (numDays > 0) {
            if (walletConfigService.getBoolean(EDCIWalletConstants.CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_WITH_NO_CRED, true)) {
                List<WalletDAO> wallets = walletRepository.fetchOldTemporaryWallets(numDays);
                walletRepository.deleteAll(wallets);
            } else {
                List<WalletDAO> wallets = walletRepository.fetchOldTemporaryWalletsWithNoCredentials(numDays);
                walletRepository.deleteAll(wallets);
            }
        }

    }

    /*DB Access methods*/

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO fetchWalletByUserId(String userId) {
        validateWalletExists(userId);
        return walletMapper.toDTO(walletRepository.fetchByUserId(userId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO fetchWalletByUserEmail(String userEmail) {
        validateWalletExistsByEmail(userEmail);
        return walletMapper.toDTO(walletRepository.fetchByUserEmail(userEmail));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO fetchWalletByUserEmail(String userEmail, boolean validateExists) {
        if (validateExists) {
            validateWalletExistsByEmail(userEmail);
        }
        return walletMapper.toDTO(walletRepository.fetchByUserEmail(userEmail));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteWalletEntity(String userId) {
        validateWalletExists(userId);
        walletRepository.deleteById(this.fetchWalletByUserId(userId).getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO addWalletEntity(WalletDTO walletDTO) {

        String walletAddressPrefix = walletConfigService.getString("wallet.address.prefix");

        WalletDAO walletUserId = walletRepository.fetchByUserId(walletDTO.getUserId());
        WalletDAO walletEmail = walletRepository.fetchByUserEmail(walletDTO.getUserEmail());

        //If wallet is temporary, and we are trying to create a definitive one, we overwrite the existing one with the new Ids
        if (walletUserId == null && walletEmail != null && walletEmail.getTemporary()) {
            walletEmail.setTemporary(false);
            walletEmail.setUserId(walletDTO.getUserId());
            walletEmail.setWalletAddress(walletAddressPrefix + "/" + walletDTO.getUserId());
            return walletMapper.toDTO(walletRepository.save(walletEmail));
        }

        if (walletEmail == null && walletUserId == null) {
            walletDTO.setWalletAddress(walletAddressPrefix + "/" + walletDTO.getUserId());
            return walletMapper.toDTO(walletRepository.save(walletMapper.toDAO(walletDTO)));
        } else {

            if (walletEmail == null) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_ID, "wallet.already.extists.id.error", walletDTO.getUserId());
            } else if (walletUserId == null) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_EMAIL, "wallet.already.extists.mail.error", walletDTO.getUserEmail());
            }

            if (walletEmail.getPk() != walletUserId.getPk()) {
                throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.WALLET_INVALID_ID, "wallet.already.extists.id.error", walletDTO.getUserId());
            }

            return walletMapper.toDTO(walletUserId);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDTO addBulkWalletEntity(WalletDTO walletDTO) {

        try {
            String walletAddressPrefix = walletConfigService.getString("wallet.address.prefix");

            int walletEmail = walletRepository.countByUserEmail(walletDTO.getUserEmail());

            if (walletEmail >= 1) {
                walletDTO.setErrorCode(ErrorCode.WALLET_EXISTS.toString());
                walletDTO.setErrorMsg(edciMessageService.getMessage("wallet.already.extists.mail.error", walletDTO.getUserEmail()));
                return walletDTO;
            }

            walletDTO.setTemporary(true);
            walletDTO.setWalletAddress(walletAddressPrefix + "/" + "Temp_" + RandomStringUtils.random(10, true, true));
            walletDTO.setUserId(RandomStringUtils.random(10, true, true));
            walletMapper.toDTO(walletRepository.save(walletMapper.toDAO(walletDTO)));

        } catch (Throwable e) {
            walletDTO.setErrorCode(ErrorCode.UNDEFINED.toString());
            walletDTO.setErrorMsg(edciMessageService.getMessage("global.internal.error"));
        }

        return walletDTO;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isValidUserId(String userId) {
        return walletRepository.countByUserId(userId) <= 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateWalletExists(String userId) {
        if (!(walletRepository.countByUserId(userId) > 0)) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.not.extists.error", userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateWalletExistsByEmail(String userEmail) {
        WalletDAO wallet = walletRepository.fetchByUserEmail(userEmail);
        if (wallet == null) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.WALLET_NOT_FOUND, "wallet.wallet.not.extists.email.error", userEmail);
        }
    }
}
