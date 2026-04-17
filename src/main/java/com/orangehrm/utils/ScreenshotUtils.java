package com.orangehrm.utils;

import com.orangehrm.constants.AppConstants;
import com.orangehrm.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils — captures and saves screenshots.
 *
 * <p>Screenshots are saved to {@code test-output/screenshots/} with a
 * timestamped filename for easy triage.
 *
 * <p>Usage (called by TestListener on failure):
 * <pre>
 *   String path = ScreenshotUtils.captureScreenshot("LoginTest_invalidCredentials");
 *   extentTest.addScreenCaptureFromPath(path);
 * </pre>
 */
public final class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and saves it to the screenshots directory.
     *
     * @param testName name of the test (used in filename)
     * @return absolute path to the saved screenshot file
     */
    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot capture screenshot — WebDriver is null");
            return "";
        }

        String timestamp  = LocalDateTime.now().format(FORMATTER);
        String fileName   = testName + "_" + timestamp + ".png";
        String destPath   = AppConstants.SCREENSHOT_DIR + fileName;

        try {
            File srcFile  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(destPath);
            destFile.getParentFile().mkdirs();                // ensure directory exists
            FileUtils.copyFile(srcFile, destFile);
            log.info("Screenshot saved: {}", destPath);
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to save screenshot for test: {}", testName, e);
            return "";
        }
    }

    /**
     * Returns a screenshot as a Base64-encoded string (for embedding in Extent Reports).
     *
     * @return Base64 string or empty string on failure
     */
    public static String captureScreenshotAsBase64() {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot capture screenshot — WebDriver is null");
            return "";
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture Base64 screenshot", e);
            return "";
        }
    }

    /**
     * Returns a screenshot as a byte array (for Allure attachments).
     *
     * @return byte array or empty array on failure
     */
    public static byte[] captureScreenshotAsBytes() {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot capture screenshot — WebDriver is null");
            return new byte[0];
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture Byte screenshot", e);
            return new byte[0];
        }
    }
}
