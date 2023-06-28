package eu.europa.ec.empl.edci.model.view.fields;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkFieldView {

    private URI link;
    private List<String> title;
    private String targetFramework;
    private Map<String, List<String>> titleAvailableLangs = new HashMap<>();

    public String getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(String targetFramework) {
        this.targetFramework = targetFramework;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public Map<String, List<String>> getTitleAvailableLangs() {
        return titleAvailableLangs;
    }

    public void setTitleAvailableLangs(Map<String, List<String>> titleAvailableLangs) {
        this.titleAvailableLangs = titleAvailableLangs;
    }
}
