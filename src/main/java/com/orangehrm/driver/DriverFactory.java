package com.orangehrm.driver;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.enums.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * DriverFactory — responsible for creating and configuring WebDriver instances.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>Uses WebDriverManager to auto-manage binary downloads (no manual chromedriver.exe)</li>
 *   <li>Supports local and remote (Grid / BrowserStack) execution</li>
 *   <li>Browser type resolved from System property → config.properties (Jenkins override friendly)</li>
 *   <li>Stores the instance in DriverManager's ThreadLocal for parallel safety</li>
 * </ul>
 *
 * <p>Usage (called by BaseTest):
 * <pre>
 *   DriverFactory.initDriver();
 *   WebDriver driver = DriverManager.getDriver();
 * </pre>
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {}

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /**
     * Initialize WebDriver based on config / system properties.
     * Stores it in DriverManager ThreadLocal.
     */
    public static void initDriver() {
        String browserValue = ConfigReader.getBrowser();
        log.info("Initializing browser: [{}]", browserValue);

        BrowserType browserType = BrowserType.fromString(browserValue);

        WebDriver driver;
        if (ConfigReader.isRunRemote()) {
            driver = createRemoteDriver(browserType);
        } else {
            driver = createLocalDriver(browserType);
        }

        configureDriver(driver);
        DriverManager.setDriver(driver);
        log.info("WebDriver initialized successfully: {}", driver.getClass().getSimpleName());
    }

    // ----------------------------------------------------------------
    // Local Driver Creation
    // ----------------------------------------------------------------

    private static WebDriver createLocalDriver(BrowserType type) {
        switch (type) {
            case CHROME: {
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(buildChromeOptions(false));
            }
            case CHROME_HEADLESS: {
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(buildChromeOptions(true));
            }
            case FIREFOX: {
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(buildFirefoxOptions(false));
            }
            case FIREFOX_HEADLESS: {
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(buildFirefoxOptions(true));
            }
            case EDGE: {
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver(buildEdgeOptions());
            }
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + type);
        }
    }

    // ----------------------------------------------------------------
    // Remote Driver Creation (Selenium Grid / BrowserStack)
    // ----------------------------------------------------------------

    private static WebDriver createRemoteDriver(BrowserType type) {
        String gridUrl = ConfigReader.getGridUrl();
        log.info("Connecting to Selenium Grid at: {}", gridUrl);

        try {
            switch (type) {
                case CHROME:
                case CHROME_HEADLESS:
                    return new RemoteWebDriver(new URL(gridUrl),
                        buildChromeOptions(type == BrowserType.CHROME_HEADLESS));
                case FIREFOX:
                case FIREFOX_HEADLESS:
                    return new RemoteWebDriver(new URL(gridUrl),
                        buildFirefoxOptions(type == BrowserType.FIREFOX_HEADLESS));
                case EDGE:
                    return new RemoteWebDriver(new URL(gridUrl), buildEdgeOptions());
                default:
                    throw new IllegalArgumentException("Unsupported browser type for remote: " + type);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL: " + gridUrl, e);
        }
    }

    // ----------------------------------------------------------------
    // Browser Options Builders
    // ----------------------------------------------------------------

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");         // Chrome v112+ headless flag
        }
        options.addArguments(
            "--start-maximized",
            "--disable-notifications",
            "--disable-popup-blocking",
            "--no-sandbox",                                 // required for Linux/Docker
            "--disable-dev-shm-usage",                     // prevents /dev/shm exhaustion in Docker
            "--disable-extensions",
            "--remote-allow-origins=*",
            "--disable-infobars"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        log.debug("ChromeOptions configured. Headless: {}", headless);
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        log.debug("FirefoxOptions configured. Headless: {}", headless);
        return options;
    }

    private static EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(
            "--start-maximized",
            "--disable-notifications",
            "--no-sandbox",
            "--disable-dev-shm-usage"
        );
        log.debug("EdgeOptions configured.");
        return options;
    }

    // ----------------------------------------------------------------
    // Common Driver Configuration
    // ----------------------------------------------------------------

    private static void configureDriver(WebDriver driver) {
        int pageLoad  = ConfigReader.getInt("page.load.timeout");
        int implWait  = ConfigReader.getInt("implicit.wait");

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implWait));
        
        // Window sizing is handled via ChromeOptions/FirefoxOptions arguments 
        // which is more stable in headless/CI environments.
        
        log.debug("Driver timeouts set — PageLoad: {}s, Implicit: {}s", pageLoad, implWait);
    }
}
