package com.orangehrm.keywords;

import com.orangehrm.driver.DriverManager;
import com.orangehrm.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

/**
 * KeywordExecutor — the Keyword-Driven layer of the Hybrid Framework.
 *
 * <p>Keywords are English-like action words that map to Selenium operations.
 * They are driven from an Excel "keyword sheet" where testers write:
 * <pre>
 *   | Keyword    | Locator Type | Locator Value       | Test Data |
 *   | openUrl    |              |                     | https://.. |
 *   | enterText  | name         | username            | Admin      |
 *   | click      | css          | button[type=submit] |            |
 *   | assertText | css          | h6.header           | Dashboard  |
 * </pre>
 *
 * <p>This class is the engine that executes those keywords.
 */
public final class KeywordExecutor {

    private static final Logger log = LogManager.getLogger(KeywordExecutor.class);

    private KeywordExecutor() {}

    /**
     * Dispatch a keyword to its corresponding Selenium action.
     *
     * @param keyword      action keyword (case-insensitive)
     * @param locatorType  type: id | name | css | xpath | linkText
     * @param locatorValue locator string (empty for page-level keywords)
     * @param testData     data to use in the action (e.g., text to type)
     */
    public static void execute(String keyword, String locatorType, String locatorValue, String testData) {
        log.info("Executing keyword: [{}] | Locator: [{}={}] | Data: [{}]",
                keyword, locatorType, locatorValue, testData);

        String kw = keyword.trim().toLowerCase();
        switch (kw) {
            case "openurl":      openUrl(testData); break;
            case "entertext":    enterText(locatorType, locatorValue, testData); break;
            case "click":        click(locatorType, locatorValue); break;
            case "clear":        clearField(locatorType, locatorValue); break;
            case "selectbytext": selectByText(locatorType, locatorValue, testData); break;
            case "asserttitle":  assertTitle(testData); break;
            case "asserttext":   assertText(locatorType, locatorValue, testData); break;
            case "asserturl":    assertUrl(testData); break;
            case "waitvisible":  waitVisible(locatorType, locatorValue, Integer.parseInt(testData)); break;
            case "scroll":       scrollToElement(locatorType, locatorValue); break;
            case "refresh":      refreshPage(); break;
            case "navigateback": navigateBack(); break;
            default:
                throw new IllegalArgumentException("Unknown keyword: '" + keyword + "'");
        }
    }

    // ----------------------------------------------------------------
    // Keyword implementations
    // ----------------------------------------------------------------

    private static void openUrl(String url) {
        DriverManager.getDriver().get(url);
        log.info("Opened URL: {}", url);
    }

    private static void enterText(String locType, String locValue, String text) {
        WebElement el = findElement(locType, locValue);
        WaitUtils.waitForVisibility(el, 15);
        el.clear();
        el.sendKeys(text);
        log.info("Entered '{}' into [{}={}]", text, locType, locValue);
    }

    private static void click(String locType, String locValue) {
        WebElement el = findElement(locType, locValue);
        WaitUtils.waitForClickability(el, 15);
        el.click();
        log.info("Clicked [{}={}]", locType, locValue);
    }

    private static void clearField(String locType, String locValue) {
        findElement(locType, locValue).clear();
    }

    private static void selectByText(String locType, String locValue, String text) {
        new Select(findElement(locType, locValue)).selectByVisibleText(text);
    }

    private static void assertTitle(String expectedTitle) {
        String actualTitle = DriverManager.getDriver().getTitle();
        if (!actualTitle.contains(expectedTitle)) {
            throw new AssertionError(
                "Title mismatch. Expected to contain: '" + expectedTitle + "' | Actual: '" + actualTitle + "'");
        }
        log.info("Title assertion passed: contains '{}'", expectedTitle);
    }

    private static void assertText(String locType, String locValue, String expected) {
        WebElement el = findElement(locType, locValue);
        WaitUtils.waitForVisibility(el, 15);
        String actual = el.getText();
        if (!actual.contains(expected)) {
            throw new AssertionError(
                "Text mismatch. Expected: '" + expected + "' | Actual: '" + actual + "'");
        }
        log.info("Text assertion passed: element contains '{}'", expected);
    }

    private static void assertUrl(String expectedUrlFragment) {
        String actualUrl = DriverManager.getDriver().getCurrentUrl();
        if (!actualUrl.contains(expectedUrlFragment)) {
            throw new AssertionError(
                "URL mismatch. Expected to contain: '" + expectedUrlFragment + "' | Actual: '" + actualUrl + "'");
        }
        log.info("URL assertion passed: contains '{}'", expectedUrlFragment);
    }

    private static void waitVisible(String locType, String locValue, int seconds) {
        WaitUtils.waitForVisibility(findElement(locType, locValue), seconds);
    }

    private static void scrollToElement(String locType, String locValue) {
        WebElement el = findElement(locType, locValue);
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].scrollIntoView(true);", el);
    }

    private static void refreshPage() {
        DriverManager.getDriver().navigate().refresh();
    }

    private static void navigateBack() {
        DriverManager.getDriver().navigate().back();
    }

    // ----------------------------------------------------------------
    // Locator resolver
    // ----------------------------------------------------------------

    private static WebElement findElement(String locatorType, String locatorValue) {
        WebDriver driver = DriverManager.getDriver();
        By by;
        String locType = locatorType.trim().toLowerCase();
        switch (locType) {
            case "id":        by = By.id(locatorValue); break;
            case "name":      by = By.name(locatorValue); break;
            case "css":       by = By.cssSelector(locatorValue); break;
            case "xpath":     by = By.xpath(locatorValue); break;
            case "linktext":  by = By.linkText(locatorValue); break;
            case "classname": by = By.className(locatorValue); break;
            case "tagname":   by = By.tagName(locatorValue); break;
            default:
                throw new IllegalArgumentException("Unsupported locator type: '" + locatorType + "'");
        }
        return driver.findElement(by);
    }
}
