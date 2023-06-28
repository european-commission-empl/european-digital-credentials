package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import java.util.List;

public class AwardingProcessDCView {

    private List<IdentifierDTView> identifier; //*

    private TextDTView description; //0..1

    private List<TextDTView> additionalNote; //*

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String awardingDate; //0..1

    private LocationDCView awardingLocation; //0..1

    private CodeDTView educationalSystemNote;

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public TextDTView getDescription() {
        return description;
    }

    public void setDescription(TextDTView description) {
        this.description = description;
    }

    public List<TextDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<TextDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(String awardingDate) {
        this.awardingDate = awardingDate;
    }

    public LocationDCView getAwardingLocation() {
        return awardingLocation;
    }

    public void setAwardingLocation(LocationDCView awardingLocation) {
        this.awardingLocation = awardingLocation;
    }

    public CodeDTView getEducationalSystemNote() {
        return educationalSystemNote;
    }

    public void setEducationalSystemNote(CodeDTView educationalSystemNote) {
        this.educationalSystemNote = educationalSystemNote;
    }
}