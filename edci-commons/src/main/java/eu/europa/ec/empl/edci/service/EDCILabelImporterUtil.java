package eu.europa.ec.empl.edci.service;

import com.google.gson.Gson;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class EDCILabelImporterUtil {

    private static final String SOURCE_LABELS_BACK_DIRECTORY = "src/main/resources/labels/back/";
    private static final String SOURCE_LABELS_FRONT_DIRECTORY = "src/main/resources/labels/front/";
    private static final String RESOURCE_BUNDLE_PATH = "src/main/resources-unfiltered";
    private static final String I18N_PATH = "src/main/angular/src/assets/i18n";
    private static final String NEWLINE_LABEL_CHAR = "\\n";

    private static final List<String> APPS_BACK = Arrays.asList("issuer");
    private static final List<String> APPS_FRONT = Arrays.asList("issuer");
    private static final List<String> LANGUAGES = Arrays.asList("bg", "cs", "da", "de", "el", "es", "et", "fi", "fr", "ga", "hr", "hu", "is", "it", "lt", "lv", "mk", "mt", "nl", "no", "pl", "pt", "ro", "sk", "sl", "sr", "sv", "tr");
//    private static final List<String> LANGUAGES = Arrays.asList("hr");
    private static final String SOURCE_FILES_PREFIX = "messages_";
    private static final String LABELS_FILES_PREFIX = "messages_";

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

    //GIT CONFIGS
    private static final String GIT_REFERENCE_LANGUAGE = "en";
    private static final String GIT_BASE_URL = "https://umane.everis.com/git/api/v4/projects/9014/repository/files/";

    private static final String GIT_RESOURCE_BUNDLE_PATH = "src/main/resources-unfiltered/";
    private static final String I18N_GIT_PATH = "src/main/angular/src/assets/i18n/";
    private static final String GIT_BACK_FILENAME = "messages_en.properties";
    private static final String GIT_FRONT_FILENAME = "en.json";

    private static String gitAccessToken;


    public static void main(String[] args) {

        String fromCommit;
        String toCommit;

        if (args.length < 1) {
            System.err.println("Execution examples with args:");
            System.err.println("mvn exec:java -Dexec.args=\"NewLabels FromCommit_HASH ToCommit_HASH gitAccessToken\"");
            System.err.println("mvn exec:java -P ImportLabels");
        } else {
            System.out.println("Starting ");
        }

        fromCommit = args[1];
        toCommit = args[2];
        gitAccessToken = args[3];

        System.out.println(">>>>>>>>>>>>>>>>> Parameters <<<<<<<<<<<<<<<<<<<<");
        System.out.println(">>>>>>>>>>>>>>>>> Action: " + args[0]);
        System.out.println(">>>>>>>>>>>>>>>>> From commit: " + args[1]);
        System.out.println(">>>>>>>>>>>>>>>>> To commit: " + args[2]);
        System.out.println(">>>>>>>>>>>>>>>>> Git Access token: " + args[3]);
        System.out.println(">>>>>>>>>>>>>>>>> <<<<<<<<<<<<<<<<<<<");

        switch (args[0]) {
            case "ImportLabels":
                for (String appName : APPS_BACK) {
                    EDCILabelImporterUtil.updateAndAppendBackendLabels(appName);
                }

                for (String appName : APPS_FRONT) {
                    EDCILabelImporterUtil.updateAndAppendFrontendLabels(appName);
                }
                break;
            case "NewLabels":
                EDCILabelImporterUtil.getAndPrintNewOrUpdatedLabels(fromCommit, toCommit);
                break;
        }
    }

    public static void getAndPrintNewOrUpdatedLabels(String fromCommit, String toCommit) {

        XSSFWorkbook workbook = new XSSFWorkbook();

        EDCILabelImporterUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "issuer", fromCommit, toCommit);
        EDCILabelImporterUtil.getAndPrintNewOrUpdatedFrontendLabels(workbook, "issuer", fromCommit, toCommit);

        EDCILabelImporterUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "viewer", fromCommit, toCommit);
        EDCILabelImporterUtil.getAndPrintNewOrUpdatedFrontendLabels(workbook, "viewer", fromCommit, toCommit);

        EDCILabelImporterUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "wallet", fromCommit, toCommit);

        File myFile = new File("target/Literals_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(myFile)) {
            workbook.write(outputStream);
            System.out.println(">>>>>>>>>>>>>>>>> Generated file: " + myFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println(String.format("ERROR GENERATING THE EXCEL FILE"));
        }
    }

    private static void getAndPrintNewOrUpdatedFrontendLabels(XSSFWorkbook workbook, String appName, String fromCommit, String toCommit) {

        String sourceFile = EDCILabelImporterUtil.downloadRawFileFromGit(GIT_BASE_URL, fromCommit, appName, I18N_GIT_PATH, GIT_FRONT_FILENAME);
        String sourceFileActual = EDCILabelImporterUtil.downloadRawFileFromGit(GIT_BASE_URL, toCommit, appName, I18N_GIT_PATH, GIT_FRONT_FILENAME);

        try {
            Map<String, String> oldMessages = new Gson().fromJson(sourceFile, new HashMap<String, String>().getClass());
            Map<String, String> currentMessages = new Gson().fromJson(sourceFileActual, new HashMap<String, String>().getClass());
            Map<String, String> newOrUpdatedLabels = EDCILabelImporterUtil.getUpdatedOrAddedLabels(oldMessages, currentMessages);

            addExcelSheet(workbook, StringUtils.capitalize(appName) + " front", newOrUpdatedLabels);

        } catch (Exception e) {
            System.out.println(String.format("ERROR PARSING PROPERTY FILE IN FRONTEND FOR APP %s", appName));
        }
    }

    private static void getAndPrintNewOrUpdatedBackendLabels(XSSFWorkbook workbook, String appName, String fromCommit, String toCommit) {

        String sourceFile = EDCILabelImporterUtil.downloadRawFileFromGit(GIT_BASE_URL, fromCommit, appName, GIT_RESOURCE_BUNDLE_PATH, GIT_BACK_FILENAME);
        String sourceFileActual = EDCILabelImporterUtil.downloadRawFileFromGit(GIT_BASE_URL, toCommit, appName, GIT_RESOURCE_BUNDLE_PATH, GIT_BACK_FILENAME);

        try {

            Properties oldMessages = PropertiesLoaderUtils.loadProperties(new ByteArrayResource(sourceFile.getBytes()));
            Properties currentMessages = PropertiesLoaderUtils.loadProperties(new ByteArrayResource(sourceFileActual.getBytes()));
            Map<String, String> newOrUpdatedLabels = EDCILabelImporterUtil.getUpdatedOrAddedLabels(oldMessages, currentMessages);

            addExcelSheet(workbook, StringUtils.capitalize(appName) + " back", newOrUpdatedLabels);

        } catch (IOException e) {
            System.out.println(String.format("ERROR PARSING PROPERTY FILE IN BACKEND FOR APP %s", appName));
        }

    }

    private static String downloadRawFileFromGit(String baseUrl, String commitSha, String app, String resourcePath, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(gitAccessToken);
        String resourceURL = "";
        resourceURL = String.format("edci-%s/edci-%s-web/", app, app).concat(resourcePath).concat(fileName);
        Map<String, String> map = new HashMap();
        map.put("file", resourceURL);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("{file}").path("raw").queryParam("ref", commitSha);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(String.format("Downloading file - %s", uriBuilder.buildAndExpand(map).encode().toUri()));
        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.buildAndExpand(map).encode().toUri(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        } else {
            throw new EDCIException(String.format("Error downloading file - %s", resourceURL));
        }
    }

    private static Map<String, String> getUpdatedOrAddedLabels(Properties oldLabels, Properties newLabels) {
        Map<String, String> results = new HashMap<>();
        newLabels.stringPropertyNames().stream().forEach(key -> {
            if (!oldLabels.containsKey(key)) {
                results.put(key, (String) newLabels.getProperty(key));
            } else {
                String oldLabel = oldLabels.getProperty(key);
                String newLabel = newLabels.getProperty(key);
                if (!newLabel.equals(oldLabel)) {
                    results.put(key, newLabel);
                }
            }
        });
        return results;
    }

    private static Map<String, String> getUpdatedOrAddedLabels(Map<String, String> oldLabels, Map<String, String> newLabels) {
        Map<String, String> results = new HashMap<>();
        newLabels.keySet().stream().forEach(key -> {
            if (!oldLabels.containsKey(key)) {
                results.put(key, (String) newLabels.get(key));
            } else {
                String oldLabel = oldLabels.get(key);
                String newLabel = newLabels.get(key);
                if (!newLabel.equals(oldLabel)) {
                    results.put(key, newLabel);
                }
            }
        });
        return results;
    }

    private static void addExcelSheet(XSSFWorkbook workbook, String sheetName, Map<String, String> results) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        XSSFRow row = null;

        CellStyle style = workbook.createCellStyle();
        // Setting Background color
        style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        row = sheet.createRow(0);

        XSSFCell keyCell = row.createCell(0);
        keyCell.setCellValue("key");
        keyCell.setCellStyle(style);

        XSSFCell valueCell = row.createCell(1);
        valueCell.setCellValue("value");
        valueCell.setCellStyle(style);

        int rows = 1;
        for (String key : results.keySet()) {
            row = sheet.createRow(rows++);
            if (row != null) {
                keyCell = row.createCell(0);
                keyCell.setCellValue(key);
                valueCell = row.createCell(1);
                valueCell.setCellValue(results.get(key));
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

    }

    private static void updateAndAppendFrontendLabels(String appName) {
        for (String language : LANGUAGES) {
            //FRONT LABELS
            System.out.println("##### Starting FRONT labels");
            File sourceFrontFile = EDCILabelImporterUtil.readFrontSourceFile(appName, language);
            File labelFrontFile = EDCILabelImporterUtil.readFrontLabelsFile(appName, language);
            boolean freshCreated = false;
            if (!labelFrontFile.exists()) {
                try {
                    //Create NEW JSON
                    freshCreated = true;
                    labelFrontFile.createNewFile();
                    EDCILabelImporterUtil.writeFile(labelFrontFile, "{\n}");
                } catch (IOException e) {
                    System.out.println("ERROR - COULD NOT CREATE NEW FILE " + labelFrontFile.getPath());
                }
            }
            //Parse files to key/value maps
            Map<String, String> frontSource = EDCILabelImporterUtil.parseFile(sourceFrontFile, SOURCE_FILE_DELIMITER, FRONT_COMMENTARY_MARKERS);
            Map<String, String> frontLabels = EDCILabelImporterUtil.parseFile(labelFrontFile, LABEL_FILE_DELIMITER_FRONT, FRONT_COMMENTARY_MARKERS);
            //Discriminate between toUpdate and toAdd labels
            Map[] splitFront = EDCILabelImporterUtil.doSplitBetweenUpdatedAndAdded(frontSource, frontLabels);
            Map<String, String> frontLabelsToUpdate = splitFront[0];
            Map<String, String> frontLabelsToAdd = splitFront[1];
            System.out.println(String.format("[%s - %s] Found %d FRONT labels (%d new, %d updates) in source to an already existing number of %d",
                    appName, language, frontSource.size(), frontLabelsToAdd.size(), frontLabelsToUpdate.size(), frontLabels.size()));
            //Create String for new updated File
            StringBuilder frontUpdatedStringBuilder = EDCILabelImporterUtil.createUpdatedStringBuilder(frontLabelsToUpdate, labelFrontFile, LABEL_FILE_DELIMITER_FRONT, FRONT_COMMENTARY_MARKERS, FRONT_ENDLINE_DELIMITER, FRONT_VALUE_WRAPPER, false);
            //delete last closing }
            if (frontLabelsToAdd.size() > 0) {
                frontUpdatedStringBuilder.deleteCharAt(frontUpdatedStringBuilder.lastIndexOf(FRONT_FILE_DELIMITER)).append(FRONT_ENDLINE_DELIMITER);
            }
            StringBuilder frontAppendedStringBuilder = EDCILabelImporterUtil.appendLabels(frontLabelsToAdd, frontUpdatedStringBuilder, LABEL_FILE_DELIMITER_FRONT, FRONT_COMMENTARY_MARKERS.get(0), FRONT_ENDLINE_DELIMITER, FRONT_VALUE_WRAPPER, false);
            //if fresh created, remove first comma
            if (freshCreated) {
                frontAppendedStringBuilder.deleteCharAt(frontAppendedStringBuilder.indexOf(","));
            }
            frontAppendedStringBuilder.append("\n".concat(FRONT_FILE_DELIMITER));
            try {
                //OverWrite file
                EDCILabelImporterUtil.writeFile(labelFrontFile, frontAppendedStringBuilder.toString());
                System.out.println(String.format("[%s - %s] added %d FRONT labels, updated %d FRONT labels", appName, language, frontLabelsToAdd.size(), frontLabelsToUpdate.size()));
            } catch (IOException e) {
                System.out.println(String.format("ERROR WRITING FRONT FILE %s", labelFrontFile.getPath()));
            }
        }
    }

    private static void updateAndAppendBackendLabels(String appName) {
        for (String language : LANGUAGES) {

            //BACK LABELS
            System.out.println(String.format("########## LANGUAGE %s - [%s] ##########", language, appName));
            System.out.println("##### Starting BACK labels");
            //Read source and Dest file
            File backSourceFile = EDCILabelImporterUtil.readBackSourceFile(appName, language);
            File backLabelFile = EDCILabelImporterUtil.readBackLabelsFile(appName, language);
            if (!backLabelFile.exists()) {
                try {
                    backLabelFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("ERROR - COULD NOT CREATE NEW FILE " + backLabelFile.getPath());
                }
            }
            //Parse files to key/value maps
            Map<String, String> backSource = EDCILabelImporterUtil.parseFile(backSourceFile, SOURCE_FILE_DELIMITER, BACK_COMMENTARY_MARKERS);
            Map<String, String> backLabels = EDCILabelImporterUtil.parseFile(backLabelFile, LABEL_FILE_DELIMITER_BACK, BACK_COMMENTARY_MARKERS);
            //Discriminate between toUpdate and toAdd labels
            Map[] splitBack = EDCILabelImporterUtil.doSplitBetweenUpdatedAndAdded(backSource, backLabels);
            Map<String, String> backLabelsToUpdate = splitBack[0];
            Map<String, String> backLabelsToAdd = splitBack[1];
            System.out.println(String.format("[%s - %s] Found %d BACK labels (%d new, %d updates) in source to an already existing number of %d",
                    appName, language, backSource.size(), backLabelsToAdd.size(), backLabelsToUpdate.size(), backLabels.size()));
            //Create String for new updated file
            StringBuilder backUpdatedStringBuilder = EDCILabelImporterUtil.createUpdatedStringBuilder(backLabelsToUpdate, backLabelFile, LABEL_FILE_DELIMITER_BACK, BACK_COMMENTARY_MARKERS, BACK_ENDLINE_DELIMITER, BACK_VALUE_WRAPPER, true);
            StringBuilder backAppendedStringBuilder = EDCILabelImporterUtil.appendLabels(backLabelsToAdd, backUpdatedStringBuilder, LABEL_FILE_DELIMITER_BACK, BACK_COMMENTARY_MARKERS.get(0), BACK_ENDLINE_DELIMITER, BACK_VALUE_WRAPPER, true);
            try {
                //OverWrite file
                EDCILabelImporterUtil.writeFile(backLabelFile, backAppendedStringBuilder.toString());
                System.out.println(String.format("[%s - %s] added %d BACK labels, updated %d BACK labels", appName, language, backLabelsToAdd.size(), backLabelsToUpdate.size()));
            } catch (IOException e) {
                System.out.println(String.format("ERROR WRITING FRONT FILE %s", backLabelFile.getPath()));
            }


        }
    }

    private static void writeFile(File file, String content) throws IOException {
        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            System.out.println(String.format("Writing file %s", file.getPath()));
            oStreamWriter.write(content.toString());
        } catch (IOException e) {
            System.out.println(String.format("ERROR WRITING FILE %s", file.getPath()));
            throw e;
        }
    }

    //Creates a String Builder from a file and a map of labels to update.
    private static StringBuilder createUpdatedStringBuilder(Map<String, String> labels, File labelsFile, String destDelimiter, List<String> commentaryMarkers, String lineDelimiter, String valueWrapper, boolean addComment) {
        StringBuilder updatedStringBuilder = new StringBuilder();
        try (Stream<String> lineStream = Files.lines(labelsFile.toPath())) {
            //Read Line by line, and update if needed
            lineStream.forEach(line -> {
                if (EDCILabelImporterUtil.startsWith(line, commentaryMarkers)) {
                    updatedStringBuilder.append(line.concat("\n"));
                } else {
                    String[] data = EDCILabelImporterUtil.splitLine(line, destDelimiter);
                    if (data != null && data.length == 2 && labels.containsKey(data[0])) {
                        String dateString = commentaryMarkers.get(0).concat("UPDATED AT " + new Date().toString());
                        String value = EDCILabelImporterUtil.unQuote(labels.get(data[0]));
                        String labelString = valueWrapper.concat(data[0]).concat(valueWrapper).concat(destDelimiter).concat(valueWrapper).concat(value).concat(valueWrapper).concat(lineDelimiter);
                        if (addComment) {
                            updatedStringBuilder.append(dateString.concat("\n").concat(labelString));
                        } else {
                            updatedStringBuilder.append(labelString);
                        }
                        updatedStringBuilder.append("\n");
                    } else {
                        updatedStringBuilder.append(line.concat("\n"));
                    }
                }
            });
        } catch (IOException e) {
            System.out.println(String.format("ERROR - COULD NOT READ %s lines", labelsFile.getPath()));
        }
        return updatedStringBuilder;
    }

    //Appends new labels to a StringBuilder from a map
    private static StringBuilder appendLabels(Map<String, String> labels, StringBuilder updatedStringBuilder, String destDelimiter, String commentaryMarker, String endLineDelimiter, String valueWrapper, boolean addComment) {
        StringBuilder appendedStringBuilder = new StringBuilder(updatedStringBuilder);
        if (addComment) {
            appendedStringBuilder.append(commentaryMarker.concat("ADDED AT " + new Date().toString().concat("\n")));
        }
        int index = 0;
        for (Map.Entry<String, String> label : labels.entrySet()) {
            index++;
            String value = EDCILabelImporterUtil.unQuote(label.getValue());
            appendedStringBuilder.append(valueWrapper.concat(label.getKey()).concat(valueWrapper)
                    .concat(destDelimiter)
                    .concat(valueWrapper).concat(value).concat(valueWrapper));
            if (index < labels.size()) {
                appendedStringBuilder.append(endLineDelimiter);
            }
            appendedStringBuilder.append("\n");
        }
        return appendedStringBuilder;
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

    //Creates path to source File(incoming updates - CSV back)
    private static File readBackSourceFile(String appName, String language) {
        Path filePath = Paths.get(
                SOURCE_LABELS_BACK_DIRECTORY.concat("/")
                        .concat(appName)
                        .concat("/")
                        .concat(SOURCE_FILES_PREFIX)
                        .concat(language)
                        .concat(".csv")
        );
        return EDCILabelImporterUtil.readFile(filePath);
    }

    //Creates path to source File(incoming updates - CSV front)
    private static File readFrontSourceFile(String appName, String language) {
        Path filePath = Paths.get(
                SOURCE_LABELS_FRONT_DIRECTORY.concat("/")
                        .concat(appName)
                        .concat("/")
                        .concat(SOURCE_FILES_PREFIX)
                        .concat(language)
                        .concat(".csv")
        );
        return EDCILabelImporterUtil.readFile(filePath);
    }

    //Creates path to labels File(the ones used by apps)
    private static File readBackLabelsFile(String appName, String language) {
        Path labelsPath = Paths.get(
                "../".concat("edci-")
                        .concat(appName)
                        .concat("/edci-")
                        .concat(appName)
                        .concat("-web/")
                        .concat(RESOURCE_BUNDLE_PATH)
                        .concat("/")
                        .concat(LABELS_FILES_PREFIX)
                        .concat(language)
                        .concat(".properties")
        );
        return EDCILabelImporterUtil.readFile(labelsPath);
    }

    private static File readFrontLabelsFile(String appName, String language) {
        Path labelsPath = Paths.get(
                "../".concat("edci-")
                        .concat(appName)
                        .concat("/edci-")
                        .concat(appName)
                        .concat("-web/")
                        .concat(I18N_PATH)
                        .concat("/")
                        .concat(language)
                        .concat(".json")
        );
        return EDCILabelImporterUtil.readFile(labelsPath);
    }

    private static File readFile(Path path) {
        File file = null;
        try {
            file = new File(path.toString());
            System.out.println("READED FILE - " + path.toString());
        } catch (Exception e) {
            System.out.println("ERROR READING FILE - " + path.toString());
        }
        return file;
    }

    //Parses file splitting lines on first delimiter found
    private static Map<String, String> parseFile(File labelsFile, String delimiter, List<String> commentaryMarkers) {
        Map<String, String> labels = new LinkedHashMap<String, String>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(labelsFile.getPath()), StandardCharsets.UTF_8)) {
            String line;
            String lastKey = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (!EDCILabelImporterUtil.startsWith(line, commentaryMarkers)) {
                    String[] data = EDCILabelImporterUtil.splitLine(line, delimiter);
                    if (data != null && data.length == 2) {
                        lastKey = data[0];
                        labels.put(data[0], data[1]);
                    } else if (data != null && data.length == 1) {
                        StringBuilder lastEntrySB = new StringBuilder(labels.get(lastKey));
                        lastEntrySB.append(NEWLINE_LABEL_CHAR).append(data[0]);
                        labels.put(lastKey, lastEntrySB.toString());
                    } else {
                        System.out.println(String.format("ERROR - STRANGE LINE AT %s, (%s)", labelsFile.getPath(), line));
                    }
                }
            }
            System.out.println("PARSED FILE - " + labelsFile.getPath());
        } catch (Exception e) {
            System.out.println("ERROR PARSING FILE - " + labelsFile.getPath() + " / " + e.getMessage());
            e.printStackTrace();
        }
        return labels;
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

    //Discriminate between updated and added labels, updated will be in [0] position and added in [1]
    private static Map[] doSplitBetweenUpdatedAndAdded(Map<String, String> source, Map<String, String> labels) {
        Map[] split = new LinkedHashMap[2];
        System.out.println(String.format("Comparing %d new labels to %d preexisting labels", source.size(), labels.size()));
        Map<String, String> updated = new LinkedHashMap<String, String>();
        Map<String, String> added = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            if (labels.containsKey(entry.getKey())) {
                updated.put(entry.getKey(), source.get(entry.getKey()));
            } else {
                added.put(entry.getKey(), source.get(entry.getKey()));
            }
        }
        split[0] = updated;
        split[1] = added;
        return split;
    }
}
