package rom.cdr.service.subscriber;

import lombok.RequiredArgsConstructor;
import rom.cdr.entity.Subscriber;
import rom.cdr.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с данными абонентов.
 * Предоставляет методы доступа к информации об абонентах телефонной сети.
 */
@Service
@RequiredArgsConstructor
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    /**
     * Получает список всех номеров телефонов (MSISDN) абонентов.
     *
     * @return список номеров телефонов в формате строк
     */
    public List<String> getAllMsisdns() {
        return subscriberRepository.findAll().stream()
                .map(Subscriber::getMsisdn)
                .collect(Collectors.toList());
    }
}
