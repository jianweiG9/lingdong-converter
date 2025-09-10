package com.example.converter.core;

import com.example.converter.config.ConverterProperties;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Core document converter that handles file format conversions using LibreOffice.
 * 
 * <p>This class provides the main functionality for converting documents between
 * different formats using LibreOffice's headless mode. It supports various
 * document formats including DOC, DOCX, PDF, RTF, and others.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class DocumentConverter {

    private static final Logger logger = LoggerFactory.getLogger(DocumentConverter.class);

    private final LibreOfficeManager libreOfficeManager;
    private final ConverterProperties properties;

    /**
     * Constructs a new DocumentConverter with the specified dependencies.
     * 
     * @param libreOfficeManager the LibreOffice manager instance
     * @param properties the converter configuration properties
     */
    @Autowired
    public DocumentConverter(LibreOfficeManager libreOfficeManager, ConverterProperties properties) {
        this.libreOfficeManager = libreOfficeManager;
        this.properties = properties;
    }

    /**
     * Converts a document from one format to another.
     * 
     * @param inputFile the input file to convert
     * @param outputFile the output file for the converted document
     * @param inputFormat the input document format
     * @param outputFormat the output document format
     * @throws ConversionException if the conversion fails
     */
    public void convert(File inputFile, File outputFile, DocumentFormat inputFormat, DocumentFormat outputFormat) 
            throws ConversionException {
        
        if (inputFile == null || !inputFile.exists()) {
            throw new ConversionException("Input file does not exist: " + inputFile);
        }
        
        if (outputFile == null) {
            throw new ConversionException("Output file cannot be null");
        }
        
        // Ensure output directory exists
        File outputDir = outputFile.getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        logger.info("Converting {} to {}: {} -> {}", 
                inputFormat.getExtension(), outputFormat.getExtension(), 
                inputFile.getName(), outputFile.getName());
        
        try {
            // Start LibreOffice manager if not already started
            if (!libreOfficeManager.isStarted()) {
                libreOfficeManager.start();
            }
            
            // Perform the conversion
            performConversion(inputFile, outputFile, inputFormat, outputFormat);
            
            // Verify the output file was created
            if (!outputFile.exists() || outputFile.length() == 0) {
                throw new ConversionException("Conversion failed: output file was not created or is empty");
            }
            
            logger.info("Conversion completed successfully: {} -> {}", 
                    inputFile.getName(), outputFile.getName());
            
        } catch (Exception e) {
            logger.error("Conversion failed: {} -> {}", inputFile.getName(), outputFile.getName(), e);
            throw new ConversionException("Conversion failed: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a document using file paths.
     * 
     * @param inputPath the path to the input file
     * @param outputPath the path to the output file
     * @param inputFormat the input document format
     * @param outputFormat the output document format
     * @throws ConversionException if the conversion fails
     */
    public void convert(String inputPath, String outputPath, DocumentFormat inputFormat, DocumentFormat outputFormat) 
            throws ConversionException {
        convert(new File(inputPath), new File(outputPath), inputFormat, outputFormat);
    }

    /**
     * Converts a document and returns the output as a byte array.
     * 
     * @param inputFile the input file to convert
     * @param inputFormat the input document format
     * @param outputFormat the output document format
     * @return the converted document as a byte array
     * @throws ConversionException if the conversion fails
     */
    public byte[] convertToBytes(File inputFile, DocumentFormat inputFormat, DocumentFormat outputFormat) 
            throws ConversionException {
        
        try {
            // Create a temporary output file
            Path tempFile = Files.createTempFile("conversion_", "." + outputFormat.getExtension());
            
            try {
                // Perform the conversion
                convert(inputFile, tempFile.toFile(), inputFormat, outputFormat);
                
                // Read the converted file into a byte array
                return Files.readAllBytes(tempFile);
                
            } finally {
                // Clean up the temporary file
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            throw new ConversionException("Failed to convert document to bytes", e);
        }
    }

    /**
     * Converts a document from bytes to another format.
     * 
     * @param inputBytes the input document as bytes
     * @param inputFormat the input document format
     * @param outputFormat the output document format
     * @return the converted document as a byte array
     * @throws ConversionException if the conversion fails
     */
    public byte[] convertFromBytes(byte[] inputBytes, DocumentFormat inputFormat, DocumentFormat outputFormat) 
            throws ConversionException {
        
        try {
            // Create a temporary input file
            Path tempInputFile = Files.createTempFile("input_", "." + inputFormat.getExtension());
            Path tempOutputFile = Files.createTempFile("output_", "." + outputFormat.getExtension());
            
            try {
                // Write input bytes to temporary file
                Files.write(tempInputFile, inputBytes);
                
                // Perform the conversion
                convert(tempInputFile.toFile(), tempOutputFile.toFile(), inputFormat, outputFormat);
                
                // Read the converted file into a byte array
                return Files.readAllBytes(tempOutputFile);
                
            } finally {
                // Clean up temporary files
                Files.deleteIfExists(tempInputFile);
                Files.deleteIfExists(tempOutputFile);
            }
            
        } catch (IOException e) {
            throw new ConversionException("Failed to convert document from bytes", e);
        }
    }

    /**
     * Performs the actual document conversion using LibreOffice.
     * 
     * @param inputFile the input file
     * @param outputFile the output file
     * @param inputFormat the input format
     * @param outputFormat the output format
     * @throws ConversionException if the conversion fails
     */
    private void performConversion(File inputFile, File outputFile, DocumentFormat inputFormat, DocumentFormat outputFormat) 
            throws ConversionException {
        
        try {
            // Build LibreOffice command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(buildLibreOfficeCommand(inputFile, outputFile, outputFormat));
            processBuilder.directory(new File(libreOfficeManager.getOfficeHome()));
            
            // Set environment variables
            processBuilder.environment().put("HOME", System.getProperty("user.home"));
            
            logger.debug("Executing LibreOffice command: {}", String.join(" ", processBuilder.command()));
            
            // Start the process
            Process process = processBuilder.start();
            
            // Wait for the process to complete
            boolean finished = process.waitFor(properties.getProcessTimeout(), TimeUnit.MILLISECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                throw new ConversionException("LibreOffice conversion timed out after " + properties.getProcessTimeout() + "ms");
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                // Read error output
                String errorOutput = readProcessOutput(process.getErrorStream());
                throw new ConversionException("LibreOffice conversion failed with exit code " + exitCode + 
                        ". Error: " + errorOutput);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConversionException("Conversion was interrupted", e);
        } catch (IOException e) {
            throw new ConversionException("Failed to execute LibreOffice conversion", e);
        }
    }

    /**
     * Builds the LibreOffice command for document conversion.
     * 
     * @param inputFile the input file
     * @param outputFile the output file
     * @param outputFormat the output format
     * @return array of command arguments
     */
    private String[] buildLibreOfficeCommand(File inputFile, File outputFile, DocumentFormat outputFormat) {
        String executable = isWindows() ? "soffice.exe" : "soffice";
        String executablePath = Paths.get(libreOfficeManager.getOfficeHome(), "program", executable).toString();
        
        return new String[]{
                executablePath,
                "--headless",
                "--invisible",
                "--nodefault",
                "--nolockcheck",
                "--nologo",
                "--norestore",
                "--convert-to", outputFormat.getFilterName(),
                "--outdir", outputFile.getParent(),
                inputFile.getAbsolutePath()
        };
    }

    /**
     * Reads the output from a process stream.
     * 
     * @param inputStream the process input stream
     * @return the output as a string
     */
    private String readProcessOutput(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (IOException e) {
            logger.warn("Failed to read process output", e);
            return "Unable to read error output";
        }
    }

    /**
     * Checks if the current system is Windows.
     * 
     * @return true if the system is Windows
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * Detects the document format from a file extension.
     * 
     * @param fileName the file name
     * @return the detected document format
     * @throws ConversionException if the format cannot be detected
     */
    public DocumentFormat detectFormat(String fileName) throws ConversionException {
        String extension = FilenameUtils.getExtension(fileName);
        Optional<DocumentFormat> format = DocumentFormat.findByExtension(extension);
        
        if (!format.isPresent()) {
            throw new ConversionException("Unsupported file format: " + extension);
        }
        
        return format.get();
    }

    /**
     * Checks if a conversion between two formats is supported.
     * 
     * @param inputFormat the input format
     * @param outputFormat the output format
     * @return true if the conversion is supported
     */
    public boolean isConversionSupported(DocumentFormat inputFormat, DocumentFormat outputFormat) {
        // Basic validation - both formats must be supported
        if (inputFormat == null || outputFormat == null) {
            return false;
        }
        
        // Same format conversion is not needed
        if (inputFormat == outputFormat) {
            return false;
        }
        
        // LibreOffice can handle most conversions between supported formats
        return true;
    }
}