package org.example.service.CDR;

import jakarta.annotation.PostConstruct;
import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.repository.CDRRepository;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация сервиса для работы с CDR.
 * Предоставляет методы для сохранения, поиска, удаления и инициализации данных CDR.
 */
@Service
@Component
public class CDRServiceImpl implements CDRService {
    private final CDRRepository cdrRepository;
    private final SubscriberServiceImpl subscriberService;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param cdrRepository   репозиторий для работы с CDR
     * @param subscriberService сервис для работы с абонентами
     */
    @Autowired
    public CDRServiceImpl (CDRRepository cdrRepository, SubscriberServiceImpl subscriberService) {
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
    public CDR saveCDR(CDR cdr) {
        return cdrRepository.save(cdr);
    }

    @Override
    public List<CDR> fetchCDRList() {
        return cdrRepository.findAll();
    }

    @Override
    public void deleteCDRByID(Long CDRId) {
        cdrRepository.deleteById(CDRId);
    }

    @Override
    public List<CDR> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn) {
        return cdrRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn);
    }

    @Override
    public void initializeData() {
        generateCDRsForYear();
        initializeSubscribers();
    }

    @Override
    public <S extends CDR> List<S> saveAllCDRs(Iterable<S> entities) {
        return cdrRepository.saveAll(entities);
    }

    @Override
    public List<CDR> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
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
     * Генерирует CDR записи для всех абонентов за последний год.
     */
    private void generateCDRsForYear() {
        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();
        Random random = new Random();

        List<CDR> allCDRs = new ArrayList<>();

        for (Subscriber subscriber : subscribers) {
            LocalDateTime currentDate = startDate;
            while (currentDate.isBefore(endDate)) {
                CDR cdr = generateCDRForSubscriber(subscriber, currentDate);
                allCDRs.add(cdr);
                currentDate = currentDate.plusMinutes(random.nextInt(1440));
            }
        }

        allCDRs.sort(Comparator.comparing(CDR::getStartTime));

        saveAllCDRs(allCDRs);
    }

    /**
     * Генерирует CDR запись для указанного абонента в заданное время.
     *
     * @param subscriber абонент, для которого генерируется CDR запись
     * @param startTime  время начала звонка
     * @return сгенерированная CDR запись
     */
    private CDR generateCDRForSubscriber(Subscriber subscriber, LocalDateTime startTime) {
        CDR cdr = new CDR();
        Random random = new Random();
        cdr.setCallType(random.nextBoolean() ? "01" : "02");
        cdr.setCallerMsisdn(subscriber.getMsisdn());
        cdr.setReceiverMsisdn(getRandomReceiverMsisdn(subscriber.getMsisdn()));
        cdr.setStartTime(startTime);
        cdr.setEndTime(startTime.plusSeconds(random.nextInt(3600)));

        return cdr;
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
