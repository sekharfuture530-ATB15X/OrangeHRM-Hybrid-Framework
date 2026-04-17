package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import com.orangehrm.enums.WaitStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — Page Object for the OrangeHRM login screen.
 *
 * <p>URL: https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
 *
 * <p>Design principles applied:
 * <ul>
 *   <li>All locators via {@code @FindBy} — PageFactory initialised in BasePage constructor</li>
 *   <li>Every action method returns a Page object (fluent API / method chaining)</li>
 *   <li>Only page-specific logic here — no assertions, no waits beyond WaitStrategy</li>
 *   <li>Returns DashboardPage on successful login (page transitions are typed)</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   DashboardPage dashboard = new LoginPage()
 *       .enterUsername("Admin")
 *       .enterPassword("admin123")
 *       .clickLoginButton();
 * </pre>
 */
public class LoginPage extends BasePage {

    // ----------------------------------------------------------------
    // Locators (via PageFactory @FindBy)
    // ----------------------------------------------------------------

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".oxd-alert-content-text")
    private WebElement errorMessage;

    @FindBy(css = ".orangehrm-login-branding img")
    private WebElement companyLogo;

    @FindBy(css = ".oxd-input--error ~ span")
    private WebElement usernameValidationMsg;

    @FindBy(css = ".oxd-text--p.orangehrm-login-forgot-header")
    private WebElement forgotPasswordLink;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------

    /**
     * Initialises PageFactory elements via the BasePage constructor.
     */
    public LoginPage() {
        super();
        log.info("LoginPage initialised.");
    }

    // ----------------------------------------------------------------
    // Page Actions (fluent — each returns a Page object)
    // ----------------------------------------------------------------

    /**
     * Type the username into the username field.
     *
     * @param username string to type
     * @return this LoginPage instance (for method chaining)
     */
    public LoginPage enterUsername(String username) {
        sendKeys(usernameInput, username, WaitStrategy.VISIBLE, "Username Field");
        return this;
    }

    /**
     * Type the password into the password field.
     *
     * @param password string to type
     * @return this LoginPage instance
     */
    public LoginPage enterPassword(String password) {
        sendKeys(passwordInput, password, WaitStrategy.VISIBLE, "Password Field");
        return this;
    }

    /**
     * Click the Login button.
     * Returns a DashboardPage — the expected destination after successful login.
     *
     * @return new DashboardPage instance
     */
    public DashboardPage clickLoginButton() {
        click(loginButton, WaitStrategy.CLICKABLE, "Login Button");
        return new DashboardPage();
    }

    /**
     * Full login in a single call (convenience method).
     *
     * @param username user credential
     * @param password user credential
     * @return DashboardPage on success
     */
    public DashboardPage loginAs(String username, String password) {
        return enterUsername(username)
               .enterPassword(password)
               .clickLoginButton();
    }

    /**
     * Click Login without providing credentials (triggers validation messages).
     *
     * @return this LoginPage (still on login page)
     */
    public LoginPage clickLoginWithoutCredentials() {
        click(loginButton, WaitStrategy.CLICKABLE, "Login Button (empty submit)");
        return this;
    }

    /**
     * Click the "Forgot your password?" link.
     *
     * @return new ForgotPasswordPage instance
     */
    public ForgotPasswordPage clickForgotPassword() {
        click(forgotPasswordLink, WaitStrategy.CLICKABLE, "Forgot Password Link");
        return new ForgotPasswordPage();
    }

    // ----------------------------------------------------------------
    // Page Assertions (state getters — used in test classes)
    // ----------------------------------------------------------------

    /**
     * Returns the error message displayed on invalid login.
     */
    public String getErrorMessage() {
        return getText(errorMessage, WaitStrategy.VISIBLE, "Login Error Message");
    }

    /**
     * Returns the validation message shown below the username field when empty.
     */
    public String getUsernameValidationMessage() {
        return getText(usernameValidationMsg, WaitStrategy.VISIBLE, "Username Validation Message");
    }

    /**
     * Returns true if the company logo is visible (page is loaded).
     */
    public boolean isLogoDisplayed() {
        return isDisplayed(companyLogo, "Company Logo");
    }

    /**
     * Returns true if the login button is displayed and clickable.
     */
    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton, "Login Button");
    }
}
