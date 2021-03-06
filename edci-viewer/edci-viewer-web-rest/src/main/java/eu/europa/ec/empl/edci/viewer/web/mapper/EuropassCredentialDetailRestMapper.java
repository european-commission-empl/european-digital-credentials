package eu.europa.ec.empl.edci.viewer.web.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassCredentialDetailView;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassDiplomaView;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")

public interface EuropassCredentialDetailRestMapper {

    SimpleDateFormat formatterdateOnly = new SimpleDateFormat(EDCIConstants.DATE_LOCAL);
    SimpleDateFormat formatterdateFull = new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT);

    default EuropassCredentialDetailView toVO(EuropassCredentialDTO europassCredentialDTO, @Context String locale) {
        EuropassCredentialDetailView credentialDetailView = new EuropassCredentialDetailView();

        credentialDetailView.setTitle(europassCredentialDTO.getTitle().getStringContent(locale));
        credentialDetailView.setIssuanceDate(europassCredentialDTO.getIssuanceDate() != null ? formatterdateOnly.format(europassCredentialDTO.getIssuanceDate()) : null);
        credentialDetailView.setExpirationDate(europassCredentialDTO.getExpirationDate() != null ? formatterdateOnly.format(europassCredentialDTO.getExpirationDate()) : null);
        credentialDetailView.setType(europassCredentialDTO.getType());
        //credentialDetailView.setAgentReferences(credentialDetailDTO.getOrgReferences());
        credentialDetailView.setCredentialSubject(europassCredentialDTO.getCredentialSubject());
        credentialDetailView.setDescription(europassCredentialDTO.getDescription().getStringContent("en"));
        credentialDetailView.setDisplayParams(null); // does not need this info
        credentialDetailView.setIssuanceLocation("UAB - Mock");
        credentialDetailView.setIssuer(europassCredentialDTO.getIssuer());
        /*ToDo*/
        //credentialDetailView.setProof(credentialDetailDTO.getProof());
        credentialDetailView.setXml(europassCredentialDTO.getOriginalXML());

        //subCredentials -> ToDo
        /*try {
            credentialDetailView.setContains(this.toVOList(EuropassCredentialUtil.parseSubCredentials(europassCredentialDTO.getContains()), locale));
        } catch (JAXBException e) {
           logger.error(e);
        }*/

        return credentialDetailView;
    }

    List<EuropassCredentialDetailView> toVOList(List<EuropassCredentialDTO> credentialDetailDTOList, @Context String locale);

    public EuropassDiplomaView toVO(EuropassDiplomaDTO europassDiplomaDTO);

    public EuropassDiplomaDTO toDTO(EuropassDiplomaView europassDiplomaView);

    default String dateToString(Date date) {

        if (date == null) {
            return null;
        }

        return formatterdateFull.format(date);

    }

    default Date stringToDate(String dateStr) {

        if (dateStr == null || dateStr.length() <= 0) {
            return null;
        }

        Date returnValue = null;
        try {
            returnValue = formatterdateFull.parse(dateStr);
        } catch (Exception e) {
            throw new EDCIBadRequestException().addDescription("Date format not valid: " + dateStr);
        }

        return returnValue;

    }

    default String toStringBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    default byte[] toBytesString(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        return Base64.getDecoder().decode(str);
    }
}
