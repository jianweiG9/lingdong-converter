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

package com.docconverter.core.office;

/**
 * An exception that provides information on an error while dealing with office operations.
 * 
 * <p>This exception is thrown when LibreOffice processes encounter errors during document
 * conversion operations, process management, or communication failures.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public class OfficeException extends Exception {
    
    private static final long serialVersionUID = -1360754252407765922L;

    /**
     * Constructs a new office exception with the specified detail message. 
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     */
    public OfficeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new office exception with the specified detail message and cause.
     *
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *     (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *     unknown.)
     */
    public OfficeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}