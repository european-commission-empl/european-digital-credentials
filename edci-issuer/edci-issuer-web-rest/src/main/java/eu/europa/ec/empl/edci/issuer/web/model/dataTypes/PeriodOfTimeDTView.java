package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import org.joda.time.LocalDate;

public class PeriodOfTimeDTView extends DataTypeView {

    private TextDTView prefLabel; //0..*

    private LocalDate startDate; //0..1

    private LocalDate endDate; //0..1

    public TextDTView getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(TextDTView prefLabel) {
        this.prefLabel = prefLabel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
