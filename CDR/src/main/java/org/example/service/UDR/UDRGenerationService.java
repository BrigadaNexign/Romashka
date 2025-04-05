package org.example.service.UDR;

import org.example.dto.UDR;
import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.service.CDR.CDRServiceImpl;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для генерации UDR отчетов.
 * Предоставляет методы для создания UDR отчетов для абонентов на основе CDR.
 */
@Service
public class UDRGenerationService {
    @Autowired
    private final CDRServiceImpl cdrService;
    @Autowired
    private final SubscriberServiceImpl subscriberService;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param cdrService      сервис для работы с CDR
     * @param subscriberService сервис для работы с абонентами
     */
    public UDRGenerationService(CDRServiceImpl cdrService, SubscriberServiceImpl subscriberService) {
        this.cdrService = cdrService;
        this.subscriberService = subscriberService;
    }

    /**
     * Генерирует UDR отчет для указанного абонента за весь период.
     *
     * @param msisdn номер абонента
     * @return объект UDR с деталями звонков абонента
     */
    public UDR generateUDRForSubscriber(String msisdn) {
        List<CDR> cdrs = cdrService.fetchCDRListByMsisdn(msisdn, msisdn);
        return createUDRFromCDRs(msisdn, cdrs);
    }

    /**
     * Генерирует UDR отчет для указанного абонента за указанный месяц.
     *
     * @param msisdn номер абонента
     * @param month  месяц в формате YearMonth
     * @return объект UDR с деталями звонков абонента
     */
    public UDR generateUDRForSubscriber(String msisdn, YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);
        List<CDR> cdrs = cdrService.fetchCDRListByMsisdnAndTime(
                msisdn, startOfMonth, endOfMonth);
        return createUDRFromCDRs(msisdn, cdrs);
    }

    /**
     * Генерирует UDR отчеты для всех абонентов за указанный месяц.
     *
     * @param month месяц в формате YearMonth
     * @return карта, где ключ — номер абонента, а значение — объект UDR
     */
    public Map<String, UDR> generateUDRForAllSubscribers(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);
        Map<String, UDR> udrMap = new HashMap<>();

        List<Subscriber> subscribers = subscriberService.fetchSubscriberList();
        for (Subscriber subscriber : subscribers) {
            String msisdn = subscriber.getMsisdn();
            List<CDR> cdrs = cdrService.fetchCDRListByMsisdnAndTime(
                    msisdn, startOfMonth, endOfMonth);
            udrMap.put(msisdn, createUDRFromCDRs(msisdn, cdrs));
        }

        return udrMap;
    }

    /**
     * Создает объект UDR на основе списка CDR записей.
     *
     * @param msisdn номер абонента
     * @param cdrs   список CDR записей
     * @return объект UDR с деталями звонков абонента
     */
    private UDR createUDRFromCDRs(String msisdn, List<CDR> cdrs) {
        Duration incomingCallDuration = Duration.ZERO;
        Duration outcomingCallDuration = Duration.ZERO;

        for (CDR cdr : cdrs) {
            Duration callDuration = Duration.between(cdr.getStartTime(), cdr.getEndTime());
            if (isIncomingAndReceiver(cdr, msisdn)) {
                incomingCallDuration = incomingCallDuration.plus(callDuration);
            } else if (isOutcomingAndCaller(cdr, msisdn)) {
                outcomingCallDuration = outcomingCallDuration.plus(callDuration);
            }
        }

        UDR udr = new UDR();
        udr.setMsisdn(msisdn);
        udr.setIncomingCall(new UDR.CallDetail(formatDuration(incomingCallDuration)));
        udr.setOutcomingCall(new UDR.CallDetail(formatDuration(outcomingCallDuration)));

        return udr;
    }

    /**
     * Проверяет, является ли CDR запись входящим звонком для указанного абонента.
     *
     * @param cdr    запись CDR
     * @param msisdn номер абонента
     * @return true, если запись является входящим звонком для абонента, иначе false
     */
    private boolean isIncomingAndReceiver(CDR cdr, String msisdn) {
        return cdr.getCallType().equals("02") && cdr.getReceiverMsisdn().equals(msisdn);
    }

    /**
     * Проверяет, является ли CDR запись исходящим звонком для указанного абонента.
     *
     * @param cdr    запись CDR
     * @param msisdn номер абонента
     * @return true, если запись является исходящим звонком для абонента, иначе false
     */
    private boolean isOutcomingAndCaller(CDR cdr, String msisdn) {
        return cdr.getCallType().equals("01") && cdr.getCallerMsisdn().equals(msisdn);
    }

    /**
     * Форматирует продолжительность звонка в строку формата "часы:минуты:секунды".
     *
     * @param duration продолжительность звонка
     * @return строка в формате "часы:минуты:секунды"
     */
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}