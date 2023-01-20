package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.List;

public class NoteDTView extends DataTypeView {

    private List<ContentDTView> contents; //?

    private String topic; //?

    public List<ContentDTView> getContents() {
        return contents;
    }

    public void setContents(List<ContentDTView> contents) {
        this.contents = contents;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}