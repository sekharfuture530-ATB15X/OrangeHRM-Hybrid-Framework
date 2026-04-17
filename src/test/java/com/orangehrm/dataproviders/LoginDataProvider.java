package com.orangehrm.dataproviders;

import com.orangehrm.constants.AppConstants;
import com.orangehrm.utils.ExcelUtils;
import com.orangehrm.utils.JsonUtils;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

/**
 * LoginDataProvider — TestNG DataProvider class for Login test data.
 *
 * <p>Provides two separate data sources:
 * <ol>
 *   <li>Excel (.xlsx via Apache POI) — {@code loginDataFromExcel()}</li>
 *   <li>JSON (via Jackson) — {@code loginDataFromJson()}</li>
 * </ol>
 *
 * <p>Usage in test class:
 * <pre>
 *   &#64;Test(dataProvider = "loginDataExcel", dataProviderClass = LoginDataProvider.class)
 *   public void testLogin(Map&lt;String, String&gt; data) {
 *       String username = data.get("username");
 *       ...
 *   }
 * </pre>
 *
 * <p>Excel sheet "LoginData" expected columns:
 * <pre>
 *   username | password | expectedResult | testCase
 * </pre>
 *
 * <p>JSON file expected structure:
 * <pre>
 *   [
 *     { "username": "Admin", "password": "admin123", "expectedResult": "pass", "testCase": "TC_001" },
 *     { "username": "wrong", "password": "wrong",    "expectedResult": "fail", "testCase": "TC_002" }
 *   ]
 * </pre>
 */
public class LoginDataProvider {

    /**
     * Provides login credentials from Excel — Data Driven approach.
     *
     * @return Object[][] where each row is a Map{@literal <}String, String{@literal >}
     */
    @DataProvider(name = "loginDataExcel", parallel = false)
    public static Object[][] loginDataFromExcel() {
        return ExcelUtils.getDataAsArray(AppConstants.EXCEL_DATA_PATH, AppConstants.LOGIN_SHEET);
    }

    /**
     * Provides login credentials from JSON.
     *
     * @return Object[][] where each element is a Map{@literal <}String, String{@literal >}
     */
    @DataProvider(name = "loginDataJson", parallel = false)
    public static Object[][] loginDataFromJson() {
        return JsonUtils.getDataAsArray(AppConstants.JSON_DATA_PATH);
    }

    /**
     * Provides valid-only credentials (filtered from JSON).
     * Useful for smoke tests that only need positive flows.
     */
    @DataProvider(name = "validLoginData", parallel = false)
    public static Object[][] validLoginData() {
        List<Map<String, String>> allData = JsonUtils.getTestData(AppConstants.JSON_DATA_PATH);

        Object[][] validRows = allData.stream()
            .filter(row -> "pass".equalsIgnoreCase(row.get("expectedResult")))
            .map(row -> new Object[]{row})
            .toArray(Object[][]::new);

        return validRows;
    }

    /**
     * Provides invalid-only credentials (filtered from JSON).
     * Useful for negative test scenarios.
     */
    @DataProvider(name = "invalidLoginData", parallel = false)
    public static Object[][] invalidLoginData() {
        List<Map<String, String>> allData = JsonUtils.getTestData(AppConstants.JSON_DATA_PATH);

        Object[][] invalidRows = allData.stream()
            .filter(row -> "fail".equalsIgnoreCase(row.get("expectedResult")))
            .map(row -> new Object[]{row})
            .toArray(Object[][]::new);

        return invalidRows;
    }
}
