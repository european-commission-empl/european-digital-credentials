package eu.europa.ec.empl.edci.repository.entity;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;

public interface IOCBIdentifiedDAO extends Identifiable {

    abstract String getOCBID();

    abstract void setOCBID(String uuid);
}
