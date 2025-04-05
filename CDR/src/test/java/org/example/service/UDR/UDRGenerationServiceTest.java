package org.example.service.UDR;

import org.example.dto.UDR;
import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.service.CDR.CDRServiceImpl;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UDRGenerationServiceTest {

    @Mock
    private CDRServiceImpl cdrService;

    @Mock
    private SubscriberServiceImpl subscriberService;

    @InjectMocks
    private UDRGenerationService udrGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateUDRForSubscriber_ShouldReturnUDR() {
        String msisdn = "79992221122";
        CDR cdr1 = createCDR("01", msisdn, "79993331133", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        CDR cdr2 = createCDR("02", "79993331133", msisdn, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        when(cdrService.fetchCDRListByMsisdn(msisdn, msisdn)).thenReturn(Arrays.asList(cdr1, cdr2));

        UDR udr = udrGenerationService.generateUDRForSubscriber(msisdn);

        assertNotNull(udr);
        assertEquals(msisdn, udr.getMsisdn());
        assertEquals("00:05:00", udr.getOutcomingCall().getTotalTime());
        assertEquals("00:10:00", udr.getIncomingCall().getTotalTime());
        verify(cdrService, times(1)).fetchCDRListByMsisdn(msisdn, msisdn);
    }

    @Test
    void generateUDRForSubscriber_WithMonth_ShouldReturnUDR() {
        String msisdn = "79992221122";
        YearMonth month = YearMonth.of(2025, 2);
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        CDR cdr1 = createCDR("01", msisdn, "79993331133", startOfMonth.plusDays(1), startOfMonth.plusDays(1).plusMinutes(5));
        CDR cdr2 = createCDR("02", "79993331133", msisdn, startOfMonth.plusDays(2), startOfMonth.plusDays(2).plusMinutes(10));

        when(cdrService.fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth))
                .thenReturn(Arrays.asList(cdr1, cdr2));

        UDR udr = udrGenerationService.generateUDRForSubscriber(msisdn, month);

        assertNotNull(udr);
        assertEquals(msisdn, udr.getMsisdn());
        assertEquals("00:05:00", udr.getOutcomingCall().getTotalTime());
        assertEquals("00:10:00", udr.getIncomingCall().getTotalTime());
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth);
    }

    @Test
    void generateUDRForAllSubscribers_ShouldReturnUDRMap() {
        // Arrange
        YearMonth month = YearMonth.of(2025, 2);
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("79992221122");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("79993331133");

        CDR cdr1 = createCDR("01", "79992221122", "79993331133", startOfMonth.plusDays(1), startOfMonth.plusDays(1).plusMinutes(5));
        CDR cdr2 = createCDR("02", "79992221122", "79993331133", startOfMonth.plusDays(2), startOfMonth.plusDays(2).plusMinutes(10));

        when(subscriberService.fetchSubscriberList()).thenReturn(Arrays.asList(subscriber1, subscriber2));
        when(cdrService.fetchCDRListByMsisdnAndTime("79992221122", startOfMonth, endOfMonth))
                .thenReturn(List.of(cdr1, cdr2));
        when(cdrService.fetchCDRListByMsisdnAndTime("79993331133", startOfMonth, endOfMonth))
                .thenReturn(List.of(cdr1, cdr2));

        Map<String, UDR> udrMap = udrGenerationService.generateUDRForAllSubscribers(month);

        assertNotNull(udrMap);
        assertEquals(2, udrMap.size());

        UDR udr1 = udrMap.get("79992221122");
        assertNotNull(udr1);
        assertEquals("00:05:00", udr1.getOutcomingCall().getTotalTime());

        UDR udr2 = udrMap.get("79993331133");
        assertNotNull(udr2);
        assertEquals("00:10:00", udr2.getIncomingCall().getTotalTime());

        verify(subscriberService, times(1)).fetchSubscriberList();
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime("79992221122", startOfMonth, endOfMonth);
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime("79993331133", startOfMonth, endOfMonth);
    }

    @Test
    void generateUDRForSubscriber_ShouldHandleEmptyCDRList() {
        String msisdn = "79992221122";
        when(cdrService.fetchCDRListByMsisdn(msisdn, msisdn)).thenReturn(Collections.emptyList());

        UDR udr = udrGenerationService.generateUDRForSubscriber(msisdn);

        assertNotNull(udr);
        assertEquals(msisdn, udr.getMsisdn());
        assertEquals("00:00:00", udr.getOutcomingCall().getTotalTime());
        assertEquals("00:00:00", udr.getIncomingCall().getTotalTime());
        verify(cdrService, times(1)).fetchCDRListByMsisdn(msisdn, msisdn);
    }

    @Test
    void generateUDRForSubscriber_WithMonth_ShouldHandleEmptyCDRList() {
        String msisdn = "79992221122";
        YearMonth month = YearMonth.of(2025, 2);
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        when(cdrService.fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth))
                .thenReturn(Collections.emptyList());

        UDR udr = udrGenerationService.generateUDRForSubscriber(msisdn, month);

        assertNotNull(udr);
        assertEquals(msisdn, udr.getMsisdn());
        assertEquals("00:00:00", udr.getOutcomingCall().getTotalTime());
        assertEquals("00:00:00", udr.getIncomingCall().getTotalTime());
        verify(cdrService, times(1)).fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth);
    }

    @Test
    void generateUDRForAllSubscribers_ShouldHandleEmptySubscriberList() {
        YearMonth month = YearMonth.of(2025, 2);
        when(subscriberService.fetchSubscriberList()).thenReturn(Collections.emptyList());

        Map<String, UDR> udrMap = udrGenerationService.generateUDRForAllSubscribers(month);

        assertNotNull(udrMap);
        assertTrue(udrMap.isEmpty());
        verify(subscriberService, times(1)).fetchSubscriberList();
    }

    private CDR createCDR(String callType, String callerMsisdn, String receiverMsisdn, LocalDateTime startTime, LocalDateTime endTime) {
        CDR cdr = new CDR();
        cdr.setCallType(callType);
        cdr.setCallerMsisdn(callerMsisdn);
        cdr.setReceiverMsisdn(receiverMsisdn);
        cdr.setStartTime(startTime);
        cdr.setEndTime(endTime);
        return cdr;
    }
}