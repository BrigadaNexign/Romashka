package org.example.service.CDR;

import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.repository.CDRRepository;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CDRServiceImplTest {

    @Mock
    private CDRRepository cdrRepository;

    @Mock
    private SubscriberServiceImpl subscriberService;

    @InjectMocks
    private CDRServiceImpl cdrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCDR() {
        CDR cdr = new CDR();
        cdr.setCallerMsisdn("79992221122");
        cdr.setReceiverMsisdn("79993331133");
        cdr.setStartTime(LocalDateTime.now());
        cdr.setEndTime(LocalDateTime.now().plusMinutes(5));

        when(cdrRepository.save(cdr)).thenReturn(cdr);

        CDR savedCDR = cdrService.saveCDR(cdr);

        assertNotNull(savedCDR);
        assertEquals(cdr.getCallerMsisdn(), savedCDR.getCallerMsisdn());
        verify(cdrRepository, times(1)).save(cdr);
    }

    @Test
    void testFetchCDRList() {
        CDR cdr1 = new CDR();
        cdr1.setCallerMsisdn("79992221122");
        cdr1.setReceiverMsisdn("79993331133");

        CDR cdr2 = new CDR();
        cdr2.setCallerMsisdn("79994441144");
        cdr2.setReceiverMsisdn("79995551155");

        when(cdrRepository.findAll()).thenReturn(Arrays.asList(cdr1, cdr2));

        List<CDR> cdrList = cdrService.fetchCDRList();

        assertEquals(2, cdrList.size());
        verify(cdrRepository, times(1)).findAll();
    }

    @Test
    void testDeleteCDRByID() {
        Long cdrId = 1L;

        doNothing().when(cdrRepository).deleteById(cdrId);

        cdrService.deleteCDRByID(cdrId);

        verify(cdrRepository, times(1)).deleteById(cdrId);
    }

    @Test
    void testFetchCDRListByMsisdn() {
        String callerMsisdn = "79992221122";
        String receiverMsisdn = "79993331133";

        CDR cdr = new CDR();
        cdr.setCallerMsisdn(callerMsisdn);
        cdr.setReceiverMsisdn(receiverMsisdn);

        when(cdrRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn))
                .thenReturn(Collections.singletonList(cdr));

        List<CDR> cdrList = cdrService.fetchCDRListByMsisdn(callerMsisdn, receiverMsisdn);

        assertEquals(1, cdrList.size());
        assertEquals(callerMsisdn, cdrList.get(0).getCallerMsisdn());
        verify(cdrRepository, times(1)).findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn);
    }

    @Test
    void testInitializeData() {
        cdrService.initializeData();

        verify(subscriberService, times(10)).saveSubscriber(any(Subscriber.class));
        verify(cdrRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testSaveAllCDRs() {
        CDR cdr1 = new CDR();
        cdr1.setCallerMsisdn("79992221122");
        cdr1.setReceiverMsisdn("79993331133");

        CDR cdr2 = new CDR();
        cdr2.setCallerMsisdn("79994441144");
        cdr2.setReceiverMsisdn("79995551155");

        List<CDR> cdrs = Arrays.asList(cdr1, cdr2);

        when(cdrRepository.saveAll(cdrs)).thenReturn(cdrs);

        List<CDR> savedCDRs = cdrService.saveAllCDRs(cdrs);

        assertEquals(2, savedCDRs.size());
        verify(cdrRepository, times(1)).saveAll(cdrs);
    }

    @Test
    void testFetchCDRListByMsisdnAndTime() {
        String msisdn = "79992221122";
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
        LocalDateTime endOfMonth = LocalDateTime.now();

        CDR cdr = new CDR();
        cdr.setCallerMsisdn(msisdn);
        cdr.setReceiverMsisdn("79993331133");
        cdr.setStartTime(startOfMonth.plusDays(1));

        when(cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(msisdn, startOfMonth, endOfMonth))
                .thenReturn(Collections.singletonList(cdr));

        List<CDR> cdrList = cdrService.fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth);

        assertEquals(1, cdrList.size());
        assertEquals(msisdn, cdrList.get(0).getCallerMsisdn());
        verify(cdrRepository, times(1))
                .findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(msisdn, startOfMonth, endOfMonth);
    }
}