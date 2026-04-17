package com.orangehrm.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempResourceGenTest {

    @Test
    public void generateResources() {
        String filePath = "src/test/resources/testdata/LoginTestData.xlsx";
        System.out.println("Generating resources...");
        
        try {
            Path parent = Paths.get(filePath).getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                // Sheet 1: LoginData
                Sheet loginSheet = workbook.createSheet("LoginData");
                String[] loginHeaders = {"username", "password", "expectedResult", "testCase"};
                Object[][] loginData = {
                    {"Admin", "admin123", "pass", "TC_XL_001"},
                    {"Admin", "wrongPass", "fail", "TC_XL_002"},
                    {"invalidUser", "admin123", "fail", "TC_XL_003"},
                    {"", "", "fail", "TC_XL_004"}
                };
                createSheetWithData(loginSheet, loginHeaders, loginData);

                // Sheet 2: KeywordData
                Sheet keywordSheet = workbook.createSheet("KeywordData");
                String[] kwHeaders = {"testCase", "keyword", "locatorType", "locatorValue", "testData"};
                Object[][] kwData = {
                    {"TC_KD_001", "openUrl",    "",     "",                       "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"},
                    {"TC_KD_001", "enterText",  "name", "username",               "Admin"},
                    {"TC_KD_001", "enterText",  "name", "password",               "admin123"},
                    {"TC_KD_001", "click",      "css",  "button[type='submit']",  ""},
                    {"TC_KD_001", "assertUrl",  "",     "",                       "dashboard"},
                    {"TC_KD_001", "assertText", "css",  ".oxd-topbar-header-breadcrumb h6", "Dashboard"}
                };
                createSheetWithData(keywordSheet, kwHeaders, kwData);

                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }
            }
            System.out.println("LoginTestData.xlsx generated successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createSheetWithData(Sheet sheet, String[] headers, Object[][] data) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            
            CellStyle style = sheet.getWorkbook().createCellStyle();
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j].toString());
            }
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
