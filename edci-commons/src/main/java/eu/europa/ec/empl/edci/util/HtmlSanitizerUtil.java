package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.utils.NamespaceResolver;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.sanitizer.EDCISanitizedHtml;
import eu.europa.ec.empl.edci.sanitizer.EDCISanitizers;
import eu.europa.ec.empl.edci.sanitizer.HtmlSanitizerChangeListener;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component("HtmlSanitizerUtil")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HtmlSanitizerUtil {

    public static final Logger logger = LogManager.getLogger(HtmlSanitizerUtil.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private XmlUtil xmlUtil;

    public static final Pattern WILDCARD_PATTERN = Pattern.compile("\\[\\$([^\\$]*)\\$\\]");
    public static final Pattern I18N_PATTERN = Pattern.compile("\\(\\$([^\\$]*)\\$\\)");
    public static final Pattern DATE_FULL_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})(T\\d{2}:\\d{2}:\\d{2}).*");
    public static final Pattern DATE_SHORT_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
    public static final PolicyFactory IMAGES_ONLY_DATA_POLICY = (new HtmlPolicyBuilder()).allowUrlProtocols("data").allowElements(new String[]{"img"}).allowAttributes(new String[]{"alt", "src"}).onElements(new String[]{"img"})
            .allowAttributes(new String[]{"border", "height", "width"}).onElements(new String[]{"img"}).toFactory();

    private boolean hasChildElements(Element el) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private XPath prepareXPath(Document doc) {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        xpath.setNamespaceContext(new NamespaceResolver(doc));
        return xpath;
    }

    private Document buildCredentialDoc(String xml) {

        Document doc = null;

        try (InputStream xmlStream = IOUtils.toInputStream(xml, "UTF-8")) {

            DocumentBuilderFactory factory = xmlUtil.getDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlStream);

        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return doc;
    }

    private String buildImageBase64(Element mediaObjectElement) {
        String value = null;
        try {
            String base64Content = mediaObjectElement.getElementsByTagName("content").item(0).getTextContent().trim();
            String extension = null;
            if (mediaObjectElement.getElementsByTagName("contentType").item(0) != null) {
                extension = ((Element) ((Element) mediaObjectElement.getElementsByTagName("contentType").item(0)).getElementsByTagName("targetName").item(0)).getElementsByTagName("text").item(0).getTextContent().trim().toLowerCase();
            } else if (mediaObjectElement.getElementsByTagName("contentUrl") != null) {
                String[] urlSplit = mediaObjectElement.getElementsByTagName("contentUrl").item(0).getTextContent().split("\\.");
                extension = urlSplit[urlSplit.length - 1].trim().toLowerCase();
            }

            if (extension != null && base64Content != null) {
                value = "data:image/".concat(extension).concat(";base64,").concat(base64Content);
            }

        } catch (Exception e) {
            logger.error(e);
//            Transparent image
            value = "data:image/".concat("png").concat(";base64,")
                    .concat("iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAAnElEQVR42u3RAQ0AAAgDoJvc6FrDOahAJdPhjBIiBCFCECIEIUIQIkSIEIQIQYgQhAhBiBCEIEQIQoQgRAhChCAEIUIQIgQhQhAiBCEIEYIQIQgRghAhCEGIEIQIQYgQhAhBCEKEIEQIQoQgRAhCECIEIUIQIgQhQhCCECEIEYIQIQgRghCECEGIEIQIQYgQhAgRIgQhQhAiBCHfLQGKlZ3aNUP0AAAAAElFTkSuQmCC");
        }

        return value;
    }

    private boolean isMediaObject(Element node) {
        if (node != null && "logo".equalsIgnoreCase(node.getTagName()) || "background".equalsIgnoreCase(node.getTagName())) {
            return true;
        } else {
            return false;
        }
    }

    private String getPeriod(Element node) {
        String period = null;
        try {
            period = PeriodFormat.wordBased(LocaleContextHolder.getLocale()).print(Period.parse(node.getTextContent()));
        } catch (Exception e) {
            //We aren't reading a Period
            period = null;
        }
        return period;
    }

    private String getDate(Element node, String customPattern) {
        String date = null;
        try {

            Matcher matcherFull = DATE_FULL_PATTERN.matcher(node.getTextContent());
            Matcher matcherShort = DATE_SHORT_PATTERN.matcher(node.getTextContent());

            if (matcherFull.matches()) {
                Date dateTmp = new SimpleDateFormat(EDCIConstants.DATE_ISO_8601).parse(node.getTextContent());
                if (!StringUtils.isEmpty(customPattern)) {
                    date = new SimpleDateFormat(customPattern).format(dateTmp);
                } else {
                    date = new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT).format(dateTmp);
                }
            } else if (matcherShort.matches()) {
                Date dateTmp = new SimpleDateFormat(EDCIConstants.DATE_LOCAL).parse(node.getTextContent());
                if (!StringUtils.isEmpty(customPattern)) {
                    date = new SimpleDateFormat(customPattern).format(dateTmp);
                } else {
                    date = new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL).format(dateTmp);
                }
            }

        } catch (Exception e) {
            //We aren't reading a Date
            date = null;
        }

        return date;
    }

    private String getMultilangText(Element node) {

        String value = null;

        try {
            List<Node> nodeList = IntStream.range(0, node.getChildNodes().getLength())
                    .mapToObj(node.getChildNodes()::item).filter(n -> "text".equalsIgnoreCase(n.getNodeName())).collect(Collectors.toList());

            value = nodeList.stream().filter(n -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(n.getAttributes().getNamedItem("lang").getNodeValue()))
                    .map(n -> n.getTextContent()).findFirst().orElse(null);

            if (!nodeList.isEmpty() && value == null) {
                value = nodeList.stream().filter(n -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(n.getAttributes().getNamedItem("lang").getNodeValue()))
                        .map(n -> n.getTextContent()).findFirst().orElse(null);
            }

            if (!nodeList.isEmpty() && value == null) {
                value = nodeList.get(0).getTextContent();
            }
        } catch (Exception e) {
            logger.error(e);
            value = edciMessageService.getMessage("global.literal.not.found");
        }

        return value;
    }

    protected Object getReturnType(Document doc, XPath xpath, String expression) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(expression);
        Object returnValue = null;
        try {
            returnValue = (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            returnValue = expr.evaluate(doc);
        }
        return returnValue;
    }

    protected String evaluateElement(Element result, String[] expressionPipe) {

        String value = null;

        if (result == null) {
            logger.debug("Property not found using expression " + Arrays.stream(expressionPipe).collect(Collectors.joining("|")));
        } else if (isMediaObject(result)) {
            value = buildImageBase64(result);
        } else if (!hasChildElements(result)) {

            String period = getPeriod(result);

            String date = getDate(result, expressionPipe.length > 1 ? expressionPipe[1] : null);

            if (period != null) {
                value = period;
            } else if (date != null) {
                value = date;
            } else {
                value = result.getTextContent();
            }

        } else {
            value = getMultilangText(result);
        }

        return value;

    }

    public String getValue(Document doc, XPath xpath, String expression) {

        String value = null;

        String[] expressionPipe = expression.split("\\|");

        try {

            Object node = getReturnType(doc, xpath, expressionPipe[0]);

            if (node instanceof String) {
                value = (String) node;
            } else if (Element.class.isAssignableFrom(node.getClass())) {
                value = evaluateElement((Element) node, expressionPipe);
            } else if (Node.class.isAssignableFrom(node.getClass())) {
                value = ((Node) node).getTextContent();
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return value;
    }

    public List<String> splitPages(String html) {

        String[] htmlPages = html.split("<page-break-beacon(/?)>");

        return Arrays.asList(htmlPages);
    }

    public String preparePageBreaks(String html) {

        org.jsoup.nodes.Document parsedHtml = Jsoup.parse(html);

        Elements elemsBefore = parsedHtml.select("[style*=page-break-before]").before("<page-break-beacon/>");

        elemsBefore.forEach(elem -> {
            if (StringUtils.isBlank(elem.html().trim())) {
                elem.remove();
            } else {
                Attribute attrBefore = elem.attributes().asList().get(0);
                if (attrBefore.getKey().equals("style")) {
                    attrBefore.setValue(attrBefore.getValue().replaceAll("page-break-before(\\s*):(\\s*)\\w+(;?)", ""));
                }
            }
        });

        Elements elemsAfter = parsedHtml.select("[style*=page-break-after]").after("<page-break-beacon/>");

        elemsAfter.forEach(elem -> {
            if (StringUtils.isBlank(elem.html().trim())) {
                elem.remove();
            } else {
                Attribute attrAfter = elem.attributes().asList().get(0);
                if (attrAfter.getKey().equals("style")) {
                    attrAfter.setValue(attrAfter.getValue().replaceAll("page-break-after(\\s*):(\\s*)\\w+(;?)", ""));
                }
            }
        });


        return parsedHtml.html();
    }

    public EDCISanitizedHtml processTemplate(String dirtyHTML) {

        String clean = "";

        Set<String> discardedElems = new LinkedHashSet<>();

        try {

            PolicyFactory sanitizer = Sanitizers.BLOCKS
                    .and(Sanitizers.FORMATTING)
                    .and(IMAGES_ONLY_DATA_POLICY)
                    .and(Sanitizers.TABLES)
                    .and(Sanitizers.STYLES)
                    .and(EDCISanitizers.PAGE_BREAK);

            HtmlSanitizerChangeListener sanitizerListener = new HtmlSanitizerChangeListener();

            clean = sanitizer.sanitize(dirtyHTML, sanitizerListener, discardedElems);

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException("parse.diploma.error").setCause(e);
        }

        return new EDCISanitizedHtml(discardedElems, splitPages(clean));
    }

    public EDCISanitizedHtml processHTML(EuropassCredentialDTO europassCredentialDTO) {

        EDCISanitizedHtml html = null;
        try {
            html = processHTML(europassCredentialDTO.getDisplay().getHtml(), edciCredentialModelUtil.toXML(europassCredentialDTO));

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException("parse.diploma.error").setCause(e);
        }
        return html;

    }

    public EDCISanitizedHtml processHTML(String dirtyHTML, String xml) {

        String clean = "";

        Set<String> discardedElems = new LinkedHashSet<>();

        try {

            PolicyFactory sanitizer = Sanitizers.BLOCKS
                    .and(Sanitizers.FORMATTING)
                    .and(IMAGES_ONLY_DATA_POLICY)
                    .and(Sanitizers.TABLES)
                    .and(Sanitizers.STYLES)
                    .and(EDCISanitizers.PAGE_BREAK);

            Document doc = buildCredentialDoc(xml);
            XPath xpath = prepareXPath(doc);

            HashMap<String, String> wildCards = new HashMap<>();
            Matcher m = WILDCARD_PATTERN.matcher(dirtyHTML);
            while (m.find()) {
                if (!wildCards.containsKey(m.group())) {
                    String wildCardValue = getValue(doc, xpath, m.group(1));
                    wildCards.put(m.group(), wildCardValue != null ? wildCardValue : " ");
                }
            }

            HashMap<String, String> literals = new HashMap<>();
            Matcher mLit = I18N_PATTERN.matcher(dirtyHTML);
            while (mLit.find()) {
                if (!literals.containsKey(mLit.group())) {
                    String literalValue = getValue(doc, xpath, "/eup:europassCredential/eup:displayParameters/eup:labels/eup:prefLabel[@key='" + mLit.group(1) + "']");
                    literals.put(mLit.group(), literalValue != null ? literalValue : edciMessageService.getMessage(mLit.group(1)));
                }
            }

            for (String key : wildCards.keySet()) {
                String wildCardValue = wildCards.get(key);
                if (wildCardValue != null) {
                    dirtyHTML = dirtyHTML.replace(key, wildCards.get(key));
                } else {
                    dirtyHTML = dirtyHTML.replace(key, " ");
                }
            }

            for (String key : literals.keySet()) {
                String literalValue = literals.get(key);
                if (literalValue != null) {
                    dirtyHTML = dirtyHTML.replace(key, literals.get(key));
                } else {
                    dirtyHTML = dirtyHTML.replace(key, edciMessageService.getMessage("global.literal.not.found"));
                }
            }

            HtmlSanitizerChangeListener sanitizerListener = new HtmlSanitizerChangeListener();
            clean = sanitizer.sanitize(preparePageBreaks(dirtyHTML), sanitizerListener, discardedElems);

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException("parse.diploma.error").setCause(e);
        }

        return new EDCISanitizedHtml(discardedElems, splitPages(clean));

    }


    private byte[] getClassPathResource(String path) {
        try {
            Resource backgroundImageResource = new ClassPathResource(path);
            return StreamUtils.copyToByteArray(backgroundImageResource.getInputStream());
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            throw new EDCIException().setCause(ioe).addDescription(String.format("IOException from %s", path));
        }
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }
}
