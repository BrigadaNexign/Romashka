package org.example.service.fragment;

import org.example.entity.Fragment;
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

class FragmentServiceImplTest {

    @Mock
    private CDRRepository cdrRepository;

    @Mock
    private SubscriberServiceImpl subscriberService;

    @InjectMocks
    private FragmentServiceImpl cdrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCDR() {
        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn("79992221122");
        fragment.setReceiverMsisdn("79993331133");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(5));

        when(cdrRepository.save(fragment)).thenReturn(fragment);

        Fragment savedFragment = cdrService.saveCDR(fragment);

        assertNotNull(savedFragment);
        assertEquals(fragment.getCallerMsisdn(), savedFragment.getCallerMsisdn());
        verify(cdrRepository, times(1)).save(fragment);
    }

    @Test
    void testFetchCDRList() {
        Fragment fragment1 = new Fragment();
        fragment1.setCallerMsisdn("79992221122");
        fragment1.setReceiverMsisdn("79993331133");

        Fragment fragment2 = new Fragment();
        fragment2.setCallerMsisdn("79994441144");
        fragment2.setReceiverMsisdn("79995551155");

        when(cdrRepository.findAll()).thenReturn(Arrays.asList(fragment1, fragment2));

        List<Fragment> fragmentList = cdrService.fetchCDRList();

        assertEquals(2, fragmentList.size());
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

        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn(callerMsisdn);
        fragment.setReceiverMsisdn(receiverMsisdn);

        when(cdrRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn))
                .thenReturn(Collections.singletonList(fragment));

        List<Fragment> fragmentList = cdrService.fetchCDRListByMsisdn(callerMsisdn, receiverMsisdn);

        assertEquals(1, fragmentList.size());
        assertEquals(callerMsisdn, fragmentList.get(0).getCallerMsisdn());
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
        Fragment fragment1 = new Fragment();
        fragment1.setCallerMsisdn("79992221122");
        fragment1.setReceiverMsisdn("79993331133");

        Fragment fragment2 = new Fragment();
        fragment2.setCallerMsisdn("79994441144");
        fragment2.setReceiverMsisdn("79995551155");

        List<Fragment> fragments = Arrays.asList(fragment1, fragment2);

        when(cdrRepository.saveAll(fragments)).thenReturn(fragments);

        List<Fragment> savedFragments = cdrService.saveAllCDRs(fragments);

        assertEquals(2, savedFragments.size());
        verify(cdrRepository, times(1)).saveAll(fragments);
    }

    @Test
    void testFetchCDRListByMsisdnAndTime() {
        String msisdn = "79992221122";
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
        LocalDateTime endOfMonth = LocalDateTime.now();

        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn(msisdn);
        fragment.setReceiverMsisdn("79993331133");
        fragment.setStartTime(startOfMonth.plusDays(1));

        when(cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(msisdn, startOfMonth, endOfMonth))
                .thenReturn(Collections.singletonList(fragment));

        List<Fragment> fragmentList = cdrService.fetchCDRListByMsisdnAndTime(msisdn, startOfMonth, endOfMonth);

        assertEquals(1, fragmentList.size());
        assertEquals(msisdn, fragmentList.get(0).getCallerMsisdn());
        verify(cdrRepository, times(1))
                .findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(msisdn, startOfMonth, endOfMonth);
    }
}