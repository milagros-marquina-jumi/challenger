package com.challenge.challenger.controller;

import com.challenge.challenger.exception.ExternalServiceException;
import com.challenge.challenger.model.CalculationRequest;
import com.challenge.challenger.model.CalculationResponse;
import com.challenge.challenger.service.CalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculationService calculationService;

    @Test
    void calculate_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest(10.0, 20.0);
        CalculationResponse response = new CalculationResponse(10.0, 20.0, 30.0, 10.0, 33.0, LocalDateTime.now());

        when(calculationService.calculate(any(CalculationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.num1").value(10.0))
            .andExpect(jsonPath("$.num2").value(20.0))
            .andExpect(jsonPath("$.sum").value(30.0))
            .andExpect(jsonPath("$.percentage").value(10.0))
            .andExpect(jsonPath("$.result").value(33.0))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void calculate_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"invalid\": \"json\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_ShouldReturnServiceUnavailable_WhenExternalServiceFails() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest(10.0, 20.0);

        when(calculationService.calculate(any(CalculationRequest.class)))
            .thenThrow(new ExternalServiceException("External service unavailable"));

        // Act & Assert
        mockMvc.perform(post("/api/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.message").value("External service unavailable"));
    }
}
