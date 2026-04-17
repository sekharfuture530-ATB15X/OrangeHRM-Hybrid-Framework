package com.orangehrm.enums;

/**
 * Supported browser types.
 * Using an enum instead of raw strings makes the code type-safe
 * and IDE-friendly.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    CHROME_HEADLESS,
    FIREFOX_HEADLESS;

    /**
     * Parse a string value (from properties / CLI) into a BrowserType.
     *
     * @param value string representation of the browser
     * @return corresponding BrowserType enum constant
     */
    public static BrowserType fromString(String value) {
        String browser = value.toLowerCase().trim();
        switch (browser) {
            case "chrome":           return CHROME;
            case "firefox":          return FIREFOX;
            case "edge":             return EDGE;
            case "chrome-head":
            case "chrome-headless":  return CHROME_HEADLESS;
            case "firefox-head":
            case "firefox-headless": return FIREFOX_HEADLESS;
            default:
                throw new IllegalArgumentException(
                    "Unsupported browser: '" + value + "'. " +
                    "Supported: chrome | firefox | edge | chrome-headless | firefox-headless"
                );
        }
    }
}
