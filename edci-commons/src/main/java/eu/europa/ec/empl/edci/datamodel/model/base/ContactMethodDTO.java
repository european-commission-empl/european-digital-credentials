package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;

public abstract class ContactMethodDTO {

    private Note description; //0..1

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }
}