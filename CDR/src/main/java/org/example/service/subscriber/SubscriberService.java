package org.example.service.subscriber;

import org.example.entity.Subscriber;

import java.util.List;

/**
 * Интерфейс сервиса для работы с абонентами.
 * Предоставляет методы для сохранения, поиска и удаления абонентов.
 */
public interface SubscriberService {

    /**
     * Сохраняет абонента в базе данных.
     *
     * @param subscriber абонент для сохранения
     * @return сохраненный абонент
     */
    Subscriber saveSubscriber(Subscriber subscriber);

    /**
     * Возвращает список всех абонентов.
     *
     * @return список всех абонентов
     */
    List<Subscriber> fetchSubscriberList();

    /**
     * Удаляет абонента по его идентификатору.
     *
     * @param msisdn идентификатор абонента
     */
    void deleteSubscriberByID(String msisdn);
}
