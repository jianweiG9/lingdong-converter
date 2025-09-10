package com.example.converter.core;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration of supported document formats for conversion.
 * 
 * <p>This enum defines all the document formats that can be handled by the
 * LibreOffice converter, including their MIME types, file extensions,
 * and LibreOffice filter names.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DocumentFormat {

    /**
     * Microsoft Word 97-2003 Document format.
     */
    DOC("application/msword", "doc", "MS Word 97"),

    /**
     * Microsoft Word 2007+ Document format.
     */
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx", "Office Open XML Text"),

    /**
     * Portable Document Format.
     */
    PDF("application/pdf", "pdf", "writer_pdf_Export"),

    /**
     * Rich Text Format.
     */
    RTF("application/rtf", "rtf", "Rich Text Format"),

    /**
     * OpenDocument Text format.
     */
    ODT("application/vnd.oasis.opendocument.text", "odt", "writer8"),

    /**
     * Plain Text format.
     */
    TXT("text/plain", "txt", "Text (encoded)"),

    /**
     * HyperText Markup Language.
     */
    HTML("text/html", "html", "HTML (StarWriter)"),

    /**
     * Microsoft Excel 97-2003 Workbook format.
     */
    XLS("application/vnd.ms-excel", "xls", "MS Excel 97"),

    /**
     * Microsoft Excel 2007+ Workbook format.
     */
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", "Calc Office Open XML"),

    /**
     * OpenDocument Spreadsheet format.
     */
    ODS("application/vnd.oasis.opendocument.spreadsheet", "ods", "calc8"),

    /**
     * Microsoft PowerPoint 97-2003 Presentation format.
     */
    PPT("application/vnd.ms-powerpoint", "ppt", "MS PowerPoint 97"),

    /**
     * Microsoft PowerPoint 2007+ Presentation format.
     */
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx", "Impress MS PowerPoint 2007 XML");

    private final String mimeType;
    private final String extension;
    private final String filterName;

    /**
     * Constructs a DocumentFormat with the specified properties.
     * 
     * @param mimeType the MIME type of the format
     * @param extension the file extension of the format
     * @param filterName the LibreOffice filter name for the format
     */
    DocumentFormat(String mimeType, String extension, String filterName) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.filterName = filterName;
    }

    /**
     * Gets the MIME type of this document format.
     * 
     * @return the MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the file extension of this document format.
     * 
     * @return the file extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Gets the LibreOffice filter name for this document format.
     * 
     * @return the filter name
     */
    public String getFilterName() {
        return filterName;
    }

    /**
     * Finds a DocumentFormat by its file extension.
     * 
     * @param extension the file extension to search for
     * @return an Optional containing the DocumentFormat if found
     */
    public static Optional<DocumentFormat> findByExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }
        
        String lowerExtension = extension.toLowerCase();
        if (lowerExtension.startsWith(".")) {
            lowerExtension = lowerExtension.substring(1);
        }
        
        final String finalExtension = lowerExtension;
        return Arrays.stream(values())
                .filter(format -> format.getExtension().equals(finalExtension))
                .findFirst();
    }

    /**
     * Finds a DocumentFormat by its MIME type.
     * 
     * @param mimeType the MIME type to search for
     * @return an Optional containing the DocumentFormat if found
     */
    public static Optional<DocumentFormat> findByMimeType(String mimeType) {
        if (mimeType == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(values())
                .filter(format -> format.getMimeType().equals(mimeType))
                .findFirst();
    }

    /**
     * Checks if this format is a text document format.
     * 
     * @return true if this is a text document format
     */
    public boolean isTextDocument() {
        return this == DOC || this == DOCX || this == RTF || this == ODT || this == TXT || this == HTML;
    }

    /**
     * Checks if this format is a spreadsheet format.
     * 
     * @return true if this is a spreadsheet format
     */
    public boolean isSpreadsheet() {
        return this == XLS || this == XLSX || this == ODS;
    }

    /**
     * Checks if this format is a presentation format.
     * 
     * @return true if this is a presentation format
     */
    public boolean isPresentation() {
        return this == PPT || this == PPTX;
    }

    /**
     * Checks if this format is a PDF format.
     * 
     * @return true if this is a PDF format
     */
    public boolean isPdf() {
        return this == PDF;
    }
}