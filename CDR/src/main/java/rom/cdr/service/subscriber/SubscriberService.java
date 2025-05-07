package rom.cdr.service.subscriber;

import rom.cdr.entity.Subscriber;
import rom.cdr.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getAllMsisdns() {
        return subscriberRepository.findAll().stream()
                .map(Subscriber::getMsisdn)
                .collect(Collectors.toList());
    }
}
