package eu.europa.ec.empl.edci.issuer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AssociationService {

    private static final Logger logger = LogManager.getLogger(AssociationService.class);

   /* boolean validateAssociations(List<Association> associations) throws EDCIException {

        List<LinkedHashSet<String>> sets = new ArrayList<LinkedHashSet<String>>();

        for (Association association : associations) {

            boolean setExists = false;

            for (int i = 0; i < sets.size(); i++) {
                if (!sets.get(i).isEmpty()) {
                    if (sets.get(i).contains(association.getSrcClass() + association.getSrcId())) {
                        setExists = true;
                        if (!sets.get(i).add(association.getDestClass() + association.getDestId())) {
                            throw new EDCIException(String.format("Failed to associate [%s/%d] with [%s/%d]", association.getSrcClass(), association.getSrcId(), association.getDestClass(), association.getDestId()));

                            // return true;
                        }
                    }
                }
            }

            if (!setExists) {
                sets.add(new LinkedHashSet<String>());
                sets.get(sets.size() - 1).add(association.getSrcClass() + association.getSrcId());
                sets.get(sets.size() - 1).add(association.getDestClass() + association.getDestId());
            }
        }

        return false;
    }*/

}
