package eu.europa.ec.empl.edci.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SpringApplicationContext implements ApplicationContextAware, BeanFactoryAware {

    private static BeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    @Override
    public void setBeanFactory(BeanFactory bf) throws BeansException {
        beanFactory = bf;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
