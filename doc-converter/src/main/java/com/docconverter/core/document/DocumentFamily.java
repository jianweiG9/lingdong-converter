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

/**
 * Represents a document type supported by LibreOffice.
 * 
 * <p>Document families are used to categorize different types of documents
 * based on their primary purpose and the LibreOffice application that handles them.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public enum DocumentFamily {

    /** 
     * Text documents (odt, doc, docx, rtf, etc.) 
     * Handled by LibreOffice Writer
     */
    TEXT,

    /** 
     * Web documents (html, etc.) 
     * Handled by LibreOffice Writer in web mode
     */
    WEB,

    /** 
     * Spreadsheet documents (ods, xls, xlsx, csv, etc.) 
     * Handled by LibreOffice Calc
     */
    SPREADSHEET,

    /** 
     * Presentation documents (odp, ppt, pptx, etc.) 
     * Handled by LibreOffice Impress
     */
    PRESENTATION,

    /** 
     * Drawing documents (odg, png, svg, etc.) 
     * Handled by LibreOffice Draw
     */
    DRAWING
}