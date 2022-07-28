package eu.europa.ec.empl.edci.factory;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConsumerFactory {

    @Autowired
    public EDCIBeanFactory beanFactory;

    public Set<Consumer> getEDCIConsumers(Class forClass) {
        return beanFactory.getBeansAnnotatedWith(EDCIConsumer.class).stream()
                .filter(object -> object.getClass().getAnnotation(EDCIConsumer.class).applyTo().equals(forClass))
                .map(object -> (Consumer) object)
                .sorted(Comparator.comparingInt(c -> c.getClass().getDeclaredAnnotation(EDCIConsumer.class).priority()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Consumer> getEDCIConsumers(Class forClass, boolean preProcess) {
        return getEDCIConsumers(forClass).stream().filter(object -> object.getClass().getAnnotation(EDCIConsumer.class).preProcess() == preProcess)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
