package eu.europa.ec.empl.edci.constants;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class MediaType extends org.springframework.http.MediaType {

    public static final org.springframework.http.MediaType APPLICATION_XLSX = valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final String APPLICATION_XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final org.springframework.http.MediaType APPLICATION_XLSM = valueOf("application/vnd.ms-excel.sheet.macroEnabled.12");
    public static final String APPLICATION_XLSM_VALUE = "application/vnd.ms-excel.sheet.macroEnabled.12";

    public static final org.springframework.http.MediaType APPLICATION_XLS = valueOf("application/vnd.ms-excel");
    public static final String APPLICATION_XLS_VALUE = "application/vnd.ms-excel";

    public MediaType(String type) {
        super(type);
    }

    public MediaType(String type, String subtype) {
        super(type, subtype, Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public MediaType(String type, String subtype, double qualityValue) {
        this(type, subtype, Collections.singletonMap("q", Double.toString(qualityValue)));
    }

    public MediaType(org.springframework.http.MediaType other, Charset charset) {
        super(other, charset);
    }

    public MediaType(org.springframework.http.MediaType other, Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

}
