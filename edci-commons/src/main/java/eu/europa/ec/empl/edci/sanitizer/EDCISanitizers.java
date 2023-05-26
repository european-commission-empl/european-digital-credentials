package eu.europa.ec.empl.edci.sanitizer;

import org.owasp.html.CssSchema;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.regex.Pattern;

public final class EDCISanitizers {

    public static final PolicyFactory PAGE_BREAK = (new HtmlPolicyBuilder()).allowElements("page-break-beacon").toFactory();

    private EDCISanitizers() {
    }

}
