package org.example.service.subscriber;

import org.example.entity.Subscriber;
import org.example.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriberServiceImplTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private SubscriberServiceImpl subscriberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveSubscriber_ShouldReturnSavedSubscriber() {
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn("79992221122");

        when(subscriberRepository.save(subscriber)).thenReturn(subscriber);

        Subscriber savedSubscriber = subscriberService.saveSubscriber(subscriber);

        assertNotNull(savedSubscriber);
        assertEquals("79992221122", savedSubscriber.getMsisdn());
        verify(subscriberRepository, times(1)).save(subscriber);
    }

    @Test
    void fetchSubscriberList_ShouldReturnAllSubscribers() {
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("79992221122");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("79993331133");

        when(subscriberRepository.findAll()).thenReturn(Arrays.asList(subscriber1, subscriber2));

        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();

        assertEquals(2, subscribers.size());
        verify(subscriberRepository, times(1)).findAll();
    }

    @Test
    void deleteSubscriberByID_ShouldDeleteSubscriber() {
        String msisdn = "79992221122";

        subscriberService.deleteSubscriberByID(msisdn);

        verify(subscriberRepository, times(1)).deleteById(msisdn);
    }

    @Test
    void fetchSubscriberList_ShouldReturnEmptyList_WhenNoSubscribersExist() {
        when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());

        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();

        assertTrue(subscribers.isEmpty());
        verify(subscriberRepository, times(1)).findAll();
    }

    @Test
    void saveSubscriber_ShouldHandleNullInput() {
        when(subscriberRepository.save(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> subscriberService.saveSubscriber(null));
    }

    @Test
    void deleteSubscriberByID_ShouldHandleNonExistentSubscriber() {
        String msisdn = "79994441144";
        doThrow(new IllegalArgumentException()).when(subscriberRepository).deleteById(msisdn);

        assertThrows(IllegalArgumentException.class, () -> subscriberService.deleteSubscriberByID(msisdn));
    }
}