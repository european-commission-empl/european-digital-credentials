package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.ITranslatable;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.stream.Collectors;


public class LiteralMap extends HashMap<String, List<String>> implements ITranslatable {

    public LiteralMap() {
        super();
    }

    public LiteralMap(Map<String, List<String>> map) {
        super(map);
    }

    public LiteralMap(String key, String value) {
        super();
        this.put(key, value);
    }

    public LiteralMap(String key, List<String> value) {
        super();
        this.put(key, value);
    }

    @Override
    public LiteralMap getContents() {
        return this;
    }

    public List<String> overrideValue(String key, String value) {
        return this.put(key, new ArrayList<>(Arrays.asList(value)));
    }

    public List<String> put(String key, String value) {
        List<String> result;
        if (value != null) {
            List<String> values = this.get(key);
            if (values != null) {
                result = values;
                values.add(value);
            } else {
                result = this.put(key, new ArrayList<>(Arrays.asList(value)));
            }
        } else {
            result = this.get(key);
        }

        return result;
    }

    @Override
    public String toString() {
        return MultilangFieldUtil.getLiteralStringOrAny(this, LocaleContextHolder.getLocale().toString());
    }

    public List<String> toStringList() {
        return MultilangFieldUtil.getLiteralStringListOrAny(this, LocaleContextHolder.getLocale().toString());
    }

    public static LiteralMap fromMap(Map<String, String> map) {
        Map<String, List<String>> creationMap = map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> Collections.singletonList(entry.getValue())));
        return new LiteralMap(creationMap);
    }
}
