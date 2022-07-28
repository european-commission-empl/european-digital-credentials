package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"category", "description"})
public class ResultDistributionDTO {
    @Valid
    private List<ResultCategoryDTO> category; //*
    @Valid
    private Note description; //0..1

    public List<ResultCategoryDTO> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryDTO> category) {
        this.category = category;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }
}