package com.orangehrm.reporting;

import com.aventstack.extentreports.ExtentTest;

/**
 * ExtentTestManager — thread-local store for per-test {@link ExtentTest} nodes.
 *
 * <p>In parallel execution each test thread creates its own ExtentTest node.
 * Storing them in a ThreadLocal guarantees that logging calls from Thread A
 * never accidentally write to Thread B's test node.
 *
 * <p>Usage:
 * <pre>
 *   // In TestListener.onTestStart()
 *   ExtentTest test = ExtentManager.getInstance().createTest(testName);
 *   ExtentTestManager.setTest(test);
 *
 *   // In any Page / Utility class
 *   ExtentTestManager.getTest().pass("Element clicked successfully");
 *   ExtentTestManager.getTest().fail("Unexpected alert appeared", MediaEntityBuilder...);
 * </pre>
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentTestManager() {}

    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }
}
