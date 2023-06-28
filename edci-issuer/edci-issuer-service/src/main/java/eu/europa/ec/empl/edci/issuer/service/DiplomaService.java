package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.util.DiplomaUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DiplomaService {
    private static final Logger logger = LogManager.getLogger(DiplomaService.class);

    @Autowired
    private DiplomaUtils diplomaUtils;

    private String diplomaPath = "diploma/template/diploma_default_generic_thymeleaf.html";
    private String diplomaSupplementPath = "diploma/template/diploma_default_diplomaSupplement_thymeleaf.html";

    public void informDiplomaImage(EuropassCredentialSpecDAO credentialDao, EuropeanDigitalCredentialDTO credential) {

        DiplomaSpecDAO displayDao = credentialDao.getDisplay();

        if (displayDao == null) {
            String defaultGenericTemplate = null;
            boolean isDiplomaSupplement = credentialDao.getCredentialLabel() != null && credentialDao.getCredentialLabel().getUri().equals(ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl());
            try {
                if (isDiplomaSupplement) {
                    defaultGenericTemplate = IOUtils.toString(new ClassPathResource(diplomaSupplementPath).getInputStream());
                } else {
                    defaultGenericTemplate = IOUtils.toString(new ClassPathResource(diplomaPath).getInputStream());
                }
            } catch (IOException e) {
                throw new EDCIException(e).addDescription("Error retrieving diploma's default template");
            }
            diplomaUtils.informDiplomaFromTemplate(credential, defaultGenericTemplate, null, null, isDiplomaSupplement);
        } else {
            boolean isDiplomaSupplement = credentialDao.getCredentialLabel() != null && credentialDao.getCredentialLabel().getUri().equals(ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl());
            try {
                diplomaUtils.informDiplomaFromTemplate(credential, displayDao.getHtml(), displayDao.getLabels(), displayDao.getBackground(), isDiplomaSupplement);
            } catch (Exception e) {
                throw new EDCIException(ErrorCode.DIPLOMA_BAD_FORMAT, e.getCause().toString()).addDescription("Error generating diploma template");
            }
        }

    }

}
