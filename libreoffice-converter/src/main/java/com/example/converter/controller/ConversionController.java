package com.example.converter.controller;

import com.example.converter.core.ConversionException;
import com.example.converter.core.DocumentConverter;
import com.example.converter.core.DocumentFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for document conversion operations.
 * 
 * <p>This controller provides HTTP endpoints for converting documents between
 * different formats using LibreOffice. It supports file upload/download
 * operations and various document formats including DOC, DOCX, PDF, RTF, etc.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/conversion")
@Tag(name = "Document Conversion", description = "APIs for converting documents between different formats")
public class ConversionController {

    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);

    private final DocumentConverter documentConverter;

    /**
     * Constructs a new ConversionController with the specified document converter.
     * 
     * @param documentConverter the document converter service
     */
    @Autowired
    public ConversionController(DocumentConverter documentConverter) {
        this.documentConverter = documentConverter;
    }

    /**
     * Converts an uploaded document to the specified format.
     * 
     * @param file the uploaded file to convert
     * @param outputFormat the target format for conversion
     * @return the converted document as a downloadable file
     */
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Convert uploaded document",
            description = "Converts an uploaded document to the specified output format"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversion successful",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Invalid input or unsupported format"),
            @ApiResponse(responseCode = "500", description = "Conversion failed")
    })
    public ResponseEntity<byte[]> convertDocument(
            @Parameter(description = "File to convert", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Target format for conversion", required = true)
            @RequestParam("outputFormat") String outputFormat) {

        try {
            // Validate input file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Detect input format
            DocumentFormat inputFormat = documentConverter.detectFormat(file.getOriginalFilename());
            
            // Parse output format
            Optional<DocumentFormat> outputFormatOpt = DocumentFormat.findByExtension(outputFormat);
            if (!outputFormatOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            DocumentFormat targetFormat = outputFormatOpt.get();

            // Check if conversion is supported
            if (!documentConverter.isConversionSupported(inputFormat, targetFormat)) {
                return ResponseEntity.badRequest().build();
            }

            // Convert the document
            byte[] convertedBytes = documentConverter.convertFromBytes(
                    file.getBytes(), inputFormat, targetFormat);

            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                    generateOutputFileName(file.getOriginalFilename(), targetFormat));
            headers.setContentLength(convertedBytes.length);

            logger.info("Successfully converted {} to {}", file.getOriginalFilename(), targetFormat.getExtension());

            return new ResponseEntity<>(convertedBytes, headers, HttpStatus.OK);

        } catch (ConversionException e) {
            logger.error("Conversion failed for file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error during conversion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Converts a document from one format to another using file paths.
     * 
     * @param request the conversion request containing input and output paths
     * @return the conversion result
     */
    @PostMapping("/convert-file")
    @Operation(
            summary = "Convert document by file path",
            description = "Converts a document from one format to another using file system paths"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversion successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input or file not found"),
            @ApiResponse(responseCode = "500", description = "Conversion failed")
    })
    public ResponseEntity<Map<String, Object>> convertFile(
            @Parameter(description = "Conversion request", required = true)
            @RequestBody ConversionRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate request
            if (request.getInputPath() == null || request.getOutputPath() == null) {
                response.put("success", false);
                response.put("error", "Input and output paths are required");
                return ResponseEntity.badRequest().body(response);
            }

            // Detect input format
            DocumentFormat inputFormat = documentConverter.detectFormat(request.getInputPath());
            
            // Parse output format
            Optional<DocumentFormat> outputFormatOpt = DocumentFormat.findByExtension(request.getOutputPath());
            if (!outputFormatOpt.isPresent()) {
                response.put("success", false);
                response.put("error", "Unsupported output format");
                return ResponseEntity.badRequest().body(response);
            }
            DocumentFormat outputFormat = outputFormatOpt.get();

            // Check if conversion is supported
            if (!documentConverter.isConversionSupported(inputFormat, outputFormat)) {
                response.put("success", false);
                response.put("error", "Conversion not supported between these formats");
                return ResponseEntity.badRequest().body(response);
            }

            // Perform conversion
            documentConverter.convert(request.getInputPath(), request.getOutputPath(), inputFormat, outputFormat);

            response.put("success", true);
            response.put("message", "Conversion completed successfully");
            response.put("inputFormat", inputFormat.getExtension());
            response.put("outputFormat", outputFormat.getExtension());
            response.put("outputPath", request.getOutputPath());

            logger.info("Successfully converted {} to {}", request.getInputPath(), request.getOutputPath());

            return ResponseEntity.ok(response);

        } catch (ConversionException e) {
            logger.error("Conversion failed for file: {}", request.getInputPath(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during conversion", e);
            response.put("success", false);
            response.put("error", "Unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Gets information about supported document formats.
     * 
     * @return information about supported formats
     */
    @GetMapping("/formats")
    @Operation(
            summary = "Get supported formats",
            description = "Returns information about all supported document formats"
    )
    @ApiResponse(responseCode = "200", description = "Format information retrieved successfully")
    public ResponseEntity<Map<String, Object>> getSupportedFormats() {
        Map<String, Object> response = new HashMap<>();
        
        DocumentFormat[] formats = DocumentFormat.values();
        Map<String, Object> formatInfo = new HashMap<>();
        
        for (DocumentFormat format : formats) {
            Map<String, Object> info = new HashMap<>();
            info.put("mimeType", format.getMimeType());
            info.put("extension", format.getExtension());
            info.put("filterName", format.getFilterName());
            info.put("isTextDocument", format.isTextDocument());
            info.put("isSpreadsheet", format.isSpreadsheet());
            info.put("isPresentation", format.isPresentation());
            info.put("isPdf", format.isPdf());
            
            formatInfo.put(format.name(), info);
        }
        
        response.put("formats", formatInfo);
        response.put("totalFormats", formats.length);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the conversion service.
     * 
     * @return the health status
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Checks the health status of the conversion service"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "LibreOffice Converter");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generates an output filename based on the input filename and target format.
     * 
     * @param inputFileName the input filename
     * @param outputFormat the target format
     * @return the generated output filename
     */
    private String generateOutputFileName(String inputFileName, DocumentFormat outputFormat) {
        String baseName = FilenameUtils.getBaseName(inputFileName);
        return baseName + "." + outputFormat.getExtension();
    }

    /**
     * Request class for file path conversion.
     */
    @Schema(description = "Request for file path conversion")
    public static class ConversionRequest {
        
        @Schema(description = "Path to the input file", example = "/path/to/input.docx")
        private String inputPath;
        
        @Schema(description = "Path to the output file", example = "/path/to/output.pdf")
        private String outputPath;

        /**
         * Gets the input file path.
         * 
         * @return the input path
         */
        public String getInputPath() {
            return inputPath;
        }

        /**
         * Sets the input file path.
         * 
         * @param inputPath the input path to set
         */
        public void setInputPath(String inputPath) {
            this.inputPath = inputPath;
        }

        /**
         * Gets the output file path.
         * 
         * @return the output path
         */
        public String getOutputPath() {
            return outputPath;
        }

        /**
         * Sets the output file path.
         * 
         * @param outputPath the output path to set
         */
        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }
    }
}