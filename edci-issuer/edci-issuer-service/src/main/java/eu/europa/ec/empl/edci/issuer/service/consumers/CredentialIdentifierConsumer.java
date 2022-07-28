package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, priority = 4)
@Component
public class CredentialIdentifierConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        europassCredentialDTO.setId(URI.create(europassCredentialDTO.getPrefix(europassCredentialDTO).concat(UUID.randomUUID().toString())));
        List<Identifiable> identifiables = reflectiveUtil.getInnerObjectsOfType(Identifiable.class, europassCredentialDTO).stream().filter(identifiable -> !identifiable.ignoreProcessing()).collect(Collectors.toList());
        Map<String, List<Object>> orderedObjects = reflectiveUtil.getTypesHashMap(identifiables);
        Map<Integer, URI> identifiedHashes = new HashMap<Integer, URI>();
        for (Map.Entry<String, List<Object>> entry : orderedObjects.entrySet()) {
            List<Object> identifiableList = entry.getValue();
            int counter = 1;
            for (int i = 0; i < identifiableList.size(); i++) {
                Object object = identifiableList.get(i);
                Identifiable identifiable = (Identifiable) object;
                URI uri = null;
                if (identifiedHashes.containsKey(identifiable.hashCode())) {
                    uri = identifiedHashes.get(identifiable.hashCode());
                } else {
                    String uriString = identifiable.getPrefix(identifiable).concat(String.valueOf(counter));
                    counter++;
                    uri = URI.create(uriString);
                    identifiedHashes.put(identifiable.hashCode(), uri);

                }
                identifiable.setId(uri);
            }
        }
    }
}
