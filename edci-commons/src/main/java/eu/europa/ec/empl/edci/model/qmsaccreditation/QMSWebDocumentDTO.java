package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSWebDocumentDTO {

    private URI url;
    private List<QMSLabelDTO> titles;
    private List<QMSCodeDTO> languages;
    private List<QMSCodeDTO> subjects = new ArrayList<>();
    //ToDO -> languages/subjes when CODE fields are added

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public List<QMSLabelDTO> getTitles() {
        return titles;
    }

    public void setTitles(List<QMSLabelDTO> titles) {
        this.titles = titles;
    }

    public List<QMSCodeDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<QMSCodeDTO> languages) {
        this.languages = languages;
    }

    public List<QMSCodeDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<QMSCodeDTO> subjects) {
        this.subjects = subjects;
    }
}
