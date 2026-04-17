package com.orangehrm.utils;

import com.orangehrm.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.function.Function;

/**
 * WaitUtils — centralised explicit / fluent wait helper.
 *
 * <p>Why not use {@code Thread.sleep}?
 * Hard sleeps waste time and make tests fragile.
 * WebDriverWait polls efficiently and respects dynamic page state.
 *
 * <p>Usage:
 * <pre>
 *   WaitUtils.waitForVisibility(usernameField, 20);
 *   WaitUtils.waitForClickability(loginButton, 15);
 *   WaitUtils.waitForUrl("dashboard", 10);
 * </pre>
 */
public final class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);

    private WaitUtils() {}

    // ----------------------------------------------------------------
    // WebDriverWait builder
    // ----------------------------------------------------------------

    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    // ----------------------------------------------------------------
    // Element-level waits
    // ----------------------------------------------------------------

    /**
     * Wait until element is visible on screen.
     *
     * @param element        target WebElement
     * @param timeoutSeconds how long to wait before failing
     * @return the element once visible
     */
    public static WebElement waitForVisibility(WebElement element, int timeoutSeconds) {
        log.debug("Waiting {}s for element to be visible: {}", timeoutSeconds, element);
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait until element is visible AND enabled (safe to click).
     */
    public static WebElement waitForClickability(WebElement element, int timeoutSeconds) {
        log.debug("Waiting {}s for element to be clickable", timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait until element is invisible / removed from view.
     */
    public static boolean waitForInvisibility(WebElement element, int timeoutSeconds) {
        log.debug("Waiting {}s for element to be invisible", timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOf(element));
    }

    // ----------------------------------------------------------------
    // Page-level waits
    // ----------------------------------------------------------------

    /**
     * Wait until the page title contains the given substring.
     */
    public static boolean waitForTitleContains(String titleFragment, int timeoutSeconds) {
        log.debug("Waiting {}s for title to contain: '{}'", timeoutSeconds, titleFragment);
        return getWait(timeoutSeconds).until(ExpectedConditions.titleContains(titleFragment));
    }

    /**
     * Wait until the current URL contains the given substring.
     */
    public static boolean waitForUrlContains(String urlFragment, int timeoutSeconds) {
        log.debug("Waiting {}s for URL to contain: '{}'", timeoutSeconds, urlFragment);
        return getWait(timeoutSeconds).until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Wait until the JavaScript document.readyState is "complete".
     */
    public static void waitForPageLoad(int timeoutSeconds) {
        log.debug("Waiting {}s for full page load", timeoutSeconds);
        getWait(timeoutSeconds).until(driver -> {
            String state = (String)
                ((JavascriptExecutor) driver).executeScript("return document.readyState");
            return "complete".equals(state);
        });
    }

    // ----------------------------------------------------------------
    // Fluent Wait (custom polling + ignoring exceptions)
    // ----------------------------------------------------------------

    /**
     * Creates a FluentWait for advanced scenarios where you want
     * fine-grained control over polling interval and ignored exceptions.
     *
     * @param timeoutSeconds  total wait time
     * @param pollingMillis   how frequently to poll (ms)
     * @param condition       custom condition lambda
     * @param <T>             return type of the condition
     * @return result of the condition
     */
    public static <T> T fluentWait(int timeoutSeconds,
                                   int pollingMillis,
                                   Function<WebDriver, T> condition) {
        log.debug("FluentWait — timeout: {}s, polling: {}ms", timeoutSeconds, pollingMillis);
        Wait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
            .withTimeout(Duration.ofSeconds(timeoutSeconds))
            .pollingEvery(Duration.ofMillis(pollingMillis))
            .ignoring(NoSuchElementException.class)
            .withMessage("FluentWait condition was not met within " + timeoutSeconds + "s");

        return wait.until(condition);
    }

    // ----------------------------------------------------------------
    // Convenience: sleep (use sparingly, only when truly necessary)
    // ----------------------------------------------------------------

    /**
     * Hard sleep wrapper — prefer explicit waits wherever possible.
     *
     * @param millis sleep duration in milliseconds
     */
    public static void hardSleep(long millis) {
        log.warn("Hard sleep invoked for {}ms — consider replacing with an explicit wait", millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
