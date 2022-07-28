package eu.europa.ec.empl.edci.issuer.common.constants;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Arrays;

public class XLS {


    public static class Recipients {
        public static final String DATA_SHEET_NAME = "data";
        public static final int REFERENCES_ROW = 0;
        public static final int FIELD_TYPE_ROW = 1;
        public static final int LABELS_ROW = 2;
        public static final int DESCRIPTION_ROW = 3;
        public static final int LAST_HEADER_ROW = DESCRIPTION_ROW;
        public static final String LABEL_SEPARATOR = ">";
        public static final String XLS_EXTENSION = "xls";
        public static final String INCLUDE_CHAR_Y = "y";
        public static final IndexedColors COLOR_LABELS_ROW = IndexedColors.GREY_40_PERCENT;
        public static final IndexedColors COLOR_DESCRIPTION_ROW = IndexedColors.GREY_25_PERCENT;
        public static final IndexedColors COLOR_DESCRIPTION_MANDATORY = IndexedColors.LIGHT_ORANGE;
        public static final IndexedColors FONT_COLOR = IndexedColors.BLACK;
        public static final short DESCRIPTION_ROW_FONTSIZE = 10;
        public static final short LABELS_ROW_FONTSIZE = 12;

        public enum FIELD_TYPE {
            FIELD("field"),
            RELATION("relation");

            private String type;

            public String getType() {
                return this.type;
            }

            public static FIELD_TYPE forName(String name) {
                return Arrays.stream(FIELD_TYPE.values()).filter(fieldType -> fieldType.getType().equals(name)).findFirst().orElse(null);
            }

            FIELD_TYPE(String type) {
                this.type = type;
            }
        }
    }


}
