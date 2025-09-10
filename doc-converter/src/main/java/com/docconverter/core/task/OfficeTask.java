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

package com.docconverter.core.task;

import com.docconverter.core.office.OfficeContext;
import com.docconverter.core.office.OfficeException;

/**
 * Represents a task executed by an {@link com.docconverter.core.office.OfficeManager}.
 *
 * <p>Office tasks encapsulate operations that need to be performed using LibreOffice
 * services, such as document conversion, manipulation, or information extraction.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 * @see OfficeContext
 */
public interface OfficeTask {

    /**
     * Executes the task in the specified context.
     *
     * @param context The office context providing access to LibreOffice services
     * @throws OfficeException If an error occurs during task execution
     * @throws IllegalArgumentException if context is null
     */
    void execute(OfficeContext context) throws OfficeException;
}