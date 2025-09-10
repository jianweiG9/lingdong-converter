package com.example.converter.core;

/**
 * Exception thrown when document conversion fails.
 * 
 * <p>This exception is thrown when any error occurs during the document
 * conversion process, including file I/O errors, LibreOffice process
 * failures, or unsupported format errors.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConversionException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConversionException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConversionException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ConversionException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }
}