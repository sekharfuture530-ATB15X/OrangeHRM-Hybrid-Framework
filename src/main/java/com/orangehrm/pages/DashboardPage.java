package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import com.orangehrm.enums.WaitStrategy;
import com.orangehrm.utils.WaitUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * DashboardPage — Page Object for the OrangeHRM Dashboard.
 *
 * <p>Navigated to after a successful login.
 * Provides helpers to verify successful landing and navigate to sub-sections.
 */
public class DashboardPage extends BasePage {

    // ----------------------------------------------------------------
    // Locators
    // ----------------------------------------------------------------

    @FindBy(css = ".oxd-topbar-header-breadcrumb h6")
    private WebElement pageHeader;

    @FindBy(css = ".oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(css = ".oxd-userdropdown-name")
    private WebElement loggedInUserName;

    @FindBy(linkText = "Logout")
    private WebElement logoutLink;

    @FindBy(css = ".oxd-main-menu-item--name")
    private WebElement firstMenuItemLabel;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------

    public DashboardPage() {
        super();
        log.info("DashboardPage initialised.");
    }

    // ----------------------------------------------------------------
    // Actions
    // ----------------------------------------------------------------

    /**
     * Logout from the application.
     *
     * @return new LoginPage instance
     */
    public LoginPage logout() {
        click(userDropdown, WaitStrategy.CLICKABLE, "User Dropdown");
        click(logoutLink, WaitStrategy.CLICKABLE, "Logout Link");
        return new LoginPage();
    }

    // ----------------------------------------------------------------
    // State Getters
    // ----------------------------------------------------------------

    /**
     * Returns the main header text (e.g. "Dashboard").
     * Use this to verify the user is on the correct page post-login.
     */
    public String getPageHeaderText() {
        WaitUtils.waitForUrlContains("dashboard", 15);
        return getText(pageHeader, WaitStrategy.VISIBLE, "Dashboard Page Header");
    }

    /**
     * Returns true if the Dashboard header is displayed.
     */
    public boolean isDashboardDisplayed() {
        return isDisplayed(pageHeader, "Dashboard Header");
    }

    /**
     * Returns the display name of the currently logged-in user.
     */
    public String getLoggedInUser() {
        return getText(loggedInUserName, WaitStrategy.VISIBLE, "Logged-in Username");
    }
}
