package com.orangehrm.tests;

import com.orangehrm.base.BaseTest;
import com.orangehrm.constants.AppConstants;
import com.orangehrm.keywords.KeywordExecutor;
import com.orangehrm.utils.ExcelUtils;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * KeywordDrivenTest — demonstrates the Keyword-Driven layer of the Hybrid Framework.
 *
 * <p>This test reads action steps from an Excel "Keywords" sheet and executes
 * them via {@link KeywordExecutor}. Testers with no Java knowledge can drive
 * test scenarios purely by editing the spreadsheet.
 *
 * <p>Expected Excel sheet "KeywordData" structure:
 * <pre>
 *   | testCase   | keyword     | locatorType | locatorValue            | testData            |
 *   | TC_KD_001  | openUrl     |             |                         | https://...login    |
 *   | TC_KD_001  | enterText   | name        | username                | Admin               |
 *   | TC_KD_001  | enterText   | name        | password                | admin123            |
 *   | TC_KD_001  | click       | css         | button[type='submit']   |                     |
 *   | TC_KD_001  | assertUrl   |             |                         | dashboard           |
 * </pre>
 *
 * <p>If the Excel file is absent (CI), a built-in hardcoded scenario runs instead.
 */
public class KeywordDrivenTest extends BaseTest {

    private static final String KEYWORD_SHEET = "KeywordData";

    // ----------------------------------------------------------------
    // TC_KD_001: Keyword-driven login from Excel sheet
    // ----------------------------------------------------------------

    @Test(
        groups      = {"keyword", "regression"},
        description = "TC_KD_001 — Execute login steps from Excel keyword sheet",
        priority    = 10
    )
    public void testKeywordDrivenLogin() {
        log.info("TC_KD_001: Starting keyword-driven login test");

        try {
            // Attempt to load steps from Excel
            List<Map<String, String>> steps =
                ExcelUtils.getSheetData(AppConstants.EXCEL_DATA_PATH, KEYWORD_SHEET);

            for (Map<String, String> step : steps) {
                String testCase    = step.getOrDefault("testCase", "");
                String keyword     = step.getOrDefault("keyword", "");
                String locType     = step.getOrDefault("locatorType", "");
                String locValue    = step.getOrDefault("locatorValue", "");
                String data        = step.getOrDefault("testData", "");

                if ("TC_KD_001".equals(testCase) && !keyword.isBlank()) {
                    KeywordExecutor.execute(keyword, locType, locValue, data);
                }
            }

        } catch (Exception e) {
            // Fallback: run hardcoded keyword sequence (useful when Excel is absent in CI)
            log.warn("Excel sheet not found — running hardcoded keyword scenario. Reason: {}", e.getMessage());
            runHardcodedKeywordScenario();
        }
    }

    // ----------------------------------------------------------------
    // TC_KD_002: Keyword-driven invalid login
    // ----------------------------------------------------------------

    @Test(
        groups      = {"keyword", "regression"},
        description = "TC_KD_002 — Keyword-driven invalid login verification",
        priority    = 11
    )
    public void testKeywordDrivenInvalidLogin() {
        log.info("TC_KD_002: Starting keyword-driven invalid login test");
        runInvalidLoginKeywordScenario();
    }

    // ----------------------------------------------------------------
    // Private: hardcoded keyword sequences (CI-safe fallback)
    // ----------------------------------------------------------------

    private void runHardcodedKeywordScenario() {
        String appUrl = com.orangehrm.config.ConfigReader.getAppUrl();

        KeywordExecutor.execute("openUrl",    "",     "",                       appUrl);
        KeywordExecutor.execute("enterText",  "name", "username",               "Admin");
        KeywordExecutor.execute("enterText",  "name", "password",               "admin123");
        KeywordExecutor.execute("click",      "css",  "button[type='submit']",  "");
        KeywordExecutor.execute("assertUrl",  "",     "",                       "dashboard");
        KeywordExecutor.execute("assertText", "css",  ".oxd-topbar-header-breadcrumb h6", "Dashboard");

        log.info("TC_KD_001: Hardcoded keyword scenario PASSED");
    }

    private void runInvalidLoginKeywordScenario() {
        String appUrl = com.orangehrm.config.ConfigReader.getAppUrl();

        KeywordExecutor.execute("openUrl",    "",     "",                       appUrl);
        KeywordExecutor.execute("enterText",  "name", "username",               "badUser");
        KeywordExecutor.execute("enterText",  "name", "password",               "badPass");
        KeywordExecutor.execute("click",      "css",  "button[type='submit']",  "");
        KeywordExecutor.execute("assertText", "css",  ".oxd-alert-content-text", "Invalid credentials");

        log.info("TC_KD_002: Invalid login keyword scenario PASSED");
    }
}
