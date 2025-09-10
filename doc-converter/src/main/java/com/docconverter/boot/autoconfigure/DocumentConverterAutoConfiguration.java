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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.docconverter.core.DocumentConverter;
import com.docconverter.core.document.DefaultDocumentFormatRegistry;
import com.docconverter.core.document.DocumentFormatRegistry;
import com.docconverter.core.office.OfficeManager;
import com.docconverter.local.LocalDocumentConverter;
import com.docconverter.local.office.LocalOfficeManager;

/**
 * Auto-configuration for Document Converter.
 * 
 * <p>This configuration automatically sets up the document converter beans
 * when the appropriate conditions are met.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(LocalDocumentConverter.class)
@ConditionalOnProperty(prefix = "doc-converter", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DocumentConverterProperties.class)
public class DocumentConverterAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DocumentConverterAutoConfiguration.class);

    private final DocumentConverterProperties properties;

    /**
     * Creates the auto-configuration.
     *
     * @param properties The document converter properties
     */
    public DocumentConverterAutoConfiguration(DocumentConverterProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates the document format registry bean.
     *
     * @return The document format registry
     */
    @Bean
    @ConditionalOnMissingBean(DocumentFormatRegistry.class)
    public DocumentFormatRegistry documentFormatRegistry() {
        logger.info("Creating default document format registry");
        return new DefaultDocumentFormatRegistry();
    }

    /**
     * Creates the office manager bean.
     *
     * @return The office manager
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(OfficeManager.class)
    public OfficeManager officeManager() {
        logger.info("Creating local office manager with properties: {}", properties);
        
        return LocalOfficeManager.builder()
                .officeHome(properties.getOfficeHome())
                .hostName(properties.getHostName())
                .portNumbers(properties.getPortNumbers())
                .processTimeout(properties.getProcessTimeout())
                .build();
    }

    /**
     * Creates the document converter bean.
     *
     * @param officeManager The office manager
     * @param formatRegistry The document format registry
     * @return The document converter
     */
    @Bean
    @ConditionalOnMissingBean(DocumentConverter.class)
    public DocumentConverter documentConverter(OfficeManager officeManager, 
                                              DocumentFormatRegistry formatRegistry) {
        logger.info("Creating local document converter");
        return new LocalDocumentConverter(officeManager, formatRegistry);
    }
}