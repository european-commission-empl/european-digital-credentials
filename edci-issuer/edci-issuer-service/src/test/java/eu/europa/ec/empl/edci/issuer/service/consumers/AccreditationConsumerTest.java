package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.DisplayParameterDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.service.QDRAccreditationExternalService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

public class AccreditationConsumerTest extends AbstractUnitBaseTest {

    @InjectMocks
    public AccreditationConsumer accreditationConsumer;

    @Mock
    public CredentialUtil credentialUtil;

    @Mock
    public QDRAccreditationExternalService accreditationExternalService;


    @Test
    public void accept_shouldCallServiceAndEvidenceSetter() {
        AccreditationDTO accreditationDTO = new AccreditationDTO();
        accreditationDTO.setId(URI.create("urn:accreditation:1"));

        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        DisplayParameterDTO displayParameterDTO = Mockito.mock(DisplayParameterDTO.class);
        europeanDigitalCredentialDTO.setDisplayParameter(displayParameterDTO);

        Evidence evidence = Mockito.mock(Evidence.class);
        europeanDigitalCredentialDTO.setEvidence(Arrays.asList(evidence));

        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId(URI.create(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));


        Mockito.when(displayParameterDTO.getPrimaryLanguage()).thenReturn(new ConceptDTO());
        Mockito.when(credentialUtil.isAccreditedCredential(any())).thenReturn(true);
        Mockito.when(accreditationExternalService.retrieveAccreditationByUri(any(), any(ConceptDTO.class))).thenReturn(accreditationDTO);
        Mockito.when(evidence.getId()).thenReturn(URI.create("urn:evidence:1"));
        Mockito.when(evidence.getDcType()).thenReturn(conceptDTO);
        Mockito.when(evidence.getAccreditation()).thenReturn(accreditationDTO);

        EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadDTO();
        europeanDigitalCredentialUploadDTO.setCredential(europeanDigitalCredentialDTO);

        this.accreditationConsumer.accept(new ConsumerContext(europeanDigitalCredentialUploadDTO));

        Mockito.verify(accreditationExternalService, Mockito.times(1)).retrieveAccreditationByUri(any(), any(ConceptDTO.class));
        Mockito.verify(evidence, Mockito.times(1)).setAccreditation(any());
    }
    

}
