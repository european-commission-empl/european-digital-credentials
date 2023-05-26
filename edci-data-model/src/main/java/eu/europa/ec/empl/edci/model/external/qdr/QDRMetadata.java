package eu.europa.ec.empl.edci.model.external.qdr;

import java.util.ArrayList;
import java.util.List;

public class QDRMetadata {

    private QDRValue language;
    private List<QDRValue> availableLanguages = new ArrayList<>();

    public QDRValue getLanguage() {
        return language;
    }

    public void setLanguage(QDRValue language) {
        this.language = language;
    }

    public List<QDRValue> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<QDRValue> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }
}
