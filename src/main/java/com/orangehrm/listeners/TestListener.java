package com.orangehrm.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.orangehrm.reporting.ExtentManager;
import com.orangehrm.reporting.ExtentTestManager;
import com.orangehrm.utils.ScreenshotUtils;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

/**
 * TestListener — wires TestNG lifecycle events to Extent Reports and logging.
 *
 * <p>Register this listener in testng.xml:
 * <pre>
 *   &lt;listeners&gt;
 *     &lt;listener class-name="com.orangehrm.listeners.TestListener"/&gt;
 *   &lt;/listeners&gt;
 * </pre>
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>onStart       — log suite start</li>
 *   <li>onTestStart   — create Extent test node</li>
 *   <li>onTestSuccess — mark PASS</li>
 *   <li>onTestFailure — mark FAIL + attach screenshot</li>
 *   <li>onTestSkipped — mark SKIP</li>
 *   <li>onFinish      — flush Extent report to HTML</li>
 * </ol>
 */
public class TestListener implements ISuiteListener, ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    // ----------------------------------------------------------------
    // Suite Events
    // ----------------------------------------------------------------

    @Override
    public void onStart(ISuite suite) {
        log.info("========== SUITE STARTED: {} ==========", suite.getName());
        ExtentManager.getInstance(); // ensure report is initialized early
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("========== SUITE FINISHED: {} ==========", suite.getName());
        ExtentManager.flush();       // write HTML to disk
    }

    // ----------------------------------------------------------------
    // Test Events
    // ----------------------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getFullTestName(result);
        log.info(">>> TEST STARTED: {}", testName);

        ExtentTest test = ExtentManager.getInstance()
            .createTest(testName, result.getMethod().getDescription());
        ExtentTestManager.setTest(test);

        // Tag with groups / categories
        String[] groups = result.getMethod().getGroups();
        for (String group : groups) {
            ExtentTestManager.getTest().assignCategory(group);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getFullTestName(result);
        log.info(">>> TEST PASSED: {}", testName);
        ExtentTestManager.getTest().pass("Test passed successfully");
        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getFullTestName(result);
        log.error(">>> TEST FAILED: {} — {}", testName, result.getThrowable().getMessage());

        ExtentTest test = ExtentTestManager.getTest();
        test.fail(result.getThrowable());

        // Attach screenshot to report
        String base64Screenshot = ScreenshotUtils.captureScreenshotAsBase64();
        if (!base64Screenshot.isEmpty()) {
            try {
                test.fail("Screenshot at point of failure:",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                
                // --- Allure Attachment ---
                saveScreenshotToAllure();
                
            } catch (Exception e) {
                log.error("Failed to attach screenshot to reporting systems", e);
            }
        }

        // Also save to disk for archival
        ScreenshotUtils.captureScreenshot(testName);
        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getFullTestName(result);
        log.warn(">>> TEST SKIPPED: {}", testName);
        ExtentTestManager.getTest().skip(result.getThrowable());
        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("Test failed but within success percentage: {}", getFullTestName(result));
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    // ----------------------------------------------------------------
    // Allure Attachments
    // ----------------------------------------------------------------

    @Attachment(value = "Failure Screenshot", type = "image/png")
    private byte[] saveScreenshotToAllure() {
        return ScreenshotUtils.captureScreenshotAsBytes();
    }

    private String getFullTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
            + "." + result.getMethod().getMethodName();
    }
}
