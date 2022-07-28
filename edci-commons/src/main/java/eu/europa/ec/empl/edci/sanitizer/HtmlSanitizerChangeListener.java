package eu.europa.ec.empl.edci.sanitizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.html.HtmlChangeListener;

import javax.annotation.Nullable;
import java.util.Set;

public class HtmlSanitizerChangeListener implements HtmlChangeListener<Set<String>> {

    public static final Logger logger = LogManager.getLogger(HtmlSanitizerChangeListener.class);

    @Override
    public void discardedTag(@Nullable Set<String> discardedSet, String s) {
        discardedSet.add("Tag: " + s);
    }

    @Override
    public void discardedAttributes(@Nullable Set<String> discardedSet, String s, String... strings) {
        discardedSet.add("Tag: " + s + " Attributes: " + String.join(", ", strings));
    }
}
