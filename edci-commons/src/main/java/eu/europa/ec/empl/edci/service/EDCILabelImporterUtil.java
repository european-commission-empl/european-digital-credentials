package eu.europa.ec.empl.edci.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EDCILabelImporterUtil {

    private static final String SOURCE_LABELS_DIRECTORY = "edci-commons/src/main/resources/labels/new";
    private static final String BACKEND_LABELS_DIRECTORY = "src/main/resources-unfiltered";
    private static final String FRONTEND_LABELS_DIRECTORY = "src/main/angular/src/assets/i18n";
    private static final String NEWLINE_LABEL_CHAR_UNIX = "\\n";

    private static final String LABELS_FILES_PREFIX = "messages_";

//    private static final List<String> LANGUAGES = Arrays.asList("en");
    private static final List<String> LANGUAGES = Arrays.asList("bg", "cs", "da", "de", "el", "es", "et", "fi", "fr", "ga", "hr", "hu", "is", "it", "lt", "lv", "mk", "mt", "nl", "no", "pl", "pt", "ro", "sk", "sl", "sr", "sv", "tr");

    private static final String LABEL_FILE_DELIMITER_BACK = "=";
    private static final String LABEL_FILE_DELIMITER_FRONT = ":";
    private static final String SOURCE_FILE_DELIMITER = ",";
    // 0 -> comment character , 1...N -> ignore lines
    private static final List<String> BACK_COMMENTARY_MARKERS = Arrays.asList("#");
    private static final List<String> FRONT_COMMENTARY_MARKERS = Arrays.asList("//", "{", "}");
    private static final String FRONT_FILE_DELIMITER = "}";
    private static final String FRONT_ENDLINE_DELIMITER = ",";
    private static final String BACK_ENDLINE_DELIMITER = "";
    private static final String FRONT_VALUE_WRAPPER = "\"";
    private static final String BACK_VALUE_WRAPPER = "";

    public static void main(String[] args) {

        System.out.println("Starting ");

        System.out.println(">>>>>>>>>>>>>>>>> Source file: " + args[0]); //Labels_1.3.xlsx

        String sourceFileName = args[0];

        EDCILabelImporterUtil.updateAndAppendBackendLabels("issuer", "Issuer Back", sourceFileName);
        EDCILabelImporterUtil.updateAndAppendBackendLabels("viewer", "Viewer Back", sourceFileName);
        EDCILabelImporterUtil.updateAndAppendBackendLabels("wallet", "Wallet Back", sourceFileName);

        EDCILabelImporterUtil.updateAndAppendFrontendLabels("issuer", "Issuer Front", sourceFileName);
        EDCILabelImporterUtil.updateAndAppendFrontendLabels("viewer", "Viewer Front", sourceFileName);
    }

    private static void updateAndAppendFrontendLabels(String appName, String sheetName, String sourceFileName) {

        File sourceFile = EDCILabelImporterUtil.readSourceFile(sourceFileName);
        Map<String, Map<String, String>> frontSource = EDCILabelImporterUtil.parseGeneralFile(sourceFile, sheetName);

        for (String language : LANGUAGES) {
            //FRONT LABELS
            System.out.println("##### Updating FRONT labels for " + appName + " with lang \" " + language + "\"");
            File labelFrontFile = EDCILabelImporterUtil.readFrontLabelsFile(appName, language);
            boolean freshCreated = false;
            if (!labelFrontFile.exists()) {
                try {
                    //Create NEW JSON
                    freshCreated = true;
                    labelFrontFile.createNewFile();
                    EDCILabelImporterUtil.writeFile(labelFrontFile, "{\n}");
                } catch (IOException e) {
                    System.err.println("ERROR - COULD NOT CREATE NEW FILE " + labelFrontFile.getPath());
                }
            }

            //Count
            int countNew = 0;
            int countUpdated = 0;

            //Parse files to key/value maps
            Map<String, String> frontLabels = EDCILabelImporterUtil.parseFrontFile(labelFrontFile);

            //Add the changes to the file map
            for (Map.Entry<String, String> entry : frontSource.get(language).entrySet()) {
                String value = entry.getValue();
                value = value.replaceAll("…","...");
//                value = value.replaceAll("“","\"");
//                value = value.replaceAll("„","\"");
//                value = value.replace("”","\"");
                value = value.replace("\\\"", "\"");
                value = value.replace("\\:", ":");
                if (frontLabels.put(entry.getKey(), value.trim()) == null) {
                    countNew++;
                } else {
                    countUpdated++;
                }
            }

            //Convert map to json String
            String frontAppendedStringBuilder = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                frontAppendedStringBuilder = objectMapper.writeValueAsString(frontLabels);
            } catch (JsonProcessingException e) {
                System.err.println(e);
            }

            try {
                //OverWrite file
                EDCILabelImporterUtil.writeFile(labelFrontFile, frontAppendedStringBuilder);
                System.out.println(String.format("[%s - %s] added %d FRONT labels, updated %d FRONT labels", appName, language, countNew, countUpdated));
            } catch (IOException e) {
                System.err.println(String.format("ERROR WRITING FRONT FILE %s", labelFrontFile.getPath()));
            }
        }
    }

    private static void updateAndAppendBackendLabels(String appName, String sheetName, String sourceFileName) {

        File sourceFile = EDCILabelImporterUtil.readSourceFile(sourceFileName);
        Map<String, Map<String, String>> backSource = EDCILabelImporterUtil.parseGeneralFile(sourceFile, sheetName);

        for (String language : LANGUAGES) {

            //BACK LABELS
            System.out.println("##### Updating BACK labels for " + appName + " with lang \" " + language + "\"");
            //Read source and Dest file
            File backLabelFile = EDCILabelImporterUtil.readBackLabelsFile(appName, language);
            if (!backLabelFile.exists()) {
                try {
                    backLabelFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("ERROR - COULD NOT CREATE NEW FILE " + backLabelFile.getPath());
                }
            }

            //Count
            int countNew = 0;
            int countUpdated = 0;

            //Parse files to key/value maps
            Map<String, String> backLabels = EDCILabelImporterUtil.parseBackFile(backLabelFile);

            //Add the changes to the file map
            for (Map.Entry<String, String> entry : backSource.get(language).entrySet()) {
                String value = entry.getValue();
                value = value.replaceAll("…","...");
//                value = value.replaceAll("“","\"");
//                value = value.replaceAll("„","\"");
//                value = value.replace("”","\"");
                value = value.replace("\\\"", "\"");
                value = value.replace("\\:", ":");
                if (backLabels.put(entry.getKey(), value.trim()) == null) {
                    countNew++;
                } else {
                    countUpdated++;
                }
            }

            LinkedProperties updatedProperty = new LinkedProperties();
            Set<Map.Entry<String,String>> set = backLabels.entrySet();
            for (Map.Entry<String,String> entry : set) {
                updatedProperty.put(entry.getKey(), entry.getValue());
            }

            //OverWrite file
            try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(backLabelFile), StandardCharsets.UTF_8 )) {
                updatedProperty.store(output, null);
                System.out.println(String.format("[%s - %s] added %d BACK labels, updated %d BACK labels", appName, language, countNew, countUpdated));
            } catch (IOException io) {
                System.err.println(String.format("ERROR WRITING FRONT FILE %s", backLabelFile.getPath()));
            }

        }
    }

    private static void writeFile(File file, String content) throws IOException {
        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            System.out.println(String.format("Writing file %s", file.getPath()));
            oStreamWriter.write(content.toString());
        } catch (IOException e) {
            System.err.println(String.format("ERROR WRITING FILE %s", file.getPath()));
            throw e;
        }
    }

    private static String sanitizeKey(String key) {
        key = key.trim();
        return EDCILabelImporterUtil.unQuote(key);
    }

    private static String unQuote(String str) {
        String value = str;
        if (value.startsWith(("\"")) && value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        value = value.replaceAll("\"\"", "\"");
        return value;
    }


    //Creates path to source File(incoming updates - CSV front)
    private static File readSourceFile(String fileName) {
        return new File(SOURCE_LABELS_DIRECTORY.concat("/").concat(fileName));
    }

    //Creates path to labels File(the ones used by apps)
    private static File readBackLabelsFile(String appName, String language) {
        File labelsPath = new File(
                    "edci-"
                    .concat(appName)
                    .concat("/edci-")
                    .concat(appName)
                    .concat("-web/")
                    .concat(BACKEND_LABELS_DIRECTORY)
                    .concat("/")
                    .concat(LABELS_FILES_PREFIX)
                    .concat(language)
                    .concat(".properties")
        );
        return labelsPath;
    }

    private static File readFrontLabelsFile(String appName, String language) {
        File labelsPath = new File(
                    "edci-"
                    .concat(appName)
                    .concat("/edci-")
                    .concat(appName)
                    .concat("-web/")
                    .concat(FRONTEND_LABELS_DIRECTORY)
                    .concat("/")
                    .concat(language)
                    .concat(".json")
        );
        return labelsPath;
    }

    //Gets all the key-values from one language
    private static Map<String, String> getLangLiteralsMap(Sheet sheet, String lang) {

        Map<String, String> langValues = new HashMap<>();

        int keyRow = 1;
        int langRow = -1;

        Row header = sheet.getRow(0);
        for (Cell cell : header) {
            if (CellType.STRING.equals(cell.getCellType()) && cell.getStringCellValue().contains("(" + lang + ")")) {
                langRow = cell.getColumnIndex();
                break;
            }
        }

        if (langRow == -1) {
            throw new RuntimeException("Language (\" + lang + \") not found into the Excel sheet " + sheet.getSheetName());
        }

        for (Row rows : sheet) {
            if (rows.getRowNum() == 0) {
                //ignore header
            } else {
                Cell keysRow = rows.getCell(keyRow);
                Cell langLiteralRow = rows.getCell(langRow);
                try {
                    if (langLiteralRow == null || langLiteralRow.getStringCellValue().isEmpty()) {
                        if (keysRow != null && !keysRow.getStringCellValue().isEmpty()) {
                            System.err.println("WARNING! - Sheet " + sheet.getSheetName() + "(Row: " + rows.getRowNum() + "), Key: " + keysRow.getStringCellValue()
                                    + "Language " + lang + ". Label is empty");
                        }
                    } else {
                        langValues.put(keysRow.getStringCellValue(), langLiteralRow != null ? langLiteralRow.getStringCellValue() : "");
                    }
                } catch (Exception e) {
                    System.err.println("ERROR - Unable to get value from Sheet " + sheet.getSheetName() + " Row " + rows.getRowNum()
                            + " and Cells: " + keyRow + "," + langRow);
                    throw e;
                }
            }
        }
        //Remove empty key
        langValues.remove("");

        return langValues;

    }

    //Gets all the key-values from each language
    private static Map<String, Map<String, String>> getLangLiteralsMap(Sheet sheet) {

        Map<String, Map<String, String>> mapAllFileLangs = new HashMap<>();

        LANGUAGES.forEach( l -> mapAllFileLangs.put(l, getLangLiteralsMap(sheet, l)));

        return mapAllFileLangs;
    }

    //Parses file with all the apps and languages
    private static Map<String, Map<String, String>> parseGeneralFile(File xlsAllFile, String sheetName) {

        Map<String, Map<String, String>> mapSheet = new HashMap<>();

        try (FileInputStream fis=new FileInputStream(xlsAllFile);) {

            //creating workbook instance that refers to .xls file
            Workbook wb=new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve the object

            //Map<"lang",Map<"key","value_lang">

            mapSheet = getLangLiteralsMap(wb.getSheet(sheetName));

            System.out.println("PARSED FILE - " + xlsAllFile.getPath());
        } catch (Exception e) {
            System.err.println("ERROR PARSING FILE - " + xlsAllFile.getPath() + " / " + e.getMessage());
            throw new RuntimeException("ERROR PARSING FILE - " + xlsAllFile.getPath() + " / " + e.getMessage(), e);
        }
        return mapSheet;
    }

    private static boolean startsWith(String string, List<String> matches) {
        return matches.stream().anyMatch(match -> string.startsWith(match));
    }

    private static String[] splitLine(String line, String delimiter) {
        if (line.indexOf(delimiter) != -1) {
            String key = line.substring(0, line.indexOf(delimiter));
            if (key != null) {
                key = EDCILabelImporterUtil.sanitizeKey(key);
            }
            String value = line.substring(line.indexOf(delimiter) + 1);
            return new String[]{key, value};
        } else if (!line.isEmpty()) {
            return new String[]{line};
        } else {
            return null;
        }
    }

    //Parses file splitting lines on first delimiter found
    private static Map<String, String> parseFrontFile(File labelsFile) {
        TypeReference<LinkedHashMap<String,String>> typeRef
                = new TypeReference<LinkedHashMap<String, String>>() {};
        Map<String, String> labels = null;
        try {
            labels = new ObjectMapper().readValue(labelsFile, typeRef);
        } catch (IOException e) {
            System.out.println("ERROR! Parsing file - " + labelsFile.getPath());
            throw new RuntimeException("ERROR! Parsing file - " + labelsFile.getPath());
        }
        return labels;
    }

    //Parses file splitting lines on first delimiter found
    private static Map<String, String> parseBackFile(File labelsFile) {

        Map<String, String> labels = new LinkedHashMap<String, String>();

        try (InputStreamReader fileInputStream = new InputStreamReader(new FileInputStream(labelsFile), StandardCharsets.UTF_8 )) {
            LinkedProperties properties = new LinkedProperties();
            properties.load(fileInputStream);
            for (Map.Entry entry : properties.entrySet()) {
//                String value = properties.getProperty(key);
                labels.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            System.out.println("ERROR! Parsing file - " + labelsFile.getPath());
            throw new RuntimeException("ERROR! Parsing file - " + labelsFile.getPath());
        }

        return labels;
    }

    public static class LinkedProperties extends Properties{


        private static final long serialVersionUID = 1L;

        private Map<Object, Object> linkMap = new LinkedHashMap<Object,Object>();

        @Override
        public synchronized Object put(Object key, Object value){
            super.put(key, value);
            return linkMap.put(key, value);
        }

        @Override
        public synchronized boolean contains(Object value){
            return linkMap.containsValue(value);
        }

        @Override
        public boolean containsValue(Object value){
            return linkMap.containsValue(value);
        }

        @Override
        public synchronized Enumeration<Object> elements(){
            throw new UnsupportedOperationException(
                    "Enumerations are so old-school, don't use them, "
                            + "use keySet() or entrySet() instead");
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet(){
            return linkMap.entrySet();
        }

        @Override
        public synchronized void clear(){
            linkMap.clear();
        }

        @Override
        public synchronized boolean containsKey(Object key){
            return linkMap.containsKey(key);
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(linkMap.keySet());
        }

    }
}
