package eu.europa.ec.empl.edci.wallet.common.model;

import java.util.ArrayList;
import java.util.List;

public class WalletDTO {

    private Long id;
    private String userId;
    private String userEmail;
    private String walletAddress;
    private String folder;
    private Boolean temporary = false;
    private List<CredentialDTO> credentialDTOList = new ArrayList<>();

    private String errorCode;
    private String errorMsg;

    public WalletDTO() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<CredentialDTO> getCredentialDTOList() {
        return credentialDTOList;
    }

    public void setCredentialDTOList(List<CredentialDTO> credentialDTOList) {
        this.credentialDTOList = credentialDTOList;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
