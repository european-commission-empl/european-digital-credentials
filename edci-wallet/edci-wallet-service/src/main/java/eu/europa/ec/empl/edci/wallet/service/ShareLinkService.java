package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import eu.europa.ec.empl.edci.util.ExternalServicesUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.CycleAvoidingMappingContext;
import eu.europa.ec.empl.edci.wallet.mapper.ShareLinkMapper;
import eu.europa.ec.empl.edci.wallet.repository.ShareLinkRepository;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShareLinkService implements WalletCrudService<ShareLinkDAO> {

    private static final Logger logger = LogManager.getLogger(ShareLinkService.class);

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ShareLinkMapper shareLinkMapper;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ExternalServicesUtil externalServicesUtil;

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    public ShareLinkRepository getRepository() {
        return shareLinkRepository;
    }

    /*BUSINESS LOGIC METHODS*/
    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO createShareLinkWalletAddress(String walletAddress, ShareLinkDTO shareLinkDTO) {
        return createShareLink(walletAddress, shareLinkDTO.getCredential().getUuid(), shareLinkDTO.getExpirationDate());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO createShareLink(String walletAddress, String credUUID, Date expirationDate) {

        CredentialDTO credentialDTO = credentialService.fetchCredentialByUUID(walletAddress, credUUID);

        ShareLinkDTO shareLinkDTOAux = new ShareLinkDTO();

        shareLinkDTOAux.setCredential(credentialDTO);

        Date now = new Date();
        shareLinkDTOAux.setCreationDate(now);
        shareLinkDTOAux.setExpirationDate(expirationDate);

        String sharedURL = RandomStringUtils.random(16, true, true);

        while (shareLinkRepository.countByShareURL(sharedURL) > 0) {
            sharedURL = RandomStringUtils.random(16, true, true);
        }

        shareLinkDTOAux.setShareHash(sharedURL);

        return addShareLinkEntity(shareLinkDTOAux);
    }

    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(String sharedURL, String pdfType) {
        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);
        if (!isShareLinkExpired(shareLinkDTO)) {
            return credentialService.downloadVerifiablePresentationPDF(shareLinkDTO, pdfType);
        } else {
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO, new CycleAvoidingMappingContext()));
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }
    }

    public List<VerificationCheckReport> verifyCredential(String sharedURL) throws IOException {

        List<VerificationCheckReport> credentialReport = new ArrayList<VerificationCheckReport>();

        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);

        if (!isShareLinkExpired(shareLinkDTO)) {
            CredentialDTO credentialDTO = shareLinkDTO.getCredential();
            credentialReport = externalServicesUtil.verifyCredential(credentialStorageUtil.getCredentialFromFileSystem(credentialDTO));
        } else {
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }

        return credentialReport;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadShareLinkCredential(String sharedURL) {
        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);
        if (!isShareLinkExpired(shareLinkDTO)) {
            CredentialDTO credentialDTO = shareLinkDTO.getCredential();
            return new ResponseEntity<byte[]>(credentialStorageUtil.getCredentialFromFileSystem(credentialDTO), prepareHttpHeadersForCredentialDownload("myFileName.xml"), HttpStatus.OK);
        } else {
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO, new CycleAvoidingMappingContext()));
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO updateShareLink(String sharedURL, ShareLinkDTO shareLinkDTO) {

        ShareLinkDAO shareLink = null;
        if (validateShareLinkExists(sharedURL)) {
            shareLink = shareLinkRepository.fetchBySharedURL(sharedURL);
            if (shareLinkDTO.getExpirationDate() == null) {
                throw new EDCIBadRequestException();
            }
            shareLink.setExpirationDate(shareLinkDTO.getExpirationDate());
            shareLinkRepository.save(shareLink);
        } else {
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_INVALID, "wallet.credential.share.invalid.error", sharedURL);
        }

        return shareLinkMapper.toDTO(shareLink, new CycleAvoidingMappingContext());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOldShareLinks() {

        Boolean enabled = walletConfigService.getBoolean(EDCIWalletConstants.CONFIG_CLEAN_OLD_SHARELINKS, true);

        if (enabled) {
            List<ShareLinkDAO> sharelinks = shareLinkRepository.fetchOldTemporarySharelinks();
            shareLinkRepository.deleteAll(sharelinks);
        }

    }

    /*DB ACCESS METHODS*/
    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO addShareLinkEntity(ShareLinkDTO shareLinkEntityDTO) {
        return shareLinkMapper.toDTO(shareLinkRepository.save(
                shareLinkMapper.toDAO(shareLinkEntityDTO, new CycleAvoidingMappingContext()))
                , new CycleAvoidingMappingContext());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int countByShareURL(String shareURL) {
        return shareLinkRepository.countByShareURL(shareURL);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteShareLink(ShareLinkDTO shareLinkDTO) {
        this.shareLinkRepository.deleteById(shareLinkDTO.getId());
    }

    public String getShareLinkURL(String hash) {
        return walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_SHARED_URL).replaceAll(Parameter.SHARED_HASH, hash);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO fetchShareLinkBySharedURL(String sharedURL) {
        if (validateShareLinkExists(sharedURL)) {
            return this.shareLinkMapper.toDTO(shareLinkRepository.fetchBySharedURL(sharedURL), new CycleAvoidingMappingContext());
        } else {
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_INVALID, "wallet.credential.share.invalid.error", sharedURL);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean validateShareLinkExists(String sharedURL) {
        return this.countByShareURL(sharedURL) > 0;
    }


    /*
     * UTIL METHODS
     */

    protected boolean isShareLinkExpired(ShareLinkDTO shareLinkDTO) {
        return isShareLinkExpired(shareLinkDTO.getExpirationDate());
    }

    protected boolean isShareLinkExpired(Date expirationDate) {
        Date now = new Date();
        return now.after(expirationDate);
    }

    protected HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }


}
