package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import com.orangehrm.enums.WaitStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * ForgotPasswordPage — Page Object for the Forgot Password screen.
 *
 * <p>Reached by clicking "Forgot your password?" on the Login page.
 */
public class ForgotPasswordPage extends BasePage {

    @FindBy(css = ".orangehrm-forgot-password-title")
    private WebElement pageTitle;

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(css = "button[type='submit']")
    private WebElement resetButton;

    @FindBy(css = "button[type='button']")
    private WebElement cancelButton;

    public ForgotPasswordPage() {
        super();
        log.info("ForgotPasswordPage initialised.");
    }

    public ForgotPasswordPage enterUsername(String username) {
        sendKeys(usernameInput, username, WaitStrategy.VISIBLE, "Reset Username Field");
        return this;
    }

    public ForgotPasswordPage clickResetPassword() {
        click(resetButton, WaitStrategy.CLICKABLE, "Reset Password Button");
        return this;
    }

    public LoginPage clickCancel() {
        click(cancelButton, WaitStrategy.CLICKABLE, "Cancel Button");
        return new LoginPage();
    }

    public String getPageTitle() {
        return getText(pageTitle, WaitStrategy.VISIBLE, "Forgot Password Title");
    }
}
