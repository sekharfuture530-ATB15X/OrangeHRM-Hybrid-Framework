package com.orangehrm.constants;

/**
 * Application-wide constants.
 * Centralizing magic strings/numbers here prevents typos and
 * makes global changes a single-file update.
 *
 * @author  QA Automation Framework
 * @version 1.0
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class — prevent instantiation
    }

    // ----------------------------------------------------------------
    // Page Titles
    // ----------------------------------------------------------------
    public static final String LOGIN_PAGE_TITLE       = "OrangeHRM";
    public static final String DASHBOARD_PAGE_TITLE   = "OrangeHRM";

    // ----------------------------------------------------------------
    // Page Header Text
    // ----------------------------------------------------------------
    public static final String DASHBOARD_HEADER       = "Dashboard";
    public static final String LOGIN_HEADER           = "Login";

    // ----------------------------------------------------------------
    // Error Messages (as displayed in UI)
    // ----------------------------------------------------------------
    public static final String INVALID_CREDENTIALS_MSG = "Invalid credentials";
    public static final String REQUIRED_FIELD_MSG      = "Required";

    // ----------------------------------------------------------------
    // Wait Timeouts (seconds)
    // ----------------------------------------------------------------
    public static final int IMPLICIT_WAIT    = 10;
    public static final int EXPLICIT_WAIT    = 20;
    public static final int PAGE_LOAD_WAIT   = 30;
    public static final int FLUENT_WAIT      = 15;
    public static final int POLLING_INTERVAL = 500;   // milliseconds

    // ----------------------------------------------------------------
    // File Paths (relative to project root)
    // ----------------------------------------------------------------
    public static final String CONFIG_FILE_PATH       = "src/main/resources/config/config.properties";
    public static final String EXCEL_DATA_PATH        = "src/test/resources/testdata/LoginTestData.xlsx";
    public static final String JSON_DATA_PATH         = "src/test/resources/testdata/login_data.json";
    public static final String SCREENSHOT_DIR         = "test-output/screenshots/";
    public static final String EXTENT_REPORT_DIR      = "test-output/ExtentReports/";

    // ----------------------------------------------------------------
    // Sheet Names (Excel)
    // ----------------------------------------------------------------
    public static final String LOGIN_SHEET            = "LoginData";
    public static final String USER_SHEET             = "UserData";

    // ----------------------------------------------------------------
    // JSON Keys
    // ----------------------------------------------------------------
    public static final String JSON_USERNAME          = "username";
    public static final String JSON_PASSWORD          = "password";
    public static final String JSON_EXPECTED_RESULT   = "expectedResult";

    // ----------------------------------------------------------------
    // Browser Options
    // ----------------------------------------------------------------
    public static final String CHROME_BROWSER         = "chrome";
    public static final String FIREFOX_BROWSER        = "firefox";
    public static final String EDGE_BROWSER           = "edge";

    // ----------------------------------------------------------------
    // Miscellaneous
    // ----------------------------------------------------------------
    public static final String PASS  = "PASS";
    public static final String FAIL  = "FAIL";
    public static final String SKIP  = "SKIP";
}
