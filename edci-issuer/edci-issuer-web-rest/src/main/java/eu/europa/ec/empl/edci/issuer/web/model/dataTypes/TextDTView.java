package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.ArrayList;
import java.util.List;


public class TextDTView extends DataTypeView {

    private List<ContentDTView> contents = new ArrayList<>();

    public List<ContentDTView> getContents() {
        return contents;
    }

    public void setContents(List<ContentDTView> contents) {
        this.contents = contents;
    }
}