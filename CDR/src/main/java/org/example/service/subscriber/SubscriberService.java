package org.example.service.subscriber;

import jakarta.annotation.PostConstruct;
import org.example.entity.Subscriber;
import org.example.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Реализация сервиса для работы с абонентами.
 * Предоставляет методы для сохранения, поиска и удаления абонентов.
 */
@Service
@Component
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @PostConstruct
    public void init() {
        initializeSubscribers();
    }

    private void initializeSubscribers() {
        List<String> msisdns = Arrays.asList(
                "79991113355", "79992224466", "79993335577", "79994446688",
                "79995557799", "79996668800", "79997779911", "79998880022",
                "79990001133", "79991112244", "79992223355", "79993334466",
                "79994445577", "79995556688", "79996667799", "79997778800",
                "79998889911", "79999990022", "79990001144", "79991113366",
                "79992224477", "79993335588", "79994446699", "79995557700",
                "79996668811", "79997779922", "79998880033", "79999991144"
        );

        for (String msisdn : msisdns) {
            Subscriber subscriber = new Subscriber();
            subscriber.setMsisdn(msisdn);
            saveSubscriber(subscriber);
        }
    }

    public Subscriber saveSubscriber(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    public List<Subscriber> fetchSubscriberList() {
        return subscriberRepository.findAll();
    }

    public void deleteSubscriberByID(String msisdn) {
        subscriberRepository.deleteById(msisdn);
    }
}
