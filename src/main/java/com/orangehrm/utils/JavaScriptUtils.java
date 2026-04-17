package com.orangehrm.utils;

import com.orangehrm.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

/**
 * JavaScriptUtils — helper for JavaScript-based browser interactions.
 *
 * <p>Use these only when standard Selenium actions fail due to:
 * <ul>
 *   <li>Element click interception</li>
 *   <li>Element outside viewport</li>
 *   <li>Need to read/write JS variables or localStorage</li>
 * </ul>
 */
public final class JavaScriptUtils {

    private static final Logger log = LogManager.getLogger(JavaScriptUtils.class);

    private JavaScriptUtils() {}

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) DriverManager.getDriver();
    }

    /** Click via JS — bypasses overlay interception. */
    public static void click(WebElement element) {
        js().executeScript("arguments[0].click();", element);
        log.debug("JS click performed on element");
    }

    /** Scroll element to the centre of the viewport. */
    public static void scrollToElement(WebElement element) {
        js().executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
    }

    /** Scroll to the top of the page. */
    public static void scrollToTop() {
        js().executeScript("window.scrollTo(0, 0);");
    }

    /** Scroll to the bottom of the page. */
    public static void scrollToBottom() {
        js().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Set value of an input field using JS (bypasses restrictions on some inputs).
     *
     * @param element target input
     * @param value   value to set
     */
    public static void setValue(WebElement element, String value) {
        js().executeScript("arguments[0].value=arguments[1];", element, value);
        log.debug("JS setValue: '{}'", value);
    }

    /** Highlight an element with a red border (useful for debugging / demos). */
    public static void highlight(WebElement element) {
        js().executeScript(
            "arguments[0].style.border='3px solid red';", element);
    }

    /** Returns the current URL via JS (sometimes more reliable than driver.getCurrentUrl()). */
    public static String getCurrentUrl() {
        return (String) js().executeScript("return window.location.href;");
    }

    /**
     * Wait for the JavaScript document.readyState to be "complete".
     * Useful after AJAX-heavy page transitions.
     */
    public static boolean isPageLoaded() {
        return "complete".equals(js().executeScript("return document.readyState;"));
    }

    /**
     * Read a value from browser localStorage.
     *
     * @param key localStorage key
     * @return value string, or null if not set
     */
    public static String getLocalStorageItem(String key) {
        return (String) js().executeScript(
            "return window.localStorage.getItem(arguments[0]);", key);
    }

    /** Remove an item from localStorage. */
    public static void removeLocalStorageItem(String key) {
        js().executeScript("window.localStorage.removeItem(arguments[0]);", key);
    }
}
