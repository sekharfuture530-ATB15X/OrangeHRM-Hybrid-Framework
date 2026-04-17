package com.orangehrm.driver;

import org.openqa.selenium.WebDriver;

/**
 * Thread-local WebDriver manager.
 *
 * <p>Stores each thread's WebDriver instance in a ThreadLocal variable,
 * which is crucial for parallel test execution (each thread gets its own
 * isolated browser instance with zero cross-contamination).
 *
 * <p>Usage:
 * <pre>
 *   DriverManager.setDriver(driver);   // called by DriverFactory
 *   WebDriver driver = DriverManager.getDriver();
 *   DriverManager.quitDriver();        // called in @AfterMethod
 * </pre>
 */
public final class DriverManager {

    /**
     * ThreadLocal ensures each test thread owns its private WebDriver.
     * withInitial(() -> null) means uninitialized threads return null
     * instead of throwing NPEs.
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal =
            ThreadLocal.withInitial(() -> null);

    private DriverManager() {}

    /**
     * Store the WebDriver for the current thread.
     *
     * @param driver initialized WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /**
     * Retrieve the WebDriver for the current thread.
     *
     * @return WebDriver instance, or null if not yet initialized
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quit the browser and clean up the ThreadLocal to prevent memory leaks.
     * Must be called in {@code @AfterMethod} or equivalent teardown.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();  // ← critical: prevents memory leaks in thread pools
        }
    }

    /**
     * Returns true if a driver has been initialized for the current thread.
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}
