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

package com.docconverter.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Document Converter.
 * 
 * <p>This class contains all configuration options for the document converter,
 * including LibreOffice process management, timeouts, and conversion settings.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
@ConfigurationProperties("doc-converter")
public class DocumentConverterProperties {

    /** Enable Document Converter, which means that LibreOffice instances will be launched. */
    private boolean enabled = true;

    /**
     * Represents the LibreOffice home directory. If not set, the LibreOffice installation directory is
     * auto-detected, the most recent version of LibreOffice first.
     */
    private String officeHome;

    /**
     * Host name that will be used in the --accept argument when starting a LibreOffice process. 
     * Most of the time, the default will work. But if it doesn't work (unable to connect to the started
     * process), using 'localhost' as the host name instead may work.
     */
    private String hostName = "127.0.0.1";

    /**
     * List of ports used by each Document Converter processing thread. The number of
     * LibreOffice instances is equal to the number of port numbers, since 1 LibreOffice process will
     * be launched for each port number.
     */
    private int[] portNumbers = {2002, 2003};

    /**
     * Directory where temporary LibreOffice profiles will be created. If not set, it defaults to the
     * system temporary directory as specified by the java.io.tmpdir system property.
     */
    private String workingDir;

    /**
     * Template profile directory to copy to a created LibreOffice profile directory when a LibreOffice
     * process is launched.
     */
    private String templateProfileDir;

    /**
     * Process timeout (milliseconds). Used when trying to execute a LibreOffice process call
     * (start/connect/terminate).
     */
    private long processTimeout = 120000L; // 2 minutes

    /**
     * Process retry interval (milliseconds). Used for waiting between LibreOffice process call tries
     * (start/connect/terminate).
     */
    private long processRetryInterval = 250L;

    /** 
     * Specifies the delay after an attempt to start a LibreOffice process before doing anything else.
     */
    private long afterStartProcessDelay = 2000L;

    /** 
     * Indicates whether the manager will start fail fast. If set to true, any error while
     * starting the manager will cause the manager to fail immediately. 
     */
    private boolean startFailFast = false;

    /** 
     * Indicates whether the LibreOffice processes will be kept alive on shutdown.
     */
    private boolean keepAliveOnShutdown = false;

    /** 
     * Task queue timeout (milliseconds). The maximum time a task will wait for an available
     * LibreOffice process before timing out.
     */
    private long taskQueueTimeout = 30000L; // 30 seconds

    /** 
     * Task execution timeout (milliseconds). The maximum time allowed for a single conversion task.
     */
    private long taskExecutionTimeout = 120000L; // 2 minutes

    /** 
     * The maximum number of tasks a LibreOffice process will execute before being restarted.
     */
    private int maxTasksPerProcess = 200;

    // Getters and setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOfficeHome() {
        return officeHome;
    }

    public void setOfficeHome(String officeHome) {
        this.officeHome = officeHome;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int[] getPortNumbers() {
        return portNumbers;
    }

    public void setPortNumbers(int[] portNumbers) {
        this.portNumbers = portNumbers;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getTemplateProfileDir() {
        return templateProfileDir;
    }

    public void setTemplateProfileDir(String templateProfileDir) {
        this.templateProfileDir = templateProfileDir;
    }

    public long getProcessTimeout() {
        return processTimeout;
    }

    public void setProcessTimeout(long processTimeout) {
        this.processTimeout = processTimeout;
    }

    public long getProcessRetryInterval() {
        return processRetryInterval;
    }

    public void setProcessRetryInterval(long processRetryInterval) {
        this.processRetryInterval = processRetryInterval;
    }

    public long getAfterStartProcessDelay() {
        return afterStartProcessDelay;
    }

    public void setAfterStartProcessDelay(long afterStartProcessDelay) {
        this.afterStartProcessDelay = afterStartProcessDelay;
    }

    public boolean isStartFailFast() {
        return startFailFast;
    }

    public void setStartFailFast(boolean startFailFast) {
        this.startFailFast = startFailFast;
    }

    public boolean isKeepAliveOnShutdown() {
        return keepAliveOnShutdown;
    }

    public void setKeepAliveOnShutdown(boolean keepAliveOnShutdown) {
        this.keepAliveOnShutdown = keepAliveOnShutdown;
    }

    public long getTaskQueueTimeout() {
        return taskQueueTimeout;
    }

    public void setTaskQueueTimeout(long taskQueueTimeout) {
        this.taskQueueTimeout = taskQueueTimeout;
    }

    public long getTaskExecutionTimeout() {
        return taskExecutionTimeout;
    }

    public void setTaskExecutionTimeout(long taskExecutionTimeout) {
        this.taskExecutionTimeout = taskExecutionTimeout;
    }

    public int getMaxTasksPerProcess() {
        return maxTasksPerProcess;
    }

    public void setMaxTasksPerProcess(int maxTasksPerProcess) {
        this.maxTasksPerProcess = maxTasksPerProcess;
    }
}