package org.example.service.fragment;

import org.example.service.subscriber.SubscriberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@DataJpaTest
class FragmentGeneratorTest {

    @Mock
    private FragmentServiceImpl cdrService;

    @Mock
    private SubscriberServiceImpl subscriberService;

    @InjectMocks
    private FragmentGenerator fragmentGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void generateCDRsForYear_ShouldGenerateCDRsForAllSubscribers() {
//        Subscriber subscriber1 = new Subscriber();
//        subscriber1.setMsisdn("79992221122");
//
//        Subscriber subscriber2 = new Subscriber();
//        subscriber2.setMsisdn("79993331133");
//
//        when(subscriberService.fetchSubscriberList()).thenReturn(List.of(subscriber1, subscriber2));
//        when(cdrService.saveCDR(any(Fragment.class))).thenReturn(new Fragment());
//
//        fragmentGenerator.generateCDRsForYear();
//
//        verify(cdrService, atLeast(2)).saveCDR(any(Fragment.class));
//    }

//    @Test
//    void generateCDRForSubscriber_ShouldSaveCDR() {
//        Subscriber subscriber1 = new Subscriber();
//        subscriber1.setMsisdn("79992221122");
//
//        Subscriber subscriber2 = new Subscriber();
//        subscriber2.setMsisdn("79993331133");
//
//        when(subscriberService.fetchSubscriberList()).thenReturn(List.of(subscriber1, subscriber2));
//
//        LocalDateTime startTime = LocalDateTime.now();
//        when(cdrService.saveCDR(any(Fragment.class))).thenReturn(new Fragment());
//
//        fragmentGenerator.generateCDRForSubscriber(subscriber1, startTime);
//
//        verify(cdrService, times(1)).saveCDR(any(Fragment.class));
//    }

}