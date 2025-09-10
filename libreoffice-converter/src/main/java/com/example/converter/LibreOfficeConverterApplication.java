package com.example.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LibreOffice Converter Application
 * 
 * <p>Main application class for the LibreOffice-based document conversion service.
 * This application provides local file conversion capabilities using LibreOffice
 * without any remote dependencies or OpenOffice components.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class LibreOfficeConverterApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(LibreOfficeConverterApplication.class, args);
    }
}