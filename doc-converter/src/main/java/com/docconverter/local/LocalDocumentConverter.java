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

import java.io.File;
import java.io.InputStream;

import com.docconverter.core.DocumentConverter;
import com.docconverter.core.document.DocumentFormatRegistry;
import com.docconverter.core.job.ConversionJobWithOptionalSourceFormatUnspecified;
import com.docconverter.core.office.OfficeManager;

/**
 * Local implementation of DocumentConverter using LibreOffice.
 * 
 * <p>This implementation uses a local LibreOffice installation to perform document conversions.
 * It manages LibreOffice processes and coordinates conversion tasks.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public class LocalDocumentConverter implements DocumentConverter {

    private final OfficeManager officeManager;
    private final DocumentFormatRegistry formatRegistry;

    /**
     * Creates a new LocalDocumentConverter.
     *
     * @param officeManager The office manager to use for conversions
     * @param formatRegistry The document format registry
     */
    public LocalDocumentConverter(OfficeManager officeManager, DocumentFormatRegistry formatRegistry) {
        this.officeManager = officeManager;
        this.formatRegistry = formatRegistry;
    }

    @Override
    public ConversionJobWithOptionalSourceFormatUnspecified convert(File source) {
        return new LocalConversionJob(officeManager, formatRegistry, source);
    }

    @Override
    public ConversionJobWithOptionalSourceFormatUnspecified convert(InputStream source) {
        return new LocalConversionJob(officeManager, formatRegistry, source, false);
    }

    @Override
    public ConversionJobWithOptionalSourceFormatUnspecified convert(InputStream source, boolean closeStream) {
        return new LocalConversionJob(officeManager, formatRegistry, source, closeStream);
    }

    @Override
    public DocumentFormatRegistry getFormatRegistry() {
        return formatRegistry;
    }
}