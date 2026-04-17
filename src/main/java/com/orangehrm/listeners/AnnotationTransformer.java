package com.orangehrm.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * AnnotationTransformer — globally applies {@link RetryAnalyzer} to every test.
 *
 * <p>Without this transformer, you would have to annotate every single
 * {@code @Test} with {@code retryAnalyzer = RetryAnalyzer.class}.
 * This listener does it once globally.
 *
 * <p>Register in testng.xml:
 * <pre>
 *   &lt;listeners&gt;
 *     &lt;listener class-name="com.orangehrm.listeners.AnnotationTransformer"/&gt;
 *     &lt;listener class-name="com.orangehrm.listeners.TestListener"/&gt;
 *   &lt;/listeners&gt;
 * </pre>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationTransformer implements IAnnotationTransformer {

    private static final Logger log = LogManager.getLogger(AnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        // Apply retry analyzer globally to all test methods
        Class retryClass = annotation.getRetryAnalyzerClass();
        if (retryClass == null || retryClass == Object.class) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            log.debug("RetryAnalyzer applied to: {}", testMethod != null ? testMethod.getName() : "unknown");
        }
    }
}
