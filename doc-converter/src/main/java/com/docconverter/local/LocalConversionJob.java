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

package com.docconverter.local;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.docconverter.core.document.DocumentFormat;
import com.docconverter.core.document.DocumentFormatRegistry;
import com.docconverter.core.job.ConversionJob;
import com.docconverter.core.job.ConversionJobWithOptionalSourceFormatUnspecified;
import com.docconverter.core.job.ConversionJobWithSourceSpecified;
import com.docconverter.core.office.OfficeException;
import com.docconverter.core.office.OfficeManager;
import com.docconverter.core.util.AssertUtils;

/**
 * Implementation of conversion job for local document conversion.
 * 
 * <p>This class handles the conversion process including source format detection,
 * target format specification, and the actual conversion execution.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public class LocalConversionJob implements ConversionJobWithOptionalSourceFormatUnspecified, 
        ConversionJobWithSourceSpecified, ConversionJob {

    private static final Logger logger = LoggerFactory.getLogger(LocalConversionJob.class);

    private final OfficeManager officeManager;
    private final DocumentFormatRegistry formatRegistry;
    private final Object source;
    private final boolean closeInputStream;
    private DocumentFormat sourceFormat;
    private Object target;
    private DocumentFormat targetFormat;
    private boolean closeOutputStream = true;

    /**
     * Creates a conversion job with a file source.
     */
    public LocalConversionJob(OfficeManager officeManager, DocumentFormatRegistry formatRegistry, File source) {
        this.officeManager = officeManager;
        this.formatRegistry = formatRegistry;
        this.source = source;
        this.closeInputStream = false;
    }

    /**
     * Creates a conversion job with an InputStream source.
     */
    public LocalConversionJob(OfficeManager officeManager, DocumentFormatRegistry formatRegistry, 
                             InputStream source, boolean closeInputStream) {
        this.officeManager = officeManager;
        this.formatRegistry = formatRegistry;
        this.source = source;
        this.closeInputStream = closeInputStream;
    }

    @Override
    public ConversionJobWithSourceSpecified as(DocumentFormat format) {
        AssertUtils.notNull(format, "format must not be null");
        this.sourceFormat = format;
        return this;
    }

    @Override
    public ConversionJob to(File target) {
        AssertUtils.notNull(target, "target must not be null");
        this.target = target;
        
        // Auto-detect target format from file extension
        String extension = getFileExtension(target.getName());
        if (extension != null) {
            this.targetFormat = formatRegistry.getFormatByExtension(extension);
        }
        
        if (this.targetFormat == null) {
            throw new IllegalArgumentException("Cannot determine target format from file extension: " + extension);
        }
        
        return this;
    }

    @Override
    public ConversionJob to(OutputStream target, DocumentFormat format) {
        return to(target, format, true);
    }

    @Override
    public ConversionJob to(OutputStream target, DocumentFormat format, boolean closeStream) {
        AssertUtils.notNull(target, "target must not be null");
        AssertUtils.notNull(format, "format must not be null");
        this.target = target;
        this.targetFormat = format;
        this.closeOutputStream = closeStream;
        return this;
    }

    @Override
    public void execute() throws OfficeException {
        logger.info("Starting document conversion");
        
        try {
            // Auto-detect source format if not specified
            if (sourceFormat == null) {
                sourceFormat = detectSourceFormat();
            }
            
            if (sourceFormat == null) {
                throw new OfficeException("Cannot determine source document format");
            }
            
            logger.info("Converting from {} to {}", sourceFormat.getName(), targetFormat.getName());
            
            // For now, implement a simple file-based conversion
            // In a full implementation, this would use LibreOffice UNO API
            performConversion();
            
            logger.info("Document conversion completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during document conversion", e);
            throw new OfficeException("Conversion failed: " + e.getMessage(), e);
        }
    }

    private DocumentFormat detectSourceFormat() {
        if (source instanceof File) {
            File file = (File) source;
            String extension = getFileExtension(file.getName());
            if (extension != null) {
                return formatRegistry.getFormatByExtension(extension);
            }
        }
        // For InputStream, we would need to analyze the content
        // For now, return null to indicate format detection failed
        return null;
    }

    private void performConversion() throws IOException, OfficeException {
        // This is a placeholder implementation
        // In a real implementation, this would:
        // 1. Create temporary files if needed
        // 2. Use LibreOffice UNO API to load the document
        // 3. Convert to target format
        // 4. Save to target location
        // 5. Clean up temporary files
        
        if (source instanceof File && target instanceof File) {
            // Simple file to file conversion (placeholder)
            Files.copy(((File) source).toPath(), ((File) target).toPath());
        } else if (source instanceof InputStream && target instanceof OutputStream) {
            // Stream to stream conversion (placeholder)
            try (InputStream in = (InputStream) source;
                 OutputStream out = (OutputStream) target) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                if (closeOutputStream) {
                    out.close();
                }
            } finally {
                if (closeInputStream && source instanceof InputStream) {
                    ((InputStream) source).close();
                }
            }
        } else {
            // Mixed source/target types - create temporary files
            Path tempSourceFile = null;
            Path tempTargetFile = null;
            
            try {
                // Create temporary source file if needed
                if (source instanceof InputStream) {
                    tempSourceFile = Files.createTempFile("doc-converter-src", "." + sourceFormat.getExtension());
                    try (InputStream in = (InputStream) source;
                         OutputStream out = Files.newOutputStream(tempSourceFile)) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        if (closeInputStream) {
                            ((InputStream) source).close();
                        }
                    }
                }
                
                // Create temporary target file if needed
                if (target instanceof OutputStream) {
                    tempTargetFile = Files.createTempFile("doc-converter-tgt", "." + targetFormat.getExtension());
                }
                
                // Perform actual conversion (placeholder)
                File sourceFile = source instanceof File ? (File) source : tempSourceFile.toFile();
                File targetFile = target instanceof File ? (File) target : tempTargetFile.toFile();
                
                // TODO: Implement actual LibreOffice conversion here
                Files.copy(sourceFile.toPath(), targetFile.toPath());
                
                // Copy result to output stream if needed
                if (target instanceof OutputStream) {
                    try (InputStream in = Files.newInputStream(tempTargetFile);
                         OutputStream out = (OutputStream) target) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        
                        if (closeOutputStream) {
                            out.close();
                        }
                    }
                }
                
            } finally {
                // Clean up temporary files
                if (tempSourceFile != null) {
                    try {
                        Files.deleteIfExists(tempSourceFile);
                    } catch (IOException e) {
                        logger.warn("Failed to delete temporary source file: " + tempSourceFile, e);
                    }
                }
                if (tempTargetFile != null) {
                    try {
                        Files.deleteIfExists(tempTargetFile);
                    } catch (IOException e) {
                        logger.warn("Failed to delete temporary target file: " + tempTargetFile, e);
                    }
                }
            }
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }
}