package eu.europa.ec.empl.edci.repository.entity;

import java.util.Set;

public interface IMultilangDAO {

    public Set<String> getLanguages();

    public void setLanguages(Set<String> langs);

    public String getLabel();

    public void setLabel(String label);

    public String getDefaultLanguage();

}
