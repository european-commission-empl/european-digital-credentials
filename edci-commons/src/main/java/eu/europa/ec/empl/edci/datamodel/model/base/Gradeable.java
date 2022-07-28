package eu.europa.ec.empl.edci.datamodel.model.base;

public interface Gradeable {

    public String getPk();

    public void graduate(String score);
}
