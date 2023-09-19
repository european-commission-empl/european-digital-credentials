package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.List;

public class NoteDTView extends DataTypeView {

    private List<ContentDTView> contents; //?

    private CodeDTView subject; //?

    public List<ContentDTView> getContents() {
        return contents;
    }

    private Boolean isMoreInformation;

    public void setContents(List<ContentDTView> contents) {
        this.contents = contents;
    }

    public CodeDTView getSubject() {
        return subject;
    }

    public void setSubject(CodeDTView subject) {
        this.subject = subject;
    }

    public Boolean getMoreInformation() {
        return isMoreInformation;
    }

    public void setMoreInformation(Boolean moreInformation) {
        isMoreInformation = moreInformation;
    }
}