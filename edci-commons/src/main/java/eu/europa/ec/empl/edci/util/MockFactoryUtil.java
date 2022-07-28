package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.model.InteractiveWebResourceDTO;
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
import java.net.URL;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MockFactoryUtil {

    private Log logger = LogFactory.getLog(MockFactoryUtil.class);

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public String createNexusTestCredentialXML() throws Exception {
        return edciCredentialModelUtil.getXmlFromInputString(getClass().getClassLoader().getResourceAsStream("issuer_test_seal_credential.xml"));
    }

    public <T> T createMockObject(Class<T> clazz) throws MalformedURLException {
        PodamFactory factory = new PodamFactoryImpl();
        RandomDataProviderStrategyImpl dataProviderStrategy = new RandomDataProviderStrategyImpl();
        dataProviderStrategy.setMaxDepth(1);
        dataProviderStrategy.setDefaultNumberOfCollectionElements(2);
        factory.setStrategy(dataProviderStrategy);
        T pojo = factory.manufacturePojo(clazz);

        URL testURL = new URL("https://europa.eu/europass/digital-credentials/issuer/#/home");
        try {
            this.getReflectiveUtil().doWithInnerObjectsOfType(InteractiveWebResourceDTO.class, pojo, (interactiveWebResourceDTO) -> interactiveWebResourceDTO.setId(testURL), null);
        } catch (StackOverflowError e) {
            logger.error(e);
        }
        return pojo;
    }

}
