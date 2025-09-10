package com.example.converter.controller;

import com.example.converter.core.DocumentConverter;
import com.example.converter.core.DocumentFormat;
import com.example.converter.core.ConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ConversionController class.
 * 
 * <p>These tests verify the REST API endpoints for document conversion
 * using mocked dependencies to isolate the controller logic.</p>
 * 
 * @author Generated
 * @version 1.0.0
 * @since 1.0.0
 */
@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentConverter documentConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws ConversionException {
        // Setup common mock behaviors
        when(documentConverter.detectFormat(anyString())).thenReturn(DocumentFormat.DOC);
        when(documentConverter.isConversionSupported(any(DocumentFormat.class), any(DocumentFormat.class)))
                .thenReturn(true);
        when(documentConverter.convertFromBytes(any(byte[].class), any(DocumentFormat.class), any(DocumentFormat.class)))
                .thenReturn("converted content".getBytes());
    }

    @Test
    void testConvertDocument_Success() throws Exception {
        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.doc", "application/msword", "Hello, World!".getBytes());

        // Perform the request
        mockMvc.perform(multipart("/api/v1/conversion/convert")
                .file(file)
                .param("outputFormat", "pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void testConvertDocument_EmptyFile() throws Exception {
        // Create an empty mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.doc", "application/msword", new byte[0]);

        // Perform the request
        mockMvc.perform(multipart("/api/v1/conversion/convert")
                .file(file)
                .param("outputFormat", "pdf"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConvertDocument_UnsupportedFormat() throws Exception {
        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.doc", "application/msword", "Hello, World!".getBytes());

        // Perform the request with unsupported format
        mockMvc.perform(multipart("/api/v1/conversion/convert")
                .file(file)
                .param("outputFormat", "xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConvertDocument_ConversionException() throws Exception {
        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.doc", "application/msword", "Hello, World!".getBytes());

        // Mock conversion exception
        when(documentConverter.convertFromBytes(any(byte[].class), any(DocumentFormat.class), any(DocumentFormat.class)))
                .thenThrow(new ConversionException("Conversion failed"));

        // Perform the request
        mockMvc.perform(multipart("/api/v1/conversion/convert")
                .file(file)
                .param("outputFormat", "pdf"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testConvertFile_Success() throws Exception {
        // Create conversion request
        ConversionController.ConversionRequest request = new ConversionController.ConversionRequest();
        request.setInputPath("/path/to/input.doc");
        request.setOutputPath("/path/to/output.pdf");

        // Perform the request
        mockMvc.perform(post("/api/v1/conversion/convert-file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Conversion completed successfully"))
                .andExpect(jsonPath("$.inputFormat").value("doc"))
                .andExpect(jsonPath("$.outputFormat").value("pdf"));
    }

    @Test
    void testConvertFile_MissingPaths() throws Exception {
        // Create conversion request with missing paths
        ConversionController.ConversionRequest request = new ConversionController.ConversionRequest();

        // Perform the request
        mockMvc.perform(post("/api/v1/conversion/convert-file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Input and output paths are required"));
    }

    @Test
    void testConvertFile_ConversionException() throws Exception {
        // Create conversion request
        ConversionController.ConversionRequest request = new ConversionController.ConversionRequest();
        request.setInputPath("/path/to/input.doc");
        request.setOutputPath("/path/to/output.pdf");

        // Mock conversion exception
        doThrow(new ConversionException("Conversion failed"))
                .when(documentConverter).convert(anyString(), anyString(), any(DocumentFormat.class), any(DocumentFormat.class));

        // Perform the request
        mockMvc.perform(post("/api/v1/conversion/convert-file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Conversion failed"));
    }

    @Test
    void testGetSupportedFormats() throws Exception {
        // Perform the request
        mockMvc.perform(get("/api/v1/conversion/formats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFormats").exists())
                .andExpect(jsonPath("$.formats").exists())
                .andExpect(jsonPath("$.formats.DOC").exists())
                .andExpect(jsonPath("$.formats.DOCX").exists())
                .andExpect(jsonPath("$.formats.PDF").exists());
    }

    @Test
    void testHealthCheck() throws Exception {
        // Perform the request
        mockMvc.perform(get("/api/v1/conversion/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("LibreOffice Converter"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}