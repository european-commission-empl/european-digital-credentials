package eu.europa.ec.empl.edci.sanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.net.URI;
import java.util.List;
import java.util.Set;

public class EDCISanitizedHtml {

    private Set<String> sanitizedElems;
    private List<String> html;

    private EDCISanitizedHtml() {
    }

    public EDCISanitizedHtml(Set<String> sanitizedElems, List<String> html) {
        this.sanitizedElems = sanitizedElems;
        this.html = html;
    }

    public Set<String> getSanitizedElems() {
        return sanitizedElems;
    }

    public void setSanitizedElems(Set<String> sanitizedElems) {
        this.sanitizedElems = sanitizedElems;
    }

    public List<String> getHtml() {
        return html;
    }

    public void setHtml(List<String> html) {
        this.html = html;
    }
}
