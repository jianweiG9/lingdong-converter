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

package com.docconverter.local.office;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.docconverter.core.office.OfficeException;
import com.docconverter.core.office.OfficeManager;
import com.docconverter.core.task.OfficeTask;

/**
 * Simple implementation of OfficeManager for local LibreOffice processes.
 * 
 * <p>This is a basic implementation that manages LibreOffice processes for document conversion.
 * In a full implementation, this would handle process pools, connection management, and recovery.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public class LocalOfficeManager implements OfficeManager {

    private static final Logger logger = LoggerFactory.getLogger(LocalOfficeManager.class);

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final String officeHome;
    private final String hostName;
    private final int[] portNumbers;
    private final long processTimeout;

    /**
     * Creates a new LocalOfficeManager with the specified configuration.
     */
    public LocalOfficeManager(String officeHome, String hostName, int[] portNumbers, long processTimeout) {
        this.officeHome = officeHome;
        this.hostName = hostName;
        this.portNumbers = portNumbers != null ? portNumbers.clone() : new int[]{2002};
        this.processTimeout = processTimeout;
    }

    @Override
    public void execute(OfficeTask task) throws OfficeException {
        if (!running.get()) {
            throw new IllegalStateException("OfficeManager is not running");
        }

        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }

        logger.debug("Executing office task: {}", task.getClass().getSimpleName());

        // Create a simple office context
        LocalOfficeContext context = new LocalOfficeContext();
        
        try {
            task.execute(context);
            logger.debug("Office task completed successfully");
        } catch (Exception e) {
            logger.error("Error executing office task", e);
            throw new OfficeException("Failed to execute office task", e);
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void start() throws OfficeException {
        if (running.compareAndSet(false, true)) {
            logger.info("Starting LocalOfficeManager");
            
            try {
                // In a full implementation, this would:
                // 1. Detect LibreOffice installation
                // 2. Start LibreOffice processes
                // 3. Establish connections
                // 4. Set up process pools
                
                // For now, just log the startup
                logger.info("LibreOffice office home: {}", officeHome != null ? officeHome : "auto-detected");
                logger.info("Host name: {}", hostName);
                logger.info("Port numbers: {}", java.util.Arrays.toString(portNumbers));
                logger.info("Process timeout: {} ms", processTimeout);
                
                logger.info("LocalOfficeManager started successfully");
                
            } catch (Exception e) {
                running.set(false);
                throw new OfficeException("Failed to start LocalOfficeManager", e);
            }
        } else {
            throw new IllegalStateException("OfficeManager is already running");
        }
    }

    @Override
    public void stop() throws OfficeException {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping LocalOfficeManager");
            
            try {
                // In a full implementation, this would:
                // 1. Stop all conversion tasks
                // 2. Terminate LibreOffice processes
                // 3. Clean up resources
                
                logger.info("LocalOfficeManager stopped successfully");
                
            } catch (Exception e) {
                logger.error("Error stopping LocalOfficeManager", e);
                throw new OfficeException("Failed to stop LocalOfficeManager", e);
            }
        }
    }

    /**
     * Builder for creating LocalOfficeManager instances.
     */
    public static class Builder {
        private String officeHome;
        private String hostName = "127.0.0.1";
        private int[] portNumbers = {2002};
        private long processTimeout = 120000L;

        public Builder officeHome(String officeHome) {
            this.officeHome = officeHome;
            return this;
        }

        public Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public Builder portNumbers(int... portNumbers) {
            this.portNumbers = portNumbers;
            return this;
        }

        public Builder processTimeout(long processTimeout) {
            this.processTimeout = processTimeout;
            return this;
        }

        public LocalOfficeManager build() {
            return new LocalOfficeManager(officeHome, hostName, portNumbers, processTimeout);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}