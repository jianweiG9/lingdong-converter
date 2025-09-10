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

import com.docconverter.core.task.OfficeTask;

/**
 * An office manager knows how to execute an {@link OfficeTask}. 
 * 
 * <p>An office manager must be started before performing conversion tasks and must be stopped 
 * once it is no longer required. Once stopped, an office manager cannot be restarted.
 * 
 * <p>The office manager is responsible for managing the lifecycle of LibreOffice processes
 * and coordinating document conversion tasks.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public interface OfficeManager {

    /**
     * Executes the specified task and blocks until the task terminates.
     *
     * @param task The task to execute, must not be null
     * @throws OfficeException If an error occurs during task execution
     * @throws IllegalArgumentException if task is null
     * @throws IllegalStateException if the manager is not running
     */
    void execute(OfficeTask task) throws OfficeException;

    /**
     * Gets whether the manager is running.
     *
     * @return {@code true} if the manager is running, {@code false} otherwise
     */
    boolean isRunning();

    /**
     * Starts the manager and initializes the LibreOffice process pool.
     *
     * @throws OfficeException If the manager cannot be started
     * @throws IllegalStateException if the manager is already running
     */
    void start() throws OfficeException;

    /**
     * Stops the manager and terminates all LibreOffice processes.
     *
     * @throws OfficeException If the manager cannot be stopped properly
     */
    void stop() throws OfficeException;
}