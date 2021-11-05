package eu.europa.ec.empl.edci.service;

import com.google.gson.Gson;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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

public class EDCILabelExportUtil {

    //GIT CONFIGS
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
            System.err.println("mvn exec:java -Dexec.args=\"FromCommit_HASH ToCommit_HASH gitAccessToken\"");
        } else {
            System.out.println("Starting ");
        }

        fromCommit = args[0];
        toCommit = args[1];
        gitAccessToken = args[2];

        System.out.println(">>>>>>>>>>>>>>>>> Parameters <<<<<<<<<<<<<<<<<<<<");
        System.out.println(">>>>>>>>>>>>>>>>> From commit: " + args[0]);
        System.out.println(">>>>>>>>>>>>>>>>> To commit: " + args[1]);
        System.out.println(">>>>>>>>>>>>>>>>> Git Access token: " + args[2]);
        System.out.println(">>>>>>>>>>>>>>>>> <<<<<<<<<<<<<<<<<<<");

        EDCILabelExportUtil.getAndPrintNewOrUpdatedLabels(fromCommit, toCommit);
    }

    public static void getAndPrintNewOrUpdatedLabels(String fromCommit, String toCommit) {

        XSSFWorkbook workbook = new XSSFWorkbook();

        EDCILabelExportUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "issuer", fromCommit, toCommit);
        EDCILabelExportUtil.getAndPrintNewOrUpdatedFrontendLabels(workbook, "issuer", fromCommit, toCommit);

        EDCILabelExportUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "viewer", fromCommit, toCommit);
        EDCILabelExportUtil.getAndPrintNewOrUpdatedFrontendLabels(workbook, "viewer", fromCommit, toCommit);

        EDCILabelExportUtil.getAndPrintNewOrUpdatedBackendLabels(workbook, "wallet", fromCommit, toCommit);

        File myFile = new File("Literals_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(myFile)) {
            workbook.write(outputStream);
            System.out.println(">>>>>>>>>>>>>>>>> Generated file: " + myFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println(String.format("ERROR GENERATING THE EXCEL FILE"));
        }
    }

    private static void getAndPrintNewOrUpdatedFrontendLabels(XSSFWorkbook workbook, String appName, String fromCommit, String toCommit) {

        String sourceFile = EDCILabelExportUtil.downloadRawFileFromGit(GIT_BASE_URL, fromCommit, appName, I18N_GIT_PATH, GIT_FRONT_FILENAME);
        String sourceFileActual = EDCILabelExportUtil.downloadRawFileFromGit(GIT_BASE_URL, toCommit, appName, I18N_GIT_PATH, GIT_FRONT_FILENAME);

        try {
            Map<String, String> oldMessages = new Gson().fromJson(sourceFile, new HashMap<String, String>().getClass());
            Map<String, String> currentMessages = new Gson().fromJson(sourceFileActual, new HashMap<String, String>().getClass());
            Map<String, String> newOrUpdatedLabels = EDCILabelExportUtil.getUpdatedOrAddedLabels(oldMessages, currentMessages);

            addExcelSheet(workbook, StringUtils.capitalize(appName) + " front", newOrUpdatedLabels);

        } catch (Exception e) {
            System.out.println(String.format("ERROR PARSING PROPERTY FILE IN FRONTEND FOR APP %s", appName));
        }
    }

    private static void getAndPrintNewOrUpdatedBackendLabels(XSSFWorkbook workbook, String appName, String fromCommit, String toCommit) {

        String sourceFile = EDCILabelExportUtil.downloadRawFileFromGit(GIT_BASE_URL, fromCommit, appName, GIT_RESOURCE_BUNDLE_PATH, GIT_BACK_FILENAME);
        String sourceFileActual = EDCILabelExportUtil.downloadRawFileFromGit(GIT_BASE_URL, toCommit, appName, GIT_RESOURCE_BUNDLE_PATH, GIT_BACK_FILENAME);

        try {

            Properties oldMessages = PropertiesLoaderUtils.loadProperties(new ByteArrayResource(sourceFile.getBytes()));
            Properties currentMessages = PropertiesLoaderUtils.loadProperties(new ByteArrayResource(sourceFileActual.getBytes()));
            Map<String, String> newOrUpdatedLabels = EDCILabelExportUtil.getUpdatedOrAddedLabels(oldMessages, currentMessages);

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

    private static boolean startsWith(String string, List<String> matches) {
        return matches.stream().anyMatch(match -> string.startsWith(match));
    }

}
