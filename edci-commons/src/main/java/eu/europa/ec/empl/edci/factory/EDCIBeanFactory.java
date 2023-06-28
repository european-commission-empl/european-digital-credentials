package eu.europa.ec.empl.edci.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIBeanFactory {

    @Autowired
    public ApplicationContext applicationContext;

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public <T> Set<T> getBeans(ClassPathScanningCandidateComponentProvider provider, Class<T> clazz, String basePackage) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (basePackage == null) basePackage = "eu.europa.ec.empl.edci";
        Set<T> beans = new HashSet<T>();
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            Class componentClass = Class.forName(component.getBeanClassName());
            if (clazz.isAssignableFrom(componentClass)) {
                T bean = (T) this.getBean(componentClass);
                //get new instance directly if not in spring context
                if (bean == null) bean = (T) componentClass.newInstance();
                beans.add(bean);
            }
        }
        return beans;
    }

    public Set<Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return new HashSet<Object>(applicationContext.getBeansWithAnnotation(annotation).values());
    }

}
