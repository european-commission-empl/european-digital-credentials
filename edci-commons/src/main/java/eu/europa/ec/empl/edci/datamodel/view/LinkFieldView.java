package eu.europa.ec.empl.edci.datamodel.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LinkFieldView {

    private URL link;
    private String title;
    private Map<String, String> titleAvailableLangs = new HashMap<>();

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getTitleAvailableLangs() {
        return titleAvailableLangs;
    }

    public void setTitleAvailableLangs(Map<String, String> titleAvailableLangs) {
        this.titleAvailableLangs = titleAvailableLangs;
    }
}
