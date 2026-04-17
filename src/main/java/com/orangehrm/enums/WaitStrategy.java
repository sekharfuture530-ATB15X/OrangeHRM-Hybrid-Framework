package com.orangehrm.enums;

/**
 * Enum representing explicit wait strategies available in the framework.
 *
 * <p>Usage in Page Objects:
 * <pre>
 *   sendKeys(usernameField, "Admin", WaitStrategy.CLICKABLE);
 *   click(loginButton, WaitStrategy.CLICKABLE);
 *   getText(headerLabel, WaitStrategy.VISIBLE);
 * </pre>
 */
public enum WaitStrategy {

    /** Element is present in the DOM AND visible on screen */
    VISIBLE,

    /** Element is visible AND enabled (ready to be clicked) */
    CLICKABLE,

    /** Element is present in the DOM (may not be visible) */
    PRESENCE,

    /** No explicit wait — use only when guaranteed to be immediately available */
    NONE
}
