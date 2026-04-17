package com.orangehrm.utils;

import com.orangehrm.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ElementUtils — extra element-level helpers not covered in BasePage.
 *
 * <p>Use these in Page Object classes when you need operations beyond
 * the standard click / sendKeys / getText provided by BasePage.
 *
 * <p>Usage:
 * <pre>
 *   boolean visible  = ElementUtils.isElementPresent(By.id("username"));
 *   int     count    = ElementUtils.getElementCount(By.cssSelector(".menu-item"));
 *   String  cssValue = ElementUtils.getCssValue(element, "color");
 * </pre>
 */
public final class ElementUtils {

    private static final Logger log = LogManager.getLogger(ElementUtils.class);

    private ElementUtils() {}

    /**
     * Check if any element matching the given locator is present in the DOM.
     * Does NOT require the element to be visible.
     *
     * @param by locator strategy
     * @return true if at least one matching element exists
     */
    public static boolean isElementPresent(By by) {
        List<WebElement> elements = DriverManager.getDriver().findElements(by);
        boolean present = !elements.isEmpty();
        log.debug("isElementPresent [{}]: {}", by, present);
        return present;
    }

    /**
     * Count the number of elements matching the given locator.
     *
     * @param by locator strategy
     * @return element count (0 if none found)
     */
    public static int getElementCount(By by) {
        int count = DriverManager.getDriver().findElements(by).size();
        log.debug("Element count for [{}]: {}", by, count);
        return count;
    }

    /**
     * Get the computed CSS value of a property for a WebElement.
     * (e.g., "color", "background-color", "font-size")
     *
     * @param element  target element
     * @param property CSS property name
     * @return computed value as string
     */
    public static String getCssValue(WebElement element, String property) {
        String value = element.getCssValue(property);
        log.debug("CSS '{}' = '{}'", property, value);
        return value;
    }

    /**
     * Wait until a specific element count is reached (useful for dynamic lists).
     *
     * @param by             locator for the elements
     * @param expectedCount  number of elements to wait for
     * @param timeoutSeconds max wait time
     * @return true if count matched within timeout
     */
    public static boolean waitForElementCount(By by, int expectedCount, int timeoutSeconds) {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(driver -> driver.findElements(by).size() == expectedCount);
            log.info("Element count '{}' reached for [{}]", expectedCount, by);
            return true;
        } catch (TimeoutException e) {
            log.warn("Element count '{}' not reached within {}s for [{}]",
                expectedCount, timeoutSeconds, by);
            return false;
        }
    }

    /**
     * Returns the text content of all elements matching a locator.
     *
     * @param by locator strategy
     * @return list of text strings (trimmed, empty if none found)
     */
    public static List<String> getAllTexts(By by) {
        return DriverManager.getDriver().findElements(by)
            .stream()
            .map(WebElement::getText)
            .map(String::trim)
            .collect(Collectors.toList());
    }

    /**
     * Switch to an iframe by index and return self for chaining.
     *
     * @param index zero-based iframe index
     */
    public static void switchToFrame(int index) {
        DriverManager.getDriver().switchTo().frame(index);
        log.info("Switched to iframe index: {}", index);
    }

    /**
     * Switch to an iframe by WebElement reference.
     *
     * @param frame the iframe WebElement
     */
    public static void switchToFrame(WebElement frame) {
        DriverManager.getDriver().switchTo().frame(frame);
        log.info("Switched to iframe element");
    }

    /**
     * Switch back to the default content (out of all iframes).
     */
    public static void switchToDefault() {
        DriverManager.getDriver().switchTo().defaultContent();
        log.info("Switched back to default content");
    }

    /**
     * Switch to the most recently opened browser window/tab.
     */
    public static void switchToNewWindow() {
        String newHandle = DriverManager.getDriver().getWindowHandles()
            .stream().reduce((first, second) -> second).orElseThrow();
        DriverManager.getDriver().switchTo().window(newHandle);
        log.info("Switched to new window: {}", newHandle);
    }

    /**
     * Wait for an attribute of an element to contain a specific value.
     *
     * @param element        target element
     * @param attribute      HTML attribute name
     * @param expectedValue  value to wait for
     * @param timeoutSeconds max wait time
     */
    public static void waitForAttributeContains(WebElement element,
                                                String attribute,
                                                String expectedValue,
                                                int timeoutSeconds) {
        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
            .until(ExpectedConditions.attributeContains(element, attribute, expectedValue));
        log.info("Attribute '{}' contains '{}' confirmed", attribute, expectedValue);
    }
}
