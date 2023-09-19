package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:displayParameter:")
public class DisplayParameterDTO extends JsonLdCommonDTO {

    @NotNull
    private List<ConceptDTO> language = new ArrayList<>();
    private LiteralMap description;
    private String summaryDisplay;
    private List<IndividualDisplayDTO> individualDisplay = new ArrayList<>();
    @NotNull
    private ConceptDTO primaryLanguage;
    @NotNull
    private LiteralMap title;

    public DisplayParameterDTO() {
        super();
    }

    @JsonCreator
    public DisplayParameterDTO(String uri) {
        super(uri);
    }

    public List<IndividualDisplayDTO> getIndividualDisplay() {
        return individualDisplay;
    }

    public List<ConceptDTO> getLanguage() {
        return language;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public String getSummaryDisplay() {
        return summaryDisplay;
    }

    public void setSummaryDisplay(String summaryDisplay) {
        this.summaryDisplay = summaryDisplay;
    }

    public ConceptDTO getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(ConceptDTO primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public void setLanguage(List<ConceptDTO> language) {
        this.language = language;
    }

    public void setIndividualDisplay(List<IndividualDisplayDTO> individualDisplay) {
        this.individualDisplay = individualDisplay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayParameterDTO)) return false;
        if (!super.equals(o)) return false;
        DisplayParameterDTO that = (DisplayParameterDTO) o;
        return Objects.equals(language, that.language) &&
                Objects.equals(description, that.description) &&
                Objects.equals(summaryDisplay, that.summaryDisplay) &&
                Objects.equals(individualDisplay, that.individualDisplay) &&
                Objects.equals(primaryLanguage, that.primaryLanguage) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), language, description, summaryDisplay, individualDisplay, primaryLanguage, title);
    }
}
