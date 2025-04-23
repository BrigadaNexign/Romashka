package rom.cdr.service.subscriber;

import jakarta.annotation.PostConstruct;
import rom.cdr.entity.Subscriber;
import rom.cdr.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    @PostConstruct
    public void init() {
        initializeSubscribers();
    }

    private void initializeSubscribers() {
        List<String> msisdns = Arrays.asList(
                "79991113355", "79992224466", "79993335577", "79994446688",
                "79995557799", "79996668800", "79997779911", "79998880022",
                "79990001133", "79991112244", "79992223355", "79993334466",
                "79994445577", "79995556688", "79906667799", "79907778800",
                "79908889911", "79909990022", "79900001144", "79901113366",
                "79902224477", "79903335588", "79904446699", "79905557700",
                "79906668811", "79907779922", "79908880033", "79909991144"
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

    public List<String> getAllMsisdns() {
        return subscriberRepository.findAll().stream()
                .map(Subscriber::getMsisdn)
                .collect(Collectors.toList());
    }
}
