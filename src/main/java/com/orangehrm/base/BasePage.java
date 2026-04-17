package com.orangehrm.base;

import com.orangehrm.driver.DriverManager;
import com.orangehrm.enums.WaitStrategy;
import com.orangehrm.reporting.ExtentTestManager;
import com.orangehrm.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * BasePage — parent for all Page Object classes.
 *
 * <p>Encapsulates every Selenium interaction behind a thin, named API.
 * Page Objects call these methods rather than the raw WebDriver API,
 * which means:
 * <ul>
 *   <li>Wait logic is centralised — change in one place</li>
 *   <li>Extent logging happens automatically on every action</li>
 *   <li>No raw Selenium leaking into test classes</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   public class LoginPage extends BasePage {
 *       public LoginPage() { super(); }
 *
 *       public LoginPage enterUsername(String username) {
 *           sendKeys(usernameInput, username, WaitStrategy.VISIBLE, "Username");
 *           return this;
 *       }
 *   }
 * </pre>
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(this.getClass());
    protected final WebDriver driver;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver, this);  // initialise @FindBy annotated elements
    }

    // ----------------------------------------------------------------
    // Core interaction methods
    // ----------------------------------------------------------------

    /**
     * Click an element after applying the specified wait strategy.
     *
     * @param element      target WebElement
     * @param strategy     wait strategy before clicking
     * @param elementName  human-readable name (for logs and reports)
     */
    protected void click(WebElement element, WaitStrategy strategy, String elementName) {
        applyWait(element, strategy);
        element.click();
        log.info("Clicked on: [{}]", elementName);
        logToExtent("Clicked on: " + elementName);
    }

    /**
     * Clear a field and type text into it.
     *
     * @param element      target WebElement
     * @param text         text to type
     * @param strategy     wait strategy before interaction
     * @param elementName  element description for logging
     */
    protected void sendKeys(WebElement element, String text, WaitStrategy strategy, String elementName) {
        applyWait(element, strategy);
        element.clear();
        element.sendKeys(text);
        log.info("Entered '{}' into: [{}]", text, elementName);
        logToExtent("Entered '" + text + "' into: " + elementName);
    }

    /**
     * Retrieve visible text of an element.
     *
     * @param element      target WebElement
     * @param strategy     wait strategy
     * @param elementName  element description
     * @return visible text
     */
    protected String getText(WebElement element, WaitStrategy strategy, String elementName) {
        applyWait(element, strategy);
        String text = element.getText();
        log.info("Got text '{}' from: [{}]", text, elementName);
        return text;
    }

    /**
     * Returns true if the element is displayed, false otherwise (no exception thrown).
     */
    protected boolean isDisplayed(WebElement element, String elementName) {
        try {
            boolean displayed = element.isDisplayed();
            log.info("[{}] displayed: {}", elementName, displayed);
            return displayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            log.warn("[{}] not found / stale — treating as not displayed", elementName);
            return false;
        }
    }

    /**
     * Select a dropdown option by visible text.
     */
    protected void selectByVisibleText(WebElement element, String text, String elementName) {
        applyWait(element, WaitStrategy.VISIBLE);
        new Select(element).selectByVisibleText(text);
        log.info("Selected '{}' from dropdown: [{}]", text, elementName);
        logToExtent("Selected '" + text + "' from: " + elementName);
    }

    /**
     * Retrieve value of an HTML attribute.
     */
    protected String getAttribute(WebElement element, String attribute, String elementName) {
        applyWait(element, WaitStrategy.VISIBLE);
        String value = element.getAttribute(attribute);
        log.debug("Attribute '{}' of [{}] = '{}'", attribute, elementName, value);
        return value;
    }

    /**
     * Hover over an element using Actions.
     */
    protected void hoverOver(WebElement element, String elementName) {
        applyWait(element, WaitStrategy.VISIBLE);
        new Actions(driver).moveToElement(element).perform();
        log.info("Hovered over: [{}]", elementName);
        logToExtent("Hovered over: " + elementName);
    }

    /**
     * Scroll element into viewport using JavaScript.
     */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Click an element via JavaScript (useful when standard click is intercepted).
     */
    protected void jsClick(WebElement element, String elementName) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        log.info("JS-clicked on: [{}]", elementName);
        logToExtent("JS Clicked: " + elementName);
    }

    /**
     * Returns the current page URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Returns the current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    // ----------------------------------------------------------------
    // Wait dispatcher
    // ----------------------------------------------------------------

    private void applyWait(WebElement element, WaitStrategy strategy) {
        int timeout = 20;
        switch (strategy) {
            case VISIBLE:
                WaitUtils.waitForVisibility(element, timeout);
                break;
            case CLICKABLE:
                WaitUtils.waitForClickability(element, timeout);
                break;
            case PRESENCE:
                // element already in hands of PageFactory
                break;
            case NONE:
            default:
                // no wait
                break;
        }
    }

    // ----------------------------------------------------------------
    // Extent Reports helper
    // ----------------------------------------------------------------

    private void logToExtent(String message) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().info(message);
        }
    }
}
