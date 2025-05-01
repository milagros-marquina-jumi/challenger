package com.challenge.challenger.service;

import com.challenge.challenger.exception.ExternalServiceException;
import com.challenge.challenger.model.CalculationRequest;
import com.challenge.challenger.model.CalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {
    
    @Mock
    private ExternalPercentageService externalPercentageService;
    
    @InjectMocks
    private CalculationService calculationService;
    
    private CalculationRequest request;
    
    @BeforeEach
    void setUp() {
        request = new CalculationRequest(10.0, 20.0);
    }
    
    @Test
    void calculate_ShouldReturnCorrectResult_WhenExternalServiceSucceeds() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenReturn(10.0);
        
        // Act
        CalculationResponse response = calculationService.calculate(request);
        
        // Assert
        assertEquals(10.0, response.num1());
        assertEquals(20.0, response.num2());
        assertEquals(30.0, response.sum());
        assertEquals(10.0, response.percentage());
        assertEquals(33.0, response.result()); // 30 + (30 * 10 / 100)
        assertNotNull(response.timestamp());
        
        verify(externalPercentageService, times(1)).getPercentage();
    }
    
    @Test
    void calculate_ShouldThrowExternalServiceException_WhenExternalServiceFails() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenThrow(new RuntimeException("External service failed"));
        
        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> calculationService.calculate(request));
        
        verify(externalPercentageService, times(1)).getPercentage();
    }
    
    @Test
    void getPercentage_ShouldReturnPercentage_WhenExternalServiceSucceeds() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenReturn(10.0);
        
        // Act
        double percentage = calculationService.getPercentage();
        
        // Assert
        assertEquals(10.0, percentage);
        
        verify(externalPercentageService, times(1)).getPercentage();
    }
    
    @Test
    void getPercentage_ShouldThrowExternalServiceException_WhenExternalServiceFails() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenThrow(new RuntimeException("External service failed"));
        
        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> calculationService.getPercentage());
        
        verify(externalPercentageService, times(1)).getPercentage();
    }
}
