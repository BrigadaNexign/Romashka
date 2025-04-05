package org.example.service.subscriber;

import org.example.entity.Subscriber;
import org.example.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для работы с абонентами.
 * Предоставляет методы для сохранения, поиска и удаления абонентов.
 */
@Service
@Component
public class SubscriberServiceImpl implements SubscriberService{
    private final SubscriberRepository subscriberRepository;

    /**
     * Конструктор с внедрением зависимости репозитория абонентов.
     *
     * @param subscriberRepository репозиторий для работы с абонентами
     */
    @Autowired
    public SubscriberServiceImpl(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public Subscriber saveSubscriber(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    @Override
    public List<Subscriber> fetchSubscriberList() {
        return subscriberRepository.findAll();
    }

    @Override
    public void deleteSubscriberByID(String msisdn) {
        subscriberRepository.deleteById(msisdn);
    }

}
