package com.orangehrm.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer — re-runs flaky tests automatically (up to MAX_RETRY times).
 *
 * <p>Production test suites invariably have some environment-related flakiness
 * (network delays, CI resource contention, animation timing). Rather than
 * marking all such failures as real bugs, this analyzer gives each failing
 * test a configured number of automatic retries.
 *
 * <p>Usage — annotate any test that needs retry:
 * <pre>
 *   &#64;Test(retryAnalyzer = RetryAnalyzer.class)
 *   public void testSomethingFlaky() { ... }
 * </pre>
 *
 * <p>Or apply globally via a TestNG listener that sets the retry analyzer
 * on all test methods via {@code IAnnotationTransformer}.
 *
 * <p>Override count via System property:
 * <pre>
 *   -Dmax.retry=3   (default: 2)
 * </pre>
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    private static final int MAX_RETRY;

    static {
        String prop = System.getProperty("max.retry", "2");
        MAX_RETRY = Integer.parseInt(prop);
    }

    private int retryCount = 0;

    /**
     * Called by TestNG after a test failure to determine whether to retry.
     *
     * @param result the failed test result
     * @return true = retry; false = mark as final failure
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            log.warn("Retrying test [{}] — attempt {}/{}: {}",
                result.getName(), retryCount, MAX_RETRY,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no exception");
            return true;
        }
        log.error("Test [{}] FAILED after {} retries — marking as FAILED.",
            result.getName(), MAX_RETRY);
        return false;
    }
}
