package eu.europa.ec.empl.edci.repository.entity;

public interface IOCBIdentifiedDAO {

    abstract String getOCBID();

    abstract void setOCBID(String uuid);
}
