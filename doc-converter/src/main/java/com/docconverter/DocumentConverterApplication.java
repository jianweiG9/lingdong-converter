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

package com.docconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Document Converter.
 * 
 * <p>This application provides a REST API for converting documents using LibreOffice.
 * It supports various document formats including DOC, DOCX, PDF, XLS, XLSX, PPT, PPTX, etc.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.docconverter")
public class DocumentConverterApplication {

    /**
     * Main method to start the Document Converter application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DocumentConverterApplication.class, args);
    }
}