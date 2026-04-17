package com.orangehrm.utils;

import com.orangehrm.constants.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils — reads test data from .xlsx files using Apache POI.
 *
 * <p>Data format expected in Excel:
 * <ul>
 *   <li>Row 0 = Header row (column names)</li>
 *   <li>Row 1+ = Data rows</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   List&lt;Map&lt;String, String&gt;&gt; data =
 *       ExcelUtils.getSheetData(AppConstants.EXCEL_DATA_PATH, "LoginData");
 * </pre>
 */
public final class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    private ExcelUtils() {}

    /**
     * Reads all rows from the given sheet into a list of maps.
     * Each map represents one row: {columnHeader -> cellValue}.
     *
     * @param filePath  path to the .xlsx file
     * @param sheetName name of the worksheet
     * @return list of row data maps
     */
    public static List<Map<String, String>> getSheetData(String filePath, String sheetName) {
        List<Map<String, String>> sheetData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in: " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.warn("Sheet '{}' appears to be empty — no header row found", sheetName);
                return sheetData;
            }

            // Build column index → header name mapping
            int colCount = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int col = 0; col < colCount; col++) {
                headers.add(getCellValue(headerRow.getCell(col)));
            }

            // Read data rows
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row dataRow = sheet.getRow(rowIdx);
                if (dataRow == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int col = 0; col < colCount; col++) {
                    rowMap.put(headers.get(col), getCellValue(dataRow.getCell(col)));
                }
                sheetData.add(rowMap);
            }

            log.info("Read {} data rows from sheet '{}' in file: {}", sheetData.size(), sheetName, filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + filePath, e);
        }
        return sheetData;
    }

    /**
     * Convenience: get data as Object[][] for TestNG @DataProvider.
     *
     * @param filePath  .xlsx file path
     * @param sheetName sheet name
     * @return 2D Object array usable with @DataProvider
     */
    public static Object[][] getDataAsArray(String filePath, String sheetName) {
        List<Map<String, String>> rows = getSheetData(filePath, sheetName);
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }

    // ----------------------------------------------------------------
    // Private: cell value extractor (handles all cell types)
    // ----------------------------------------------------------------

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
