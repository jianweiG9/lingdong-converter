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

package com.docconverter.core;

import java.io.File;
import java.io.InputStream;

import com.docconverter.core.document.DocumentFormat;
import com.docconverter.core.document.DocumentFormatRegistry;
import com.docconverter.core.job.ConversionJobWithOptionalSourceFormatUnspecified;

/**
 * A DocumentConverter is responsible to execute the conversion of documents using an office
 * manager.
 * 
 * <p>This interface provides methods for converting documents from various sources including
 * files and input streams. The converter supports multiple document formats through
 * the {@link DocumentFormatRegistry}.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public interface DocumentConverter {

    /**
     * Converts a source file that is stored on the local file system.
     *
     * @param source The conversion input as a file, must not be null
     * @return The current conversion specification for further configuration
     * @throws IllegalArgumentException if source is null
     */
    ConversionJobWithOptionalSourceFormatUnspecified convert(File source);

    /**
     * Converts a source stream input stream.
     *
     * @param source The conversion input as an input stream, must not be null
     * @return The current conversion specification for further configuration
     * @throws IllegalArgumentException if source is null
     */
    ConversionJobWithOptionalSourceFormatUnspecified convert(InputStream source);

    /**
     * Converts a source stream input stream with optional stream closing.
     *
     * @param source The conversion input as an input stream, must not be null
     * @param closeStream Whether the {@link InputStream} is closed after the conversion terminates
     * @return The current conversion specification for further configuration
     * @throws IllegalArgumentException if source is null
     */
    ConversionJobWithOptionalSourceFormatUnspecified convert(InputStream source, boolean closeStream);

    /**
     * Gets all the {@link DocumentFormat} supported by the converter.
     *
     * @return A {@link DocumentFormatRegistry} containing the supported formats, never null
     */
    DocumentFormatRegistry getFormatRegistry();
}