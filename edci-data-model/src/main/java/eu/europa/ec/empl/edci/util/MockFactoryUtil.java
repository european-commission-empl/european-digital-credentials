package eu.europa.ec.empl.edci.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

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

    public void throwIllegalAccessException() throws Exception {
        throw new IllegalAccessException();
    }

    public void throwInvocationTargetException() throws Exception {
        throw new InvocationTargetException(null);
    }

   /* public String createNexusTestCredentialXML() throws Exception {
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
    }*/

}
