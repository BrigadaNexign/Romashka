package org.example.service.CDR;

import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Сервис для генерации CDR записей.
 * Предоставляет методы для создания CDR записей для абонентов за определенный период.
 */
@Service
public class CDRGeneratorService {

    @Autowired
    private CDRServiceImpl cdrService;

    @Autowired
    private SubscriberServiceImpl subscriberService;

    private final Random random = new Random();

    /**
     * Генерирует CDR записи для всех абонентов за последний год.
     */
    public void generateCDRsForYear() {
        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        for (Subscriber subscriber : subscribers) {
            LocalDateTime currentDate = startDate;
            while (currentDate.isBefore(endDate)) {
                generateCDRForSubscriber(subscriber, currentDate);
                currentDate = currentDate.plusMinutes(random.nextInt(1440));
            }
        }
    }

    /**
     * Генерирует CDR запись для указанного абонента в заданное время.
     *
     * @param subscriber абонент, для которого генерируется CDR запись
     * @param startTime  время начала звонка
     */
    public void generateCDRForSubscriber(Subscriber subscriber, LocalDateTime startTime) {
        CDR cdr = new CDR();
        cdr.setCallType(random.nextBoolean() ? "01" : "02");
        cdr.setCallerMsisdn(subscriber.getMsisdn());
        cdr.setReceiverMsisdn(getRandomReceiverMsisdn(subscriber.getMsisdn()));
        cdr.setStartTime(startTime);
        cdr.setEndTime(startTime.plusSeconds(random.nextInt(3600)));

        cdrService.saveCDR(cdr);
    }

    /**
     * Возвращает случайный номер абонента, отличный от указанного.
     *
     * @param callerMsisdn номер абонента, инициировавшего звонок
     * @return номер абонента, принимающего звонок
     * @throws IllegalStateException если в списке абонентов нет других номеров, кроме callerMsisdn
     */
    public String getRandomReceiverMsisdn(String callerMsisdn) {
        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        Subscriber receiver = subscribers.get(random.nextInt(subscribers.size()));

        if (!checkOtherMsisdnExists(callerMsisdn, subscribers)) throw new IllegalStateException();

        while (receiver.equals(callerMsisdn)) {
            receiver = subscribers.get(random.nextInt(subscribers.size()));
        }
        return receiver.getMsisdn();
    }

    /**
     * Проверяет, существует ли в списке абонентов хотя бы один номер, отличный от указанного.
     *
     * @param callerMsisdn номер абонента, инициировавшего звонок
     * @param subscribers  список абонентов
     * @return true, если в списке есть другие номера, иначе false
     */
    private boolean checkOtherMsisdnExists(String callerMsisdn, List<Subscriber> subscribers) {
        return subscribers.stream().anyMatch(element -> !element.getMsisdn().equals(callerMsisdn));
    }
}