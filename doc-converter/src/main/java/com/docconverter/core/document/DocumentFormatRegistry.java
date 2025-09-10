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

import java.util.Set;

/**
 * A class implementing this interface should keep a collection of document format supported by
 * LibreOffice.
 * 
 * <p>The registry provides methods to lookup document formats by file extension or media type,
 * and to retrieve all supported output formats for a given document family.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public interface DocumentFormatRegistry {

    /**
     * Gets a document format for the specified extension.
     *
     * @param extension The extension whose document format will be returned (without the dot)
     * @return The found document format, or {@code null} if no document format exists for the
     *     specified extension
     * @throws IllegalArgumentException if extension is null or blank
     */
    DocumentFormat getFormatByExtension(String extension);

    /**
     * Gets a document format for the specified media type.
     *
     * @param mediaType The media type whose document format will be returned
     * @return The found document format, or {@code null} if no document format exists for the
     *     specified media type
     * @throws IllegalArgumentException if mediaType is null or blank
     */
    DocumentFormat getFormatByMediaType(String mediaType);

    /**
     * Gets all the {@link DocumentFormat}s of a given family.
     *
     * @param family The family whose document formats will be returned
     * @return A set with all the document formats for the specified family, never null
     * @throws IllegalArgumentException if family is null
     */
    Set<DocumentFormat> getOutputFormats(DocumentFamily family);
}