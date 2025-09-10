package com.example.converter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the document converter.
 * 
 * <p>This class holds all configuration properties related to LibreOffice
 * document conversion, including office home path, port numbers, and
 * process management settings.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "converter")
public class ConverterProperties {

    /**
     * LibreOffice installation home directory path.
     */
    private String officeHome = "/usr/lib/libreoffice";

    /**
     * Port numbers for LibreOffice processes.
     */
    private int[] portNumbers = {8100, 8101, 8102, 8103, 8104};

    /**
     * Maximum number of tasks per process.
     */
    private int maxTasksPerProcess = 100;

    /**
     * Process timeout in milliseconds.
     */
    private long processTimeout = 120000L;

    /**
     * Retry timeout in milliseconds.
     */
    private long retryTimeout = 30000L;

    /**
     * Whether to kill existing processes on startup.
     */
    private boolean killExistingProcess = true;

    /**
     * Gets the LibreOffice office home directory path.
     * 
     * @return the office home path
     */
    public String getOfficeHome() {
        return officeHome;
    }

    /**
     * Sets the LibreOffice office home directory path.
     * 
     * @param officeHome the office home path to set
     */
    public void setOfficeHome(String officeHome) {
        this.officeHome = officeHome;
    }

    /**
     * Gets the port numbers for LibreOffice processes.
     * 
     * @return array of port numbers
     */
    public int[] getPortNumbers() {
        return portNumbers;
    }

    /**
     * Sets the port numbers for LibreOffice processes.
     * 
     * @param portNumbers array of port numbers to set
     */
    public void setPortNumbers(int[] portNumbers) {
        this.portNumbers = portNumbers;
    }

    /**
     * Gets the maximum number of tasks per process.
     * 
     * @return the maximum tasks per process
     */
    public int getMaxTasksPerProcess() {
        return maxTasksPerProcess;
    }

    /**
     * Sets the maximum number of tasks per process.
     * 
     * @param maxTasksPerProcess the maximum tasks per process to set
     */
    public void setMaxTasksPerProcess(int maxTasksPerProcess) {
        this.maxTasksPerProcess = maxTasksPerProcess;
    }

    /**
     * Gets the process timeout in milliseconds.
     * 
     * @return the process timeout
     */
    public long getProcessTimeout() {
        return processTimeout;
    }

    /**
     * Sets the process timeout in milliseconds.
     * 
     * @param processTimeout the process timeout to set
     */
    public void setProcessTimeout(long processTimeout) {
        this.processTimeout = processTimeout;
    }

    /**
     * Gets the retry timeout in milliseconds.
     * 
     * @return the retry timeout
     */
    public long getRetryTimeout() {
        return retryTimeout;
    }

    /**
     * Sets the retry timeout in milliseconds.
     * 
     * @param retryTimeout the retry timeout to set
     */
    public void setRetryTimeout(long retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    /**
     * Checks if existing processes should be killed on startup.
     * 
     * @return true if existing processes should be killed
     */
    public boolean isKillExistingProcess() {
        return killExistingProcess;
    }

    /**
     * Sets whether existing processes should be killed on startup.
     * 
     * @param killExistingProcess true to kill existing processes
     */
    public void setKillExistingProcess(boolean killExistingProcess) {
        this.killExistingProcess = killExistingProcess;
    }
}