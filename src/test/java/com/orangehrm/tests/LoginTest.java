package com.orangehrm.tests;

import com.orangehrm.base.BaseTest;
import com.orangehrm.constants.AppConstants;
import com.orangehrm.dataproviders.LoginDataProvider;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.ForgotPasswordPage;
import com.orangehrm.pages.LoginPage;
import org.assertj.core.api.SoftAssertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * LoginTest — test class covering all Login-related scenarios.
 *
 * <p>Extends BaseTest — browser setup / teardown is inherited.
 *
 * <p>Test Groups:
 * <ul>
 *   <li>{@code smoke}      — happy path, run on every build</li>
 *   <li>{@code regression} — full negative + edge cases</li>
 *   <li>{@code login}      — tag to run only login-related tests</li>
 * </ul>
 *
 * <p>Assertions use AssertJ for fluent, readable error messages.
 */
public class LoginTest extends BaseTest {

    // ----------------------------------------------------------------
    // TC_001: Valid Login — Smoke
    // ----------------------------------------------------------------

    /**
     * Verifies that a valid Admin user can log in and reach the Dashboard.
     * Groups: smoke, regression, login
     */
    @Test(
        groups        = {"smoke", "regression", "login"},
        description   = "TC_001 — Valid user should be redirected to the Dashboard after login",
        priority      = 1
    )
    public void testValidLogin() {
        log.info("TC_001: Starting valid login test");

        DashboardPage dashboard = new LoginPage()
            .enterUsername("Admin")
            .enterPassword("admin123")
            .clickLoginButton();

        String headerText = dashboard.getPageHeaderText();

        Assert.assertEquals(headerText, AppConstants.DASHBOARD_HEADER,
            "Dashboard header text mismatch after valid login");

        log.info("TC_001: Valid login test PASSED — Dashboard displayed: {}", headerText);
    }

    // ----------------------------------------------------------------
    // TC_002: Invalid Credentials — Regression
    // ----------------------------------------------------------------

    @Test(
        groups      = {"regression", "login"},
        description = "TC_002 — Invalid credentials should show error message",
        priority    = 2
    )
    public void testInvalidLogin() {
        log.info("TC_002: Starting invalid login test");

        LoginPage loginPage = new LoginPage()
            .enterUsername("invalidUser")
            .enterPassword("wrongPassword")
            .clickLoginWithoutCredentials();

        String errorMsg = loginPage.getErrorMessage();

        Assert.assertTrue(
            errorMsg.contains(AppConstants.INVALID_CREDENTIALS_MSG),
            "Expected error message to contain '" + AppConstants.INVALID_CREDENTIALS_MSG +
            "' but got: '" + errorMsg + "'"
        );
    }

    // ----------------------------------------------------------------
    // TC_003: Blank Credentials — Field Validation
    // ----------------------------------------------------------------

    @Test(
        groups      = {"regression", "login"},
        description = "TC_003 — Submitting blank form should show required field validation",
        priority    = 3
    )
    public void testBlankCredentialValidation() {
        log.info("TC_003: Testing blank credential validation");

        LoginPage loginPage = new LoginPage()
            .clickLoginWithoutCredentials();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(loginPage.getUsernameValidationMessage())
            .as("Username required validation message")
            .contains(AppConstants.REQUIRED_FIELD_MSG);

        soft.assertAll();
        log.info("TC_003: Blank validation test PASSED");
    }

    // ----------------------------------------------------------------
    // TC_004: Data-Driven — JSON Data Provider
    // ----------------------------------------------------------------

    /**
     * Data-driven login test driven by JSON file.
     * Covers both positive and negative flows from a single test method.
     */
    @Test(
        dataProvider      = "loginDataJson",
        dataProviderClass = LoginDataProvider.class,
        groups            = {"regression", "login", "datadriven"},
        description       = "TC_004 — Data-driven login test with JSON data",
        priority          = 4
    )
    @SuppressWarnings("unchecked")
    public void testLoginDataDriven(Map<String, String> testData) {
        String username        = testData.get("username");
        String password        = testData.get("password");
        String expectedResult  = testData.get("expectedResult");
        String testCase        = testData.getOrDefault("testCase", "N/A");

        log.info("{}: Testing login — username={}, expectedResult={}", testCase, username, expectedResult);

        LoginPage loginPage = new LoginPage()
            .enterUsername(username)
            .enterPassword(password);

        if ("pass".equalsIgnoreCase(expectedResult)) {
            DashboardPage dashboard = loginPage.clickLoginButton();
            Assert.assertEquals(dashboard.getPageHeaderText(), AppConstants.DASHBOARD_HEADER,
                testCase + ": Dashboard header not found after valid login");
        } else {
            LoginPage resultPage = loginPage.clickLoginWithoutCredentials();
            String errorMsg = resultPage.getErrorMessage();
            Assert.assertTrue(
                errorMsg.contains(AppConstants.INVALID_CREDENTIALS_MSG),
                testCase + ": Expected error message for invalid login but got: " + errorMsg
            );
        }

        log.info("{}: Data-driven login test completed.", testCase);
    }

    // ----------------------------------------------------------------
    // TC_005: Data-Driven — Excel Data Provider
    // ----------------------------------------------------------------

    @Test(
        dataProvider      = "loginDataExcel",
        dataProviderClass = LoginDataProvider.class,
        groups            = {"regression", "login", "datadriven", "excel"},
        description       = "TC_005 — Data-driven login test with Excel data",
        priority          = 5
    )
    @SuppressWarnings("unchecked")
    public void testLoginWithExcelData(Map<String, String> testData) {
        String username       = testData.get("username");
        String password       = testData.get("password");
        String expectedResult = testData.get("expectedResult");

        log.info("Excel TC: login test — user={}", username);

        LoginPage loginPage = new LoginPage()
            .enterUsername(username)
            .enterPassword(password);

        if ("pass".equalsIgnoreCase(expectedResult)) {
            DashboardPage dashboard = loginPage.clickLoginButton();
            Assert.assertEquals(dashboard.getPageHeaderText(), AppConstants.DASHBOARD_HEADER);
        } else {
            LoginPage resultPage = loginPage.clickLoginWithoutCredentials();
            Assert.assertTrue(resultPage.getErrorMessage().contains(AppConstants.INVALID_CREDENTIALS_MSG));
        }
    }

    // ----------------------------------------------------------------
    // TC_006: Forgot Password Link
    // ----------------------------------------------------------------

    @Test(
        groups      = {"regression", "login"},
        description = "TC_006 — Forgot password link should navigate to reset page",
        priority    = 6
    )
    public void testForgotPasswordNavigation() {
        log.info("TC_006: Testing Forgot Password navigation");

        ForgotPasswordPage forgotPage = new LoginPage().clickForgotPassword();

        Assert.assertTrue(
            forgotPage.getPageTitle().toLowerCase().contains("reset"),
            "Forgot Password page title should contain 'reset'"
        );
        log.info("TC_006: Forgot Password navigation PASSED");
    }

    // ----------------------------------------------------------------
    // TC_007: Logo Visibility — Page Sanity
    // ----------------------------------------------------------------

    @Test(
        groups      = {"smoke", "login"},
        description = "TC_007 — Company logo should be visible on login page",
        priority    = 7
    )
    public void testLoginPageLogoDisplayed() {
        log.info("TC_007: Checking login page logo visibility");

        LoginPage loginPage = new LoginPage();
        Assert.assertTrue(loginPage.isLogoDisplayed(), "Company logo should be displayed on the login page");

        log.info("TC_007: Logo visibility test PASSED");
    }
}
