package com.example.converter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for the LibreOffice Converter Application.
 * 
 * <p>This class contains integration tests to verify that the Spring Boot
 * application context loads correctly and all components are properly configured.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
        "converter.office-home=/usr/lib/libreoffice",
        "converter.kill-existing-process=false"
})
class LibreOfficeConverterApplicationTests {

    /**
     * Tests that the Spring Boot application context loads successfully.
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context loads without errors
    }
}