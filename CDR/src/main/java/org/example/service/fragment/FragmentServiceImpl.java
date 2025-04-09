package org.example.service.fragment;

import jakarta.annotation.PostConstruct;
import org.example.entity.Fragment;
import org.example.entity.Subscriber;
import org.example.repository.CDRRepository;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация сервиса для работы с Fragment.
 * Предоставляет методы для сохранения, поиска, удаления и инициализации данных Fragment.
 */
@Service
@Component
public class FragmentServiceImpl implements FragmentService {
    private final CDRRepository cdrRepository;
    private final SubscriberServiceImpl subscriberService;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param cdrRepository   репозиторий для работы с Fragment
     * @param subscriberService сервис для работы с абонентами
     */
    @Autowired
    public FragmentServiceImpl(CDRRepository cdrRepository, SubscriberServiceImpl subscriberService) {
        this.cdrRepository = cdrRepository;
        this.subscriberService = subscriberService;
    }

    /**
     * Метод, выполняемый после создания бина. Инициализирует данные.
     */
    @PostConstruct
    public void init() {
        initializeData();
    }

    @Override
    public Fragment saveCDR(Fragment fragment) {
        return cdrRepository.save(fragment);
    }

    @Override
    public List<Fragment> fetchCDRList() {
        return cdrRepository.findAll();
    }

    @Override
    public void deleteCDRByID(Long CDRId) {
        cdrRepository.deleteById(CDRId);
    }

    @Override
    public List<Fragment> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn) {
        return cdrRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn);
    }

    @Override
    public void initializeData() {
        generateCDRsForYear();
        initializeSubscribers();
    }

    @Override
    public <S extends Fragment> List<S> saveAllCDRs(Iterable<S> entities) {
        return cdrRepository.saveAll(entities);
    }

    @Override
    public List<Fragment> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        return cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startOfMonth, endOfMonth
        );
    }

    /**
     * Инициализирует список абонентов.
     */
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
            subscriberService.saveSubscriber(subscriber);
        }
    }

    /**
     * Генерирует Fragment записи для всех абонентов за последний год.
     */
    private void generateCDRsForYear() {
        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();
        Random random = new Random();

        List<Fragment> allFragments = new ArrayList<>();

        for (Subscriber subscriber : subscribers) {
            LocalDateTime currentDate = startDate;
            while (currentDate.isBefore(endDate)) {
                Fragment fragment = generateCDRForSubscriber(subscriber, currentDate);
                allFragments.add(fragment);
                currentDate = currentDate.plusMinutes(random.nextInt(1440));
            }
        }

        allFragments.sort(Comparator.comparing(org.example.entity.Fragment::getStartTime));

        saveAllCDRs(allFragments);
    }

    /**
     * Генерирует Fragment запись для указанного абонента в заданное время.
     *
     * @param subscriber абонент, для которого генерируется Fragment запись
     * @param startTime  время начала звонка
     * @return сгенерированная Fragment запись
     */
    private Fragment generateCDRForSubscriber(Subscriber subscriber, LocalDateTime startTime) {
        Fragment fragment = new Fragment();
        Random random = new Random();
        fragment.setCallType(random.nextBoolean() ? "01" : "02");
        fragment.setCallerMsisdn(subscriber.getMsisdn());
        fragment.setReceiverMsisdn(getRandomReceiverMsisdn(subscriber.getMsisdn()));
        fragment.setStartTime(startTime);
        fragment.setEndTime(startTime.plusSeconds(random.nextInt(3600)));

        return fragment;
    }

    /**
     * Возвращает случайный номер абонента, отличный от указанного.
     *
     * @param callerMsisdn номер абонента, инициировавшего звонок
     * @return номер абонента, принимающего звонок
     */
    private String getRandomReceiverMsisdn(String callerMsisdn) {
        Random random = new Random();
        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        Subscriber receiver = subscribers.get(random.nextInt(subscribers.size()));
        while (receiver.getMsisdn().equals(callerMsisdn)) {
            receiver = subscribers.get(random.nextInt(subscribers.size()));
        }
        return receiver.getMsisdn();
    }

    public boolean hasConflictingCalls(
            String caller,
            String receiver,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return cdrRepository.existsConflictingCalls(caller, receiver, start, end);
    }
}
