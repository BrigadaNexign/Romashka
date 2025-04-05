package org.example.service.CDR;

import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@DataJpaTest
class CDRGeneratorServiceTest {

    @Mock
    private CDRServiceImpl cdrService;

    @Mock
    private SubscriberServiceImpl subscriberService;

    @InjectMocks
    private CDRGeneratorService cdrGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateCDRsForYear_ShouldGenerateCDRsForAllSubscribers() {
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("79992221122");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("79993331133");

        when(subscriberService.fetchSubscriberList()).thenReturn(List.of(subscriber1, subscriber2));
        when(cdrService.saveCDR(any(CDR.class))).thenReturn(new CDR());

        cdrGeneratorService.generateCDRsForYear();

        verify(cdrService, atLeast(2)).saveCDR(any(CDR.class));
    }

    @Test
    void generateCDRForSubscriber_ShouldSaveCDR() {
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("79992221122");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("79993331133");

        when(subscriberService.fetchSubscriberList()).thenReturn(List.of(subscriber1, subscriber2));

        LocalDateTime startTime = LocalDateTime.now();
        when(cdrService.saveCDR(any(CDR.class))).thenReturn(new CDR());

        cdrGeneratorService.generateCDRForSubscriber(subscriber1, startTime);

        verify(cdrService, times(1)).saveCDR(any(CDR.class));
    }

    @Test
    void getRandomReceiverMsisdn_ShouldReturnDifferentMsisdn() {
        String callerMsisdn = "79992221122";
        Subscriber receiver1 = new Subscriber();
        receiver1.setMsisdn("79993331133");

        Subscriber receiver2 = new Subscriber();
        receiver2.setMsisdn("79994441144");

        when(subscriberService.fetchSubscriberList()).thenReturn(List.of(receiver1, receiver2));

        String result = cdrGeneratorService.getRandomReceiverMsisdn(callerMsisdn);

        assertNotNull(result);
        assertNotEquals(callerMsisdn, result);
    }

    @Test
    void getRandomReceiverMsisdn_ShouldHandleSingleSubscriber() {
        String callerMsisdn = "79992221122";
        Subscriber receiver = new Subscriber();
        String receiverMsisdn = "79993331133";
        receiver.setMsisdn(receiverMsisdn);

        when(subscriberService.fetchSubscriberList()).thenReturn(Collections.singletonList(receiver));

        assertEquals(receiverMsisdn, cdrGeneratorService.getRandomReceiverMsisdn(callerMsisdn));
    }

    @Test
    void getRandomReceiverMsisdn_ShouldNotHandleSingleSubscriber() {
        String callerMsisdn = "79992221122";
        Subscriber receiver = new Subscriber();
        String receiverMsisdn = "79992221122";
        receiver.setMsisdn(receiverMsisdn);

        subscriberService.saveSubscriber(receiver);

        when(subscriberService.fetchSubscriberList()).thenReturn(Collections.singletonList(receiver));

        assertThrows(IllegalStateException.class, () -> cdrGeneratorService.getRandomReceiverMsisdn(callerMsisdn));
    }
}