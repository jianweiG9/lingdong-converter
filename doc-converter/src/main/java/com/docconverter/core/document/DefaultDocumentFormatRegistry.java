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

package com.docconverter.core.document;

import java.util.*;
import java.util.stream.Collectors;

import com.docconverter.core.util.AssertUtils;

/**
 * Default implementation of {@link DocumentFormatRegistry} that contains common document formats
 * supported by LibreOffice.
 * 
 * <p>This registry provides built-in support for popular document formats including:
 * <ul>
 *   <li>Text documents: DOC, DOCX, ODT, RTF, TXT, PDF</li>
 *   <li>Spreadsheets: XLS, XLSX, ODS, CSV</li>
 *   <li>Presentations: PPT, PPTX, ODP</li>
 *   <li>Images: PNG, JPG, GIF, SVG</li>
 * </ul>
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public class DefaultDocumentFormatRegistry implements DocumentFormatRegistry {

    private final Map<String, DocumentFormat> formatsByExtension = new HashMap<>();
    private final Map<String, DocumentFormat> formatsByMediaType = new HashMap<>();
    private final Map<DocumentFamily, Set<DocumentFormat>> formatsByFamily = new EnumMap<>(DocumentFamily.class);

    /**
     * Creates a new registry with default document formats.
     */
    public DefaultDocumentFormatRegistry() {
        initializeDefaultFormats();
    }

    private void initializeDefaultFormats() {
        // Text formats
        addFormat(createTextFormat("Microsoft Word", "doc", "application/msword"));
        addFormat(createTextFormat("Microsoft Word 2007-2019", "docx", 
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        addFormat(createTextFormat("OpenDocument Text", "odt", "application/vnd.oasis.opendocument.text"));
        addFormat(createTextFormat("Rich Text Format", "rtf", "application/rtf"));
        addFormat(createTextFormat("Plain Text", "txt", "text/plain"));
        addFormat(createPdfFormat());

        // Spreadsheet formats
        addFormat(createSpreadsheetFormat("Microsoft Excel", "xls", "application/vnd.ms-excel"));
        addFormat(createSpreadsheetFormat("Microsoft Excel 2007-2019", "xlsx", 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        addFormat(createSpreadsheetFormat("OpenDocument Spreadsheet", "ods", 
                "application/vnd.oasis.opendocument.spreadsheet"));
        addFormat(createSpreadsheetFormat("Comma Separated Values", "csv", "text/csv"));

        // Presentation formats
        addFormat(createPresentationFormat("Microsoft PowerPoint", "ppt", "application/vnd.ms-powerpoint"));
        addFormat(createPresentationFormat("Microsoft PowerPoint 2007-2019", "pptx", 
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        addFormat(createPresentationFormat("OpenDocument Presentation", "odp", 
                "application/vnd.oasis.opendocument.presentation"));

        // Drawing formats
        addFormat(createDrawingFormat("Portable Network Graphics", "png", "image/png"));
        addFormat(createDrawingFormat("JPEG Image", "jpg", "image/jpeg"));
        addFormat(createDrawingFormat("Graphics Interchange Format", "gif", "image/gif"));
        addFormat(createDrawingFormat("Scalable Vector Graphics", "svg", "image/svg+xml"));
    }

    private DocumentFormat createTextFormat(String name, String extension, String mediaType) {
        return DocumentFormat.builder()
                .name(name)
                .extension(extension)
                .mediaType(mediaType)
                .inputFamily(DocumentFamily.TEXT)
                .build();
    }

    private DocumentFormat createSpreadsheetFormat(String name, String extension, String mediaType) {
        return DocumentFormat.builder()
                .name(name)
                .extension(extension)
                .mediaType(mediaType)
                .inputFamily(DocumentFamily.SPREADSHEET)
                .build();
    }

    private DocumentFormat createPresentationFormat(String name, String extension, String mediaType) {
        return DocumentFormat.builder()
                .name(name)
                .extension(extension)
                .mediaType(mediaType)
                .inputFamily(DocumentFamily.PRESENTATION)
                .build();
    }

    private DocumentFormat createDrawingFormat(String name, String extension, String mediaType) {
        return DocumentFormat.builder()
                .name(name)
                .extension(extension)
                .mediaType(mediaType)
                .inputFamily(DocumentFamily.DRAWING)
                .build();
    }

    private DocumentFormat createPdfFormat() {
        return DocumentFormat.builder()
                .name("Portable Document Format")
                .extension("pdf")
                .mediaType("application/pdf")
                .inputFamily(DocumentFamily.TEXT)
                .storeProperty(DocumentFamily.TEXT, "FilterName", "writer_pdf_Export")
                .storeProperty(DocumentFamily.SPREADSHEET, "FilterName", "calc_pdf_Export")
                .storeProperty(DocumentFamily.PRESENTATION, "FilterName", "impress_pdf_Export")
                .storeProperty(DocumentFamily.DRAWING, "FilterName", "draw_pdf_Export")
                .build();
    }

    private void addFormat(DocumentFormat format) {
        for (String extension : format.getExtensions()) {
            formatsByExtension.put(extension.toLowerCase(), format);
        }
        formatsByMediaType.put(format.getMediaType(), format);
        
        if (format.getInputFamily() != null) {
            formatsByFamily.computeIfAbsent(format.getInputFamily(), k -> new HashSet<>()).add(format);
        }
    }

    @Override
    public DocumentFormat getFormatByExtension(String extension) {
        AssertUtils.notBlank(extension, "extension must not be null nor blank");
        return formatsByExtension.get(extension.toLowerCase());
    }

    @Override
    public DocumentFormat getFormatByMediaType(String mediaType) {
        AssertUtils.notBlank(mediaType, "mediaType must not be null nor blank");
        return formatsByMediaType.get(mediaType);
    }

    @Override
    public Set<DocumentFormat> getOutputFormats(DocumentFamily family) {
        AssertUtils.notNull(family, "family must not be null");
        return formatsByFamily.getOrDefault(family, Collections.emptySet());
    }

    /**
     * Gets all document formats in this registry.
     *
     * @return A set of all document formats
     */
    public Set<DocumentFormat> getAllFormats() {
        return new HashSet<>(formatsByExtension.values());
    }

    /**
     * Gets all supported file extensions.
     *
     * @return A set of all supported file extensions (without the dot)
     */
    public Set<String> getSupportedExtensions() {
        return new HashSet<>(formatsByExtension.keySet());
    }
}