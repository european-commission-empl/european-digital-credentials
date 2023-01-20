package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import java.net.URI;
import java.util.List;

public class WebDocumentDCView extends DataContainerView {

    private URI content; //1

    private TextDTView title; //0..1

    private CodeDTView language; //0..1

    private List<CodeDTView> subject; //*

    public URI getContent() {
        return content;
    }

    public void setContent(URI content) {
        this.content = content;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public CodeDTView getLanguage() {
        return language;
    }

    public void setLanguage(CodeDTView language) {
        this.language = language;
    }

    public List<CodeDTView> getSubject() {
        return subject;
    }

    public void setSubject(List<CodeDTView> subject) {
        this.subject = subject;
    }
}