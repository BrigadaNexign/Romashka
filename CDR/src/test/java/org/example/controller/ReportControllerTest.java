package org.example.controller;

import org.example.service.report.ReportGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ReportControllerTest {

    @Mock
    private ReportGenerationService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateReport_UUID_DataExists() throws Exception {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        UUID requestId = UUID.randomUUID();

        when(reportService.generateCDRReport(msisdn, startDate, endDate)).thenReturn(requestId);

        ResponseEntity<String> response = reportController.generateReport(msisdn, startDate, endDate);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains(requestId.toString()));
    }

    @Test
    void generateReport_Error_WhenException() throws Exception {
        String msisdn = "79992221123";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        when(reportService.generateCDRReport(msisdn, startDate, endDate))
                .thenThrow(new IllegalArgumentException("No CDR records found"));

        ResponseEntity<String> response = reportController.generateReport(msisdn, startDate, endDate);

        assertEquals(400, response.getStatusCodeValue());
         assertTrue(response.getBody().contains("Error"));
    }
}
