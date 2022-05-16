package eu.europa.ec.empl.edci.model.qmsaccreditation;

import java.util.List;

public class QMSNoteDTO {

    private List<QMSLabelDTO> contents;
    private String topic;

    public List<QMSLabelDTO> getContents() {
        return contents;
    }

    public void setContents(List<QMSLabelDTO> contents) {
        this.contents = contents;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
