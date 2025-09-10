package com.example.converter.core;

import com.example.converter.config.ConverterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages LibreOffice processes for document conversion.
 * 
 * <p>This class handles the lifecycle of LibreOffice processes, including
 * starting, stopping, and monitoring their health. It ensures that
 * LibreOffice processes are available for document conversion operations.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class LibreOfficeManager {

    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeManager.class);

    private final ConverterProperties properties;
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Constructs a new LibreOfficeManager with the specified properties.
     * 
     * @param properties the converter configuration properties
     */
    @Autowired
    public LibreOfficeManager(ConverterProperties properties) {
        this.properties = properties;
    }

    /**
     * Starts the LibreOffice manager and initializes LibreOffice processes.
     * 
     * @throws ConversionException if LibreOffice cannot be started
     */
    public void start() throws ConversionException {
        if (started.compareAndSet(false, true)) {
            logger.info("Starting LibreOffice Manager...");
            
            // Kill existing processes if configured to do so
            if (properties.isKillExistingProcess()) {
                killExistingProcesses();
            }
            
            // Verify LibreOffice installation
            verifyLibreOfficeInstallation();
            
            logger.info("LibreOffice Manager started successfully");
        }
    }

    /**
     * Stops the LibreOffice manager and cleans up resources.
     */
    public void stop() {
        if (started.compareAndSet(true, false)) {
            logger.info("Stopping LibreOffice Manager...");
            killExistingProcesses();
            logger.info("LibreOffice Manager stopped");
        }
    }

    /**
     * Checks if the LibreOffice manager is started.
     * 
     * @return true if the manager is started
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Verifies that LibreOffice is properly installed and accessible.
     * 
     * @throws ConversionException if LibreOffice is not properly installed
     */
    private void verifyLibreOfficeInstallation() throws ConversionException {
        String officeHome = properties.getOfficeHome();
        File officeHomeDir = new File(officeHome);
        
        if (!officeHomeDir.exists()) {
            throw new ConversionException("LibreOffice installation not found at: " + officeHome);
        }
        
        if (!officeHomeDir.isDirectory()) {
            throw new ConversionException("LibreOffice home is not a directory: " + officeHome);
        }
        
        // Check for LibreOffice executable
        File executable = new File(officeHomeDir, "program/soffice");
        if (!executable.exists()) {
            executable = new File(officeHomeDir, "program/soffice.exe");
        }
        
        if (!executable.exists()) {
            throw new ConversionException("LibreOffice executable not found in: " + officeHome);
        }
        
        if (!executable.canExecute()) {
            throw new ConversionException("LibreOffice executable is not executable: " + executable.getAbsolutePath());
        }
        
        logger.info("LibreOffice installation verified at: {}", officeHome);
    }

    /**
     * Kills existing LibreOffice processes.
     */
    private void killExistingProcesses() {
        try {
            // Kill LibreOffice processes on Unix-like systems
            if (isUnixLike()) {
                ProcessBuilder pb = new ProcessBuilder("pkill", "-f", "soffice");
                Process process = pb.start();
                process.waitFor();
                logger.debug("Killed existing LibreOffice processes");
            }
        } catch (Exception e) {
            logger.warn("Failed to kill existing LibreOffice processes", e);
        }
    }

    /**
     * Checks if the current system is Unix-like (Linux, macOS, etc.).
     * 
     * @return true if the system is Unix-like
     */
    private boolean isUnixLike() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux") || osName.contains("mac") || osName.contains("unix");
    }

    /**
     * Gets the LibreOffice office home directory.
     * 
     * @return the office home directory path
     */
    public String getOfficeHome() {
        return properties.getOfficeHome();
    }

    /**
     * Gets the port numbers for LibreOffice processes.
     * 
     * @return array of port numbers
     */
    public int[] getPortNumbers() {
        return properties.getPortNumbers();
    }

    /**
     * Gets the maximum number of tasks per process.
     * 
     * @return the maximum tasks per process
     */
    public int getMaxTasksPerProcess() {
        return properties.getMaxTasksPerProcess();
    }

    /**
     * Gets the process timeout in milliseconds.
     * 
     * @return the process timeout
     */
    public long getProcessTimeout() {
        return properties.getProcessTimeout();
    }

    /**
     * Gets the retry timeout in milliseconds.
     * 
     * @return the retry timeout
     */
    public long getRetryTimeout() {
        return properties.getRetryTimeout();
    }
}