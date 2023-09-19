package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.AmountDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:priceDetail:")
public class PriceDetailDTO extends JsonLdCommonDTO {

    private LiteralMap description;
    private AmountDTO amount;
    private List<Identifier> identifier = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private LiteralMap prefLabel;

    public PriceDetailDTO() {
        super();
    }

    @JsonCreator
    public PriceDetailDTO(String uri) {
        super(uri);
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public AmountDTO getAmount() {
        return amount;
    }

    public void setAmount(AmountDTO amount) {
        this.amount = amount;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceDetailDTO)) return false;
        if (!super.equals(o)) return false;
        PriceDetailDTO that = (PriceDetailDTO) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(prefLabel, that.prefLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, amount, identifier, additionalNote, prefLabel);
    }
}
