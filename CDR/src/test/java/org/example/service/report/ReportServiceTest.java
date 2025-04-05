package org.example.service.report;

import org.example.entity.CDR;
import org.example.service.CDR.CDRServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ReportServiceTest {

    @Mock
    private CDRServiceImpl cdrService;

    @InjectMocks
    private ReportGenerationService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateCDRReport_UUID_CallerExists() throws Exception {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setCallerMsisdn(msisdn);
        cdr.setReceiverMsisdn("79993331133");
        cdr.setStartTime(startDate);
        cdr.setEndTime(endDate);

        when(cdrService.fetchCDRListByMsisdnAndTime(msisdn, startDate, endDate)).thenReturn(List.of(cdr));

        UUID requestId = reportService.generateCDRReport(msisdn, startDate, endDate);

        assertNotNull(requestId);
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime(msisdn, startDate, endDate);
    }

    @Test
    void generateCDRReport_UUID_ReceiverExists() throws Exception {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setCallerMsisdn("79993331133");
        cdr.setReceiverMsisdn(msisdn);
        cdr.setStartTime(startDate);
        cdr.setEndTime(endDate);
        cdrService.saveCDR(cdr);

        when(cdrService.fetchCDRListByMsisdnAndTime(msisdn, startDate, endDate)).thenReturn(List.of(cdr));

        UUID requestId = reportService.generateCDRReport(msisdn, startDate, endDate);

        assertNotNull(requestId);
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime(msisdn, startDate, endDate);
    }

    @Test
    void generateCDRReport_Exception_NoData() {
        String msisdn = "79992221123";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        when(cdrService.fetchCDRListByMsisdnAndTime(msisdn, startDate, endDate)).thenReturn(Collections.emptyList());

        assertThrows(Exception.class, () -> reportService.generateCDRReport(msisdn, startDate, endDate));
    }
}
