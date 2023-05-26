package eu.europa.ec.empl.edci.model.view.fields;

import eu.europa.ec.empl.edci.model.view.base.AgentView;

import java.util.ArrayList;
import java.util.List;

public class AwardingOpportunityFieldView {

    private List<AgentView> awardingBody = new ArrayList<>();
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private LocationFieldView location;
    private PeriodOfTimeFieldView temporal;

    public List<AgentView> getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(List<AgentView> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public LocationFieldView getLocation() {
        return location;
    }

    public void setLocation(LocationFieldView location) {
        this.location = location;
    }

    public PeriodOfTimeFieldView getTemporal() {
        return temporal;
    }

    public void setTemporal(PeriodOfTimeFieldView temporal) {
        this.temporal = temporal;
    }
}
