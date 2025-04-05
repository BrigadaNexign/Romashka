package org.example.repository;

import org.example.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с абонентами.
 * Предоставляет методы для выполнения операций с базой данных, связанных с абонентами.
 *
 * @see org.example.entity.Subscriber
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    /**
     * Удаляет абонента по его идентификатору.
     *
     * @param msisdn идентификатор абонента
     */
    void deleteById(String msisdn);
}
