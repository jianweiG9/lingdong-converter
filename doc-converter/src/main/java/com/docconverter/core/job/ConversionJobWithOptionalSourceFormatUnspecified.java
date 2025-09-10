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

package com.docconverter.core.job;

import java.io.File;
import java.io.OutputStream;

import com.docconverter.core.document.DocumentFormat;
import com.docconverter.core.office.OfficeException;

/**
 * A conversion job with an optional source format that is not yet applied to the converter.
 * 
 * <p>This interface allows specifying the source format explicitly or letting the converter
 * auto-detect it based on the file extension or content.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public interface ConversionJobWithOptionalSourceFormatUnspecified {

    /**
     * Defines the source document format for the given input document.
     *
     * @param format The document format of the source document
     * @return The current conversion specification
     * @throws IllegalArgumentException if format is null
     */
    ConversionJobWithSourceSpecified as(DocumentFormat format);

    /**
     * Configures the current conversion to write the result to the specified target file.
     * The format will be auto-detected from the file extension.
     *
     * @param target The file to which the result of the conversion will be written. 
     *     Existing files will be overwritten. If the file is locked by the JVM or any other 
     *     application or is not writable, an exception will be thrown.
     * @return A conversion job ready for execution
     * @throws IllegalArgumentException if target is null
     */
    ConversionJob to(File target);

    /**
     * Configures the current conversion to write the result to the specified {@link OutputStream}
     * with the specified format. The stream will be closed after the conversion is written.
     *
     * @param target The output stream to which the conversion result is written to
     * @param format The target document format
     * @return A conversion job ready for execution
     * @throws IllegalArgumentException if target or format is null
     */
    ConversionJob to(OutputStream target, DocumentFormat format);

    /**
     * Configures the current conversion to write the result to the specified {@link OutputStream}
     * with the specified format.
     *
     * @param target The output stream to which the conversion result is written to
     * @param format The target document format
     * @param closeStream Determines whether the output stream is closed after writing the result
     * @return A conversion job ready for execution
     * @throws IllegalArgumentException if target or format is null
     */
    ConversionJob to(OutputStream target, DocumentFormat format, boolean closeStream);
}