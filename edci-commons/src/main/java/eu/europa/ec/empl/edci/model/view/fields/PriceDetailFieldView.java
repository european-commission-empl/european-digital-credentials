package eu.europa.ec.empl.edci.model.view.fields;

import java.util.ArrayList;
import java.util.List;

public class PriceDetailFieldView {
    private String description;
    private AmountFieldView amount;
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private List<NoteFieldView> additionalNote = new ArrayList<>();
    private String prefLabel;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AmountFieldView getAmount() {
        return amount;
    }

    public void setAmount(AmountFieldView amount) {
        this.amount = amount;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }
}
