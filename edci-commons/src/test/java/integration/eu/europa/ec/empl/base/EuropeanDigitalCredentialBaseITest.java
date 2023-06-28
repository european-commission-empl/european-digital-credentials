package integration.eu.europa.ec.empl.base;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.OrganisationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;

import java.time.ZonedDateTime;

public abstract class EuropeanDigitalCredentialBaseITest extends AbstractIntegrationBaseTest {

    public EuropeanDigitalCredentialDTO getGenericCredentialDTO() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        try {
            europeanDigitalCredentialDTO = JsonLdFactoryUtil.getVerifiableCredentialDTO();

        } catch (Exception e) {
            System.out.println(e);
        }
        return europeanDigitalCredentialDTO;
    }

    public EuropeanDigitalCredentialDTO getSimpleCredentialDTO() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();

        europeanDigitalCredentialDTO.setIssuer(this.getOrganisationDTO());
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setDisplayParameter(JsonLdFactoryUtil.getDisplayParameter());
        europeanDigitalCredentialDTO.setCredentialSubject(this.getPersonDTO());

        return europeanDigitalCredentialDTO;
    }

    public PersonDTO getPersonDTO() {
        PersonDTO credentialSubject = new PersonDTO();
        credentialSubject.setFullName(new LiteralMap("en", "Person full Name"));
        return credentialSubject;
    }

    public OrganisationDTO getOrganisationDTO() {
        OrganisationDTO organisationDTO = new OrganisationDTO();
        organisationDTO.setPrefLabel(new LiteralMap("en", "Organization preferred Name"));
        return organisationDTO;
    }
}
