package eu.europa.ec.empl.edci.wallet.common.model;


import java.util.List;

public class CredentialDTO {
    private Long pk;
    private String uuid;
    private WalletDTO walletDTO;
    private byte[] credentialXML;
    private List<byte[]> diplomaImage;
    private List<ShareLinkDTO> shareLinkDTOList;
    private List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS;
    private String type;

    public CredentialDTO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public byte[] getCredentialXML() {
        return credentialXML;
    }

    public void setCredentialXML(byte[] credentialXML) {
        this.credentialXML = credentialXML;
    }

    public WalletDTO getWalletDTO() {
        return walletDTO;
    }

    public void setWalletDTO(WalletDTO walletDTO) {
        this.walletDTO = walletDTO;
    }

    public List<byte[]> getDiplomaImage() {
        return diplomaImage;
    }

    public void setDiplomaImage(List<byte[]> diplomaImage) {
        this.diplomaImage = diplomaImage;
    }

    public List<CredentialLocalizableInfoDTO> getCredentialLocalizableInfoDTOS() {
        return credentialLocalizableInfoDTOS;
    }

    public void setCredentialLocalizableInfoDTOS(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS) {
        this.credentialLocalizableInfoDTOS = credentialLocalizableInfoDTOS;
    }

    public List<ShareLinkDTO> getShareLinkDTOList() {
        return shareLinkDTOList;
    }

    public void setShareLinkDTOList(List<ShareLinkDTO> shareLinkDTOList) {
        this.shareLinkDTOList = shareLinkDTOList;
    }

}
