package com.orangehrm.config;

import com.orangehrm.constants.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton ConfigReader — loads config.properties once and caches it.
 *
 * <p>Priority order for property resolution:
 * <ol>
 *   <li>JVM System Properties  (-Dbrowser=firefox  passed via CLI / Jenkins)</li>
 *   <li>config.properties file in project resources</li>
 * </ol>
 *
 * <p>Usage:
 * <pre>
 *   String url = ConfigReader.get("app.url");
 *   int timeout = ConfigReader.getInt("explicit.wait");
 * </pre>
 */
public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {}

    // ----------------------------------------------------------------
    // Internal loader
    // ----------------------------------------------------------------
    private static void loadProperties() {
        try (InputStream fis = new FileInputStream(AppConstants.CONFIG_FILE_PATH)) {
            properties.load(fis);
            log.info("config.properties loaded successfully from: {}", AppConstants.CONFIG_FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to load config.properties. Path: {}", AppConstants.CONFIG_FILE_PATH, e);
            throw new RuntimeException("Cannot load config.properties! Ensure the file exists at: "
                    + AppConstants.CONFIG_FILE_PATH, e);
        }
    }

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /**
     * Returns the value for the given key.
     * JVM system property overrides file value (enables CLI / Jenkins overrides).
     *
     * @param key property key
     * @return string value
     * @throws RuntimeException if key is not found in either source
     */
    public static String get(String key) {
        // System property (e.g. -Dbrowser=firefox) takes highest priority
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            log.debug("System property override: {}={}", key, systemValue);
            return systemValue.trim();
        }

        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties or System properties.");
        }
        return value.trim();
    }

    /**
     * Returns a property value, or defaultValue if not present.
     */
    public static String getOrDefault(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            log.warn("Key '{}' not found, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /** Convenience method — returns property as int. */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /** Convenience method — returns property as boolean. */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /** Returns the configured application URL. */
    public static String getAppUrl() {
        return get("app.url");
    }

    /** Returns the configured browser name (may be overridden by -Dbrowser CLI arg). */
    public static String getBrowser() {
        return getOrDefault("browser", "chrome");
    }

    /** Returns the configured environment (qa, staging, prod). */
    public static String getEnvironment() {
        return getOrDefault("env", "qa");
    }

    /** Returns whether to run on Selenium Grid / remote. */
    public static boolean isRunRemote() {
        return getBoolean("run.remote");
    }

    /** Returns Selenium Grid hub URL. */
    public static String getGridUrl() {
        return get("grid.url");
    }
}
