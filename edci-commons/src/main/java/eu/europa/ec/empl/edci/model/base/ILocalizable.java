package eu.europa.ec.empl.edci.model.base;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.model.dataTypes.IContent;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

public interface ILocalizable<T extends IContent> {

    public abstract List<T> getContents();

    public abstract T getContentsNewInstance();

    /*Return Content based on language*/
    default Optional<T> getLocalizedContent(String language) {
        return this.getLocalizedContent(language, this.getContents());
    }

    default T getContent(String language) {
        return this.getContent(language, this.getContents());
    }

    default Optional<T> getLocalizedContent(String language, List<T> contents) {
        String langaux = language != null ? language : LocaleContextHolder.getLocale().getLanguage();
        return contents != null ? contents.stream().filter(content -> langaux.equalsIgnoreCase(content.getLanguage())).findFirst() : Optional.empty();
    }

    default T getContent(String language, List<T> contents) {
        Optional<T> content = this.getLocalizedContent(language, contents);

        if (content.isPresent()) {
            return content.get();
        }
        content = this.getLocalizedContent(EDCIConstants.DEFAULT_LOCALE, contents);
        if (content.isPresent()) {
            return content.get();
        }

        if (contents.size() > 0) {
            return contents.get(0);
        }

        return null;
    }

    /*Get String of content based on language*/
    default String getStringContent(String language) {
        return this.getStringContent(language, this.getContents());
    }

    default String getStringContent() {
        return this.getStringContent(LocaleContextHolder.getLocale().getLanguage(), this.getContents());
    }

    default Optional<String> getLocalizedString(String language) {
        return this.getLocalizedString(language, this.getContents());
    }

    default String getStringContent(String language, List<T> contents) {
        return this.getContent(language, contents) != null ? this.getContent(language, contents).getContent() : null;
    }


    default Optional<String> getLocalizedString(String language, List<T> contents) {
        Optional<String> string = Optional.empty();
        Optional<T> content = this.getLocalizedContent(language, contents);
        if (content.isPresent()) {
            string = Optional.of(content.get().getContent());
        }
        return string;
    }

    /*Get String based on content language, if not available, return anything*/
    default String getLocalizedStringOrAny(String locale) {
        Optional<String> optional = this.getLocalizedString(locale, this.getContents());
        if (!optional.isPresent()) {
            return getAnyLanguageString();
        }
        return optional.get();
    }

    default String getAnyLanguageString() {
        Optional<T> content = this.getContents() != null ?
                this.getContents().stream().filter(streamContent -> streamContent.getContent() != null && streamContent.getLanguage() != null).findFirst()
                : Optional.empty();
        return content.isPresent() ? content.get().getContent() : "";
    }

    /**
     * Methods for adding a content with a language NOTE: do not change name to addContent or else reflection issues may occur in XLS
     */

    default void setContent(String language, String contentString) {
        T content = this.getOrInstanciateContent(language);
        content.setContent(contentString);
        content.setLanguage(language);
    }

    default T getOrInstanciateContent(String language) {
        Optional<T> optionalContent = this.getLocalizedContent(language);
        if (optionalContent.isPresent()) {
            return optionalContent.get();
        } else {
            T content = getContentsNewInstance();
            content.setLanguage(language);
            this.getContents().add(content);
            return content;
        }
    }
}
