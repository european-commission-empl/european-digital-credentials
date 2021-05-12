package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MockFactoryUtil {

    private Log logger = LogFactory.getLog(MockFactoryUtil.class);
    @Autowired
    private ReflectiveUtil reflectiveUtil;

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public MultipleEuropassCredentialDTO createNexusTestMockMultipleCredentialsDTO() throws MalformedURLException {
        MultipleEuropassCredentialDTO multipleEuropassCredentialDTO = new MultipleEuropassCredentialDTO();
        multipleEuropassCredentialDTO.setEuropassCredential(Arrays.asList(this.createNexusTestMockCredential()));
        return multipleEuropassCredentialDTO;
    }

    public EuropassCredentialDTO createNexusTestMockCredential() throws MalformedURLException {
        EuropassCredentialDTO mockTestCredential = new EuropassCredentialDTO();
        PersonDTO mockTestPerson = new PersonDTO();
        OrganizationDTO mockTestOrganization = new OrganizationDTO();

        mockTestCredential.setId(URI.create("urn:epass:cred:1"));
        mockTestPerson.setId(URI.create("urn:epass:person:1"));
        mockTestOrganization.setId(URI.create("urn:epass:org:1"));

        mockTestPerson.setContactPoint(Arrays.asList(createMockObject(ContactPoint.class)));
        mockTestPerson.setGivenNames(createMockObject(Text.class));
        mockTestPerson.setFamilyName(createMockObject(Text.class));
        mockTestPerson.setDateOfBirth(createMockObject(Date.class));

        mockTestOrganization.setLegalIdentifier(createMockObject(Identifier.class));
        mockTestOrganization.setPreferredName(createMockObject(Text.class));
        mockTestOrganization.setHasLocation(Arrays.asList(createMockObject(LocationDTO.class)));

        mockTestCredential.setType(createMockObject(Code.class));
        mockTestCredential.setIssuanceDate(createMockObject(Date.class));
        mockTestCredential.setIssuer(mockTestOrganization);
        mockTestCredential.setCredentialSubject(mockTestPerson);
        mockTestCredential.setTitle(createMockObject(Text.class));
        mockTestCredential.setDescription(createMockObject(Note.class));


        return mockTestCredential;
    }

    public <T> T createMockObject(Class<T> clazz) throws MalformedURLException {
        PodamFactory factory = new PodamFactoryImpl();
        RandomDataProviderStrategyImpl dataProviderStrategy = new RandomDataProviderStrategyImpl();
        dataProviderStrategy.setMaxDepth(1);
        dataProviderStrategy.setDefaultNumberOfCollectionElements(2);
        factory.setStrategy(dataProviderStrategy);
        T pojo = factory.manufacturePojo(clazz);

        URL testURL = new URL("http://dev.everisdx.io:8080/europass2/edci-issuer");
        try {
            this.getReflectiveUtil().doWithInnerObjectsOfType(InteractiveWebResourceDTO.class, pojo, (interactiveWebResourceDTO) -> interactiveWebResourceDTO.setId(testURL), null);
        } catch (StackOverflowError e) {
            logger.error(e);
        }
        return pojo;
    }

}
