package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.MultilangText;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

/**
 * Util class for translation of multi-language fields.
 */
public class MultilangFieldUtil {

    /**
     * Gets localized content using a provided language.
     *
     * @param multilang the fields to be localized
     * @param language  the language
     * @return the localized content
     */
    public static Optional<? extends MultilangText> getLocalizedContent(List<? extends MultilangText> multilang, String language) {
        String langaux = language != null ? language : LocaleContextHolder.getLocale().getLanguage();
        return multilang != null ? multilang.stream().filter(content -> langaux.equalsIgnoreCase(content.getLanguage())).findFirst() : Optional.empty();
    }

    /**
     * Gets localized content in the provided language or any other if not present.
     *
     * @param multilang the fields to be localized
     * @param language  the language
     * @return the localized content
     */
    public static MultilangText getContentOrAny(List<? extends MultilangText> multilang, String language) {
        Optional<? extends MultilangText> content = getLocalizedContent(multilang, language);

        if (content.isPresent()) {
            return content.get();
        }
        content = getLocalizedContent(multilang, DataModelConstants.Defaults.DEFAULT_LOCALE);
        if (content.isPresent()) {
            return content.get();
        }

        if (multilang != null && multilang.size() > 0) {
            return multilang.get(0);
        }

        return null;
    }

    /**
     * Gets localized content in the provided language or any other if not present.
     *
     * @param multilang the the fields to be localized
     * @param language  the language
     * @return the localized content as String
     */
    public static String getLiteralStringOrAny(List<? extends MultilangText> multilang, String language) {
        MultilangText content = getContentOrAny(multilang, language);
        return content != null ? content.getContent() : null;
    }

    /**
     * Gets localized content in the provided language.
     *
     * @param multilang the the fields to be localized
     * @param language  the language
     * @return the localized content as String
     */
    public static Optional<String> getLiteralString(List<? extends MultilangText> multilang, String language) {
        Optional<String> string = Optional.empty();
        Optional<? extends MultilangText> content = getLocalizedContent(multilang, language);
        if (content.isPresent()) {
            string = Optional.of(content.get().getContent());
        }
        return string;
    }


    private static List<String> getContentOrAny(LiteralMap literal, String language) {

        if (literal == null) {
            return null;
        }

        List<String> content = literal.get(language);

        if (content != null && !content.isEmpty()) {
            return content;
        }
        content = literal.get(DataModelConstants.Defaults.DEFAULT_LOCALE);

        if (content != null && !content.isEmpty()) {
            return content;
        }

        if (literal.values() != null && literal.values().size() > 0) {
            return literal.values().stream().filter(l -> !l.isEmpty()).findFirst().orElseGet(null);
        }

        return null;
    }

    /**
     * Gets localized content in the provided language or any other if not present.
     *
     * @param literal  the fields to be localized
     * @param language the language
     * @return the localized content as List<String>
     */
    public static List<String> getLiteralStringListOrAny(LiteralMap literal, String language) {
        return getContentOrAny(literal, language);
    }

    /**
     * Gets localized content in the provided language or any other if not present.
     *
     * @param literal  the fields to be localized
     * @param language the language
     * @return the localized content as String
     */
    public static String getLiteralStringOrAny(LiteralMap literal, String language) {
        List<String> stringList = getContentOrAny(literal, language);
        return stringList != null ? stringList.get(0) : null;
    }

    /**
     * Gets localized content in the provided language.
     *
     * @param literal  the the fields to be localized
     * @param language the language
     * @return the localized content as List<String>
     */
    public static Optional<List<String>> getLiteralStringList(LiteralMap literal, String language) {
        Optional<List<String>> string = Optional.empty();
        List<String> literalList = getContentOrAny(literal, language);
        if (literalList != null && !literalList.isEmpty()) {
            string = Optional.of(literalList);
        }
        return string;
    }

    /**
     * Gets localized content in the provided language.
     *
     * @param literal  the fields to be localized
     * @param language the language
     * @return the localized content as String
     */
    public static Optional<String> getLiteralString(LiteralMap literal, String language) {
        Optional<String> string = Optional.empty();
        String literalString = getLiteralStringOrAny(literal, language);
        if (literalString != null && !literalString.isEmpty()) {
            string = Optional.of(literalString);
        }
        return string;
    }

}
