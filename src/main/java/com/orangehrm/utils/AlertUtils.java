package com.orangehrm.utils;

import com.orangehrm.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AlertUtils — handles JavaScript browser alerts, confirms, and prompt pop-ups.
 *
 * <p>Usage:
 * <pre>
 *   AlertUtils.acceptAlert();
 *   AlertUtils.dismissAlert();
 *   String msg = AlertUtils.getAlertText();
 *   AlertUtils.sendTextToPrompt("some input");
 * </pre>
 */
public final class AlertUtils {

    private static final Logger log = LogManager.getLogger(AlertUtils.class);

    private AlertUtils() {}

    /**
     * Wait for an alert to appear, then accept (click OK).
     *
     * @param timeoutSeconds how long to wait for the alert
     */
    public static void acceptAlert(int timeoutSeconds) {
        Alert alert = waitForAlert(timeoutSeconds);
        String alertText = alert.getText();
        alert.accept();
        log.info("Alert accepted. Text was: '{}'", alertText);
    }

    /** Accept alert with default 10-second timeout. */
    public static void acceptAlert() {
        acceptAlert(10);
    }

    /**
     * Wait for an alert to appear, then dismiss (click Cancel).
     *
     * @param timeoutSeconds how long to wait for the alert
     */
    public static void dismissAlert(int timeoutSeconds) {
        Alert alert = waitForAlert(timeoutSeconds);
        String alertText = alert.getText();
        alert.dismiss();
        log.info("Alert dismissed. Text was: '{}'", alertText);
    }

    /** Dismiss alert with default 10-second timeout. */
    public static void dismissAlert() {
        dismissAlert(10);
    }

    /**
     * Get the text displayed in the current alert.
     *
     * @return alert text string
     */
    public static String getAlertText() {
        Alert alert = waitForAlert(10);
        String text = alert.getText();
        log.info("Alert text: '{}'", text);
        return text;
    }

    /**
     * Send text to a JavaScript prompt dialog, then accept.
     *
     * @param text text to enter into the prompt
     */
    public static void sendTextToPrompt(String text) {
        Alert alert = waitForAlert(10);
        alert.sendKeys(text);
        alert.accept();
        log.info("Sent '{}' to prompt and accepted", text);
    }

    /**
     * Returns true if an alert is currently present, false otherwise.
     * Non-blocking — does not throw.
     */
    public static boolean isAlertPresent() {
        try {
            DriverManager.getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private static Alert waitForAlert(int timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
            .until(ExpectedConditions.alertIsPresent());
    }
}
