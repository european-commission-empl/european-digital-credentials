package eu.europa.ec.empl.edci.repository.entity;

import eu.europa.ec.empl.edci.datamodel.Emptiable;

public interface IGenericDAO extends Emptiable {

    public Long getPk();

    public void setPk(Long pk);


}
