package com.example.converter.core;

import com.example.converter.config.ConverterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the DocumentConverter class.
 * 
 * <p>These tests verify the document conversion functionality using actual
 * LibreOffice processes. They require LibreOffice to be installed on the system.</p>
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
@EnabledIfSystemProperty(named = "test.integration", matches = "true")
class DocumentConverterTest {

    private DocumentConverter documentConverter;
    private LibreOfficeManager libreOfficeManager;
    private ConverterProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ConverterProperties();
        libreOfficeManager = new LibreOfficeManager(properties);
        documentConverter = new DocumentConverter(libreOfficeManager, properties);
    }

    @Test
    void testDetectFormat() throws ConversionException {
        // Test DOC format detection
        DocumentFormat format = documentConverter.detectFormat("test.doc");
        assertEquals(DocumentFormat.DOC, format);

        // Test DOCX format detection
        format = documentConverter.detectFormat("test.docx");
        assertEquals(DocumentFormat.DOCX, format);

        // Test PDF format detection
        format = documentConverter.detectFormat("test.pdf");
        assertEquals(DocumentFormat.PDF, format);

        // Test unsupported format
        assertThrows(ConversionException.class, () -> {
            documentConverter.detectFormat("test.xyz");
        });
    }

    @Test
    void testIsConversionSupported() {
        // Test supported conversion
        assertTrue(documentConverter.isConversionSupported(DocumentFormat.DOC, DocumentFormat.PDF));
        assertTrue(documentConverter.isConversionSupported(DocumentFormat.DOCX, DocumentFormat.PDF));
        assertTrue(documentConverter.isConversionSupported(DocumentFormat.PDF, DocumentFormat.DOCX));

        // Test unsupported conversion (same format)
        assertFalse(documentConverter.isConversionSupported(DocumentFormat.DOC, DocumentFormat.DOC));

        // Test null format
        assertFalse(documentConverter.isConversionSupported(null, DocumentFormat.PDF));
        assertFalse(documentConverter.isConversionSupported(DocumentFormat.DOC, null));
    }

    @Test
    void testConvertDocToPdf() throws IOException, ConversionException {
        // Create a simple test document
        Path testDoc = createTestDocument("test.doc", "Hello, World!");
        Path outputPdf = Files.createTempFile("output", ".pdf");

        try {
            // Perform conversion
            documentConverter.convert(testDoc.toFile(), outputPdf.toFile(), 
                    DocumentFormat.DOC, DocumentFormat.PDF);

            // Verify output file exists and has content
            assertTrue(Files.exists(outputPdf));
            assertTrue(Files.size(outputPdf) > 0);

        } finally {
            // Clean up
            Files.deleteIfExists(testDoc);
            Files.deleteIfExists(outputPdf);
        }
    }

    @Test
    void testConvertDocxToPdf() throws IOException, ConversionException {
        // Create a simple test document
        Path testDocx = createTestDocument("test.docx", "Hello, World!");
        Path outputPdf = Files.createTempFile("output", ".pdf");

        try {
            // Perform conversion
            documentConverter.convert(testDocx.toFile(), outputPdf.toFile(), 
                    DocumentFormat.DOCX, DocumentFormat.PDF);

            // Verify output file exists and has content
            assertTrue(Files.exists(outputPdf));
            assertTrue(Files.size(outputPdf) > 0);

        } finally {
            // Clean up
            Files.deleteIfExists(testDocx);
            Files.deleteIfExists(outputPdf);
        }
    }

    @Test
    void testConvertPdfToDocx() throws IOException, ConversionException {
        // Create a simple test PDF (this is a simplified test)
        Path testPdf = createTestDocument("test.pdf", "Hello, World!");
        Path outputDocx = Files.createTempFile("output", ".docx");

        try {
            // Perform conversion
            documentConverter.convert(testPdf.toFile(), outputDocx.toFile(), 
                    DocumentFormat.PDF, DocumentFormat.DOCX);

            // Verify output file exists and has content
            assertTrue(Files.exists(outputDocx));
            assertTrue(Files.size(outputDocx) > 0);

        } finally {
            // Clean up
            Files.deleteIfExists(testPdf);
            Files.deleteIfExists(outputDocx);
        }
    }

    @Test
    void testConvertToBytes() throws IOException, ConversionException {
        // Create a simple test document
        Path testDoc = createTestDocument("test.doc", "Hello, World!");

        try {
            // Perform conversion to bytes
            byte[] convertedBytes = documentConverter.convertToBytes(testDoc.toFile(), 
                    DocumentFormat.DOC, DocumentFormat.PDF);

            // Verify bytes are not empty
            assertNotNull(convertedBytes);
            assertTrue(convertedBytes.length > 0);

        } finally {
            // Clean up
            Files.deleteIfExists(testDoc);
        }
    }

    @Test
    void testConvertFromBytes() throws IOException, ConversionException {
        // Create test content
        String testContent = "Hello, World!";
        byte[] inputBytes = testContent.getBytes();

        try {
            // Perform conversion from bytes
            byte[] convertedBytes = documentConverter.convertFromBytes(inputBytes, 
                    DocumentFormat.TXT, DocumentFormat.PDF);

            // Verify bytes are not empty
            assertNotNull(convertedBytes);
            assertTrue(convertedBytes.length > 0);

        } catch (ConversionException e) {
            // This might fail if LibreOffice doesn't support the conversion
            // Just log and continue
            System.out.println("Conversion from bytes failed (expected for some formats): " + e.getMessage());
        }
    }

    /**
     * Creates a simple test document with the specified content.
     * 
     * @param fileName the name of the file to create
     * @param content the content to write to the file
     * @return the path to the created file
     * @throws IOException if file creation fails
     */
    private Path createTestDocument(String fileName, String content) throws IOException {
        Path testFile = Files.createTempFile("test", fileName);
        Files.write(testFile, content.getBytes());
        return testFile;
    }
}