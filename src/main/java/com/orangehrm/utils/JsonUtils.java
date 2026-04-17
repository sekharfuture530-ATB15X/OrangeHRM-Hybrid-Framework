package com.orangehrm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JsonUtils — reads test data from JSON files using Jackson.
 *
 * <p>Expected JSON structure (array of objects):
 * <pre>
 * [
 *   { "username": "Admin", "password": "admin123", "expectedResult": "pass" },
 *   { "username": "wrong",  "password": "wrong",    "expectedResult": "fail" }
 * ]
 * </pre>
 *
 * <p>Usage:
 * <pre>
 *   List&lt;Map&lt;String, String&gt;&gt; data =
 *       JsonUtils.getTestData(AppConstants.JSON_DATA_PATH);
 * </pre>
 */
public final class JsonUtils {

    private static final Logger log    = LogManager.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {}

    /**
     * Parses a JSON array file into a list of key-value maps.
     *
     * @param filePath path to the JSON file
     * @return list of Map&lt;String, String&gt; — each map represents one test record
     */
    public static List<Map<String, String>> getTestData(String filePath) {
        try {
            List<Map<String, String>> data = MAPPER.readValue(
                new File(filePath),
                new TypeReference<>() {}
            );
            log.info("Loaded {} records from JSON file: {}", data.size(), filePath);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON test data from: " + filePath, e);
        }
    }

    /**
     * Convenience: converts JSON data to Object[][] for TestNG @DataProvider.
     */
    public static Object[][] getDataAsArray(String filePath) {
        List<Map<String, String>> data = getTestData(filePath);
        Object[][] array = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            array[i][0] = data.get(i);
        }
        return array;
    }

    /**
     * Deserialize JSON file directly into a typed POJO.
     *
     * @param filePath  path to JSON file
     * @param valueType target class type
     * @param <T>       type parameter
     * @return deserialized object
     */
    public static <T> T readAs(String filePath, Class<T> valueType) {
        try {
            return MAPPER.readValue(new File(filePath), valueType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + valueType.getSimpleName(), e);
        }
    }
}
