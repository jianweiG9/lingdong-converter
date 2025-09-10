/*
 * Document Converter - A local document converter based on LibreOffice
 * Migrated from JODConverter project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.docconverter.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.docconverter.core.DocumentConverter;
import com.docconverter.core.document.DocumentFormat;
import com.docconverter.core.document.DocumentFormatRegistry;
import com.docconverter.core.office.OfficeException;

/**
 * REST controller for document conversion operations.
 * 
 * <p>This controller provides endpoints for converting documents between different formats
 * using LibreOffice. It supports file upload and returns the converted document as a download.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/convert")
public class ConversionController {

    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);

    private final DocumentConverter documentConverter;
    private final DocumentFormatRegistry formatRegistry;

    /**
     * Constructs a new ConversionController.
     *
     * @param documentConverter The document converter to use for conversions
     * @param formatRegistry The document format registry
     */
    @Autowired
    public ConversionController(DocumentConverter documentConverter, DocumentFormatRegistry formatRegistry) {
        this.documentConverter = documentConverter;
        this.formatRegistry = formatRegistry;
    }

    /**
     * Converts a document to the specified target format.
     *
     * @param file The source document file
     * @param targetFormat The target format (e.g., "pdf", "docx", "xlsx")
     * @return The converted document as a byte array
     * @throws OfficeException If conversion fails
     * @throws IOException If file I/O fails
     */
    @PostMapping(value = "/to/{targetFormat}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> convertDocument(
            @RequestParam("file") MultipartFile file,
            @PathVariable String targetFormat) throws OfficeException, IOException {

        logger.info("Converting document {} to format {}", file.getOriginalFilename(), targetFormat);

        // Validate input
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Get target format
        DocumentFormat format = formatRegistry.getFormatByExtension(targetFormat);
        if (format == null) {
            logger.error("Unsupported target format: {}", targetFormat);
            return ResponseEntity.badRequest().build();
        }

        try {
            // Perform conversion
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            documentConverter.convert(file.getInputStream(), true)
                    .to(outputStream, format, true)
                    .execute();

            byte[] convertedData = outputStream.toByteArray();

            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(format.getMediaType()));
            headers.setContentDispositionFormData("attachment", 
                    generateOutputFileName(file.getOriginalFilename(), targetFormat));
            headers.setContentLength(convertedData.length);

            logger.info("Successfully converted document {} to {} format, size: {} bytes", 
                    file.getOriginalFilename(), targetFormat, convertedData.length);

            return new ResponseEntity<>(convertedData, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error converting document {} to {}: {}", 
                    file.getOriginalFilename(), targetFormat, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets information about supported document formats.
     *
     * @return Information about supported formats
     */
    @GetMapping("/formats")
    public ResponseEntity<String> getSupportedFormats() {
        // For now, return a simple text response
        StringBuilder sb = new StringBuilder();
        sb.append("Supported input formats: doc, docx, odt, rtf, txt, xls, xlsx, ods, csv, ppt, pptx, odp\n");
        sb.append("Supported output formats: pdf, doc, docx, odt, rtf, txt, xls, xlsx, ods, csv, ppt, pptx, odp, png, jpg, svg");
        
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(sb.toString());
    }

    /**
     * Health check endpoint.
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Document Converter is running");
    }

    private String generateOutputFileName(String originalFileName, String targetFormat) {
        if (originalFileName == null) {
            return "converted." + targetFormat;
        }
        
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return originalFileName.substring(0, lastDotIndex) + "." + targetFormat;
        } else {
            return originalFileName + "." + targetFormat;
        }
    }
}