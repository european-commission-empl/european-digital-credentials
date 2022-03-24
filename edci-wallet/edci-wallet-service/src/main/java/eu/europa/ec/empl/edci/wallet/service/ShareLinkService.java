package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.ShareLinkMapper;
import eu.europa.ec.empl.edci.wallet.repository.ShareLinkRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.EuropassCredentialVerifyUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(ShareLinkService.class);

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
    EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

    public ShareLinkRepository getRepository() {
        return shareLinkRepository;
    }

    /*BUSINESS LOGIC METHODS*/
    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO createShareLink(String userId, ShareLinkDTO shareLinkDTO) {
        return createShareLink(userId, shareLinkDTO.getCredentialDTO().getUuid(), shareLinkDTO.getExpirationDate());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO createShareLink(String userId, String credUUID, Date expirationDate) {

        CredentialDTO credentialDTO = credentialService.fetchCredentialByUUID(userId, credUUID);

        ShareLinkDTO shareLinkDTOAux = new ShareLinkDTO();

        shareLinkDTOAux.setCredentialDTO(credentialDTO);

        Date now = new Date();
        shareLinkDTOAux.setCreationDate(now);
        shareLinkDTOAux.setExpirationDate(expirationDate);
        shareLinkDTOAux.setExpired(isShareLinkExpired(expirationDate));

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
            return credentialService.downloadVerifiablePresentationPDF(shareLinkDTO.getCredentialDTO(), shareLinkDTO.getExpirationDate(), pdfType);
        } else {
            shareLinkDTO.setExpired(true);
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO));
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }
    }

    public List<VerificationCheckDTO> verifyCredential(String sharedURL) throws IOException {

        List<VerificationCheckDTO> credentialReport = new ArrayList<VerificationCheckDTO>();

        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);

        if (!isShareLinkExpired(shareLinkDTO)) {
            CredentialDTO credentialDTO = shareLinkDTO.getCredentialDTO();
            credentialReport = europassCredentialVerifyUtil.verifyCredential(credentialDTO.getCredentialXML());
        } else {
            shareLinkDTO.setExpired(true);
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO));
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }

        return credentialReport;
    }


    public ResponseEntity<byte[]> downloadShareLinkPresentationXML(String sharedURL) {
        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);
        if (!isShareLinkExpired(shareLinkDTO)) {
            return credentialService.downloadVerifiablePresentationXML(shareLinkDTO.getCredentialDTO(), shareLinkDTO.getExpirationDate());
        } else {
            shareLinkDTO.setExpired(true);
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO));
            throw new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SHARE_LINK_EXPIRED, "wallet.credential.share.expired.error");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadShareLinkCredential(String sharedURL) {
        ShareLinkDTO shareLinkDTO = this.fetchShareLinkBySharedURL(sharedURL);
        if (!isShareLinkExpired(shareLinkDTO)) {
            CredentialDTO credentialDTO = shareLinkDTO.getCredentialDTO();
            return new ResponseEntity<byte[]>(credentialDTO.getCredentialXML(), prepareHttpHeadersForCredentialDownload("myFileName.xml"), HttpStatus.OK);
        } else {
            shareLinkDTO.setExpired(true);
            this.shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkDTO));
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

        return shareLinkMapper.toDTO(shareLink);
    }


    /*DB ACCESS METHODS*/
    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO addShareLinkEntity(ShareLinkDTO shareLinkEntityDTO) {
        return shareLinkMapper.toDTO(shareLinkRepository.save(shareLinkMapper.toDAO(shareLinkEntityDTO)));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int countByShareURL(String shareURL) {
        return shareLinkRepository.countByShareURL(shareURL);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteShareLink(ShareLinkDTO shareLinkDTO) {
        this.shareLinkRepository.deleteById(shareLinkDTO.getId());
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public ShareLinkDTO fetchShareLinkBySharedURL(String sharedURL) {
        if (validateShareLinkExists(sharedURL)) {
            return this.shareLinkMapper.toDTO(shareLinkRepository.fetchBySharedURL(sharedURL));
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
        return shareLinkDTO.isExpired() || isShareLinkExpired(shareLinkDTO.getExpirationDate());
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
