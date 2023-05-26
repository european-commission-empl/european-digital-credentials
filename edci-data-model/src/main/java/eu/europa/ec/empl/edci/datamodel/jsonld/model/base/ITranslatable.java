package eu.europa.ec.empl.edci.datamodel.jsonld.model.base;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;

public interface ITranslatable {

    default LiteralMap getContents() {
        return getContents() == null ? new LiteralMap() : getContents();
    }

    default String getContent(String language) {
        return MultilangFieldUtil.getLiteralStringOrAny(getContents(), language);
    }

}
