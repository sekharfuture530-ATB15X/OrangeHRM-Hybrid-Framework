package com.orangehrm.base;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.driver.DriverFactory;
import com.orangehrm.driver.DriverManager;
import com.orangehrm.reporting.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.*;

/**
 * BaseTest — the parent class all test classes must extend.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Browser setup before each test method (@BeforeMethod)</li>
 *   <li>Navigate to application URL</li>
 *   <li>Browser teardown after each test method (@AfterMethod)</li>
 * </ol>
 *
 * <p>Design rationale:
 * <ul>
 *   <li>@BeforeMethod / @AfterMethod (not @BeforeClass) for clean state per test</li>
 *   <li>Browser type / URL resolved at runtime so Jenkins can override via -D flags</li>
 *   <li>All child test classes inherit setup/teardown — zero code duplication</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   public class LoginTest extends BaseTest {
 *       // tests here — browser is already open and on the login page
 *   }
 * </pre>
 */
@Listeners(com.orangehrm.listeners.TestListener.class)
public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    // ----------------------------------------------------------------
    // Setup
    // ----------------------------------------------------------------

    /**
     * Runs before EACH test method.
     * Initializes the browser and navigates to the AUT URL.
     *
     * @param context TestNG context (used for reporting environment details)
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestContext context) {
        log.info("---------- Setting up browser for test ----------");
        DriverFactory.initDriver();

        String appUrl = ConfigReader.getAppUrl();
        log.info("Navigating to: {}", appUrl);
        DriverManager.getDriver().get(appUrl);

        // Log environment info to Extent report
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest()
                .info("Browser: " + ConfigReader.getBrowser())
                .info("URL: " + appUrl)
                .info("Environment: " + ConfigReader.getEnvironment());
        }
    }

    // ----------------------------------------------------------------
    // Teardown
    // ----------------------------------------------------------------

    /**
     * Runs after EACH test method.
     * Quits the browser and cleans up ThreadLocal to prevent memory leaks.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("---------- Quitting browser ----------");
        DriverManager.quitDriver();
    }
}
