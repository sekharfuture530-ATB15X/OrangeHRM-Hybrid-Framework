package com.orangehrm.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.constants.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentManager — singleton that owns the single {@link ExtentReports} instance.
 *
 * <p>Only one ExtentReports object should exist per test run. All test threads
 * share this instance; individual test entries are managed by {@link ExtentTestManager}.
 *
 * <p>Usage (called by TestListener):
 * <pre>
 *   ExtentReports extent = ExtentManager.getInstance();   // lazy init on first call
 *   ExtentManager.flush();                                 // called in @AfterSuite
 * </pre>
 */
public final class ExtentManager {

    private static final Logger log = LogManager.getLogger(ExtentManager.class);
    private static ExtentReports extentReports;

    private ExtentManager() {}

    /**
     * Returns the singleton ExtentReports instance, creating it on first call.
     */
    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            extentReports = createInstance();
        }
        return extentReports;
    }

    /**
     * Flushes all buffered report data to disk.
     * Must be called after all tests complete (@AfterSuite).
     */
    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("Extent Report flushed to disk.");
        }
    }

    // ----------------------------------------------------------------
    // Private factory
    // ----------------------------------------------------------------

    private static ExtentReports createInstance() {
        // Build a timestamped report filename so runs don't overwrite each other
        String timestamp  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportDir  = ConfigReader.getOrDefault("extent.report.dir", AppConstants.EXTENT_REPORT_DIR);
        String reportName = "OrangeHRM_Report_" + timestamp + ".html";
        String reportPath = reportDir + reportName;

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("OrangeHRM Test Automation Report");
        sparkReporter.config().setReportName("OrangeHRM Hybrid Framework — Execution Report");
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setTimelineEnabled(true);

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application",    ConfigReader.getOrDefault("app.name", "OrangeHRM"));
        extent.setSystemInfo("Environment",    ConfigReader.getEnvironment());
        extent.setSystemInfo("Browser",        ConfigReader.getBrowser());
        extent.setSystemInfo("OS",             System.getProperty("os.name"));
        extent.setSystemInfo("Java Version",   System.getProperty("java.version"));
        extent.setSystemInfo("Executed By",    System.getProperty("user.name"));

        log.info("ExtentReports initialized — report will be written to: {}", reportPath);
        return extent;
    }
}
