package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.upload.DisplayDetailsDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ContentDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.LabelDTDAO;
import eu.europa.ec.empl.edci.issuer.util.DiplomaUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class DiplomaService {
    private static final Logger logger = LogManager.getLogger(DiplomaService.class);

    @Autowired
    private DiplomaUtils diplomaUtils;

    private String diplomaPath = "diploma/template/diploma_default_generic_thymeleaf.html";
    private String diplomaSupplementPath = "diploma/template/diploma_default_diplomaSupplement_thymeleaf.html";

    public void informDiplomaImage(EuropeanDigitalCredentialDTO credential, DisplayDetailsDTO display) {

        Set<LabelDTDAO> labels = null;
        if(display != null && display.getLabels() != null) {
            labels = new HashSet<>();
            for(Map.Entry<String, Map<String, String>> entry: display.getLabels().entrySet()) {
                LabelDTDAO labelDTDAO = new LabelDTDAO();
                labelDTDAO.setKey(entry.getKey());
                for(Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
                    labelDTDAO.getContents().add(new ContentDTDAO(innerEntry.getValue(), innerEntry.getKey()));
                }
                labels.add(labelDTDAO);
            }
        }

        boolean isDiplomaSupplement = credential.getCredentialProfiles() != null && credential.getCredentialProfiles().stream().anyMatch(conceptDTO -> conceptDTO.getId().toString().equals(ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl()));

        if (display == null) {
            String defaultGenericTemplate = null;

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
            try {
                diplomaUtils.informDiplomaFromTemplate(credential, display.getTemplate(), labels, display.getBackground(), isDiplomaSupplement);
            } catch (Exception e) {
                throw new EDCIException(ErrorCode.DIPLOMA_BAD_FORMAT, e.getCause().toString()).addDescription("Error generating diploma template");
            }
        }

    }

}
