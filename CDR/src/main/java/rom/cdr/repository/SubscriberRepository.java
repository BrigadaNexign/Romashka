package rom.cdr.repository;

import rom.cdr.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с абонентами.
 * Предоставляет методы для выполнения операций с базой данных, связанных с абонентами.
 *
 * @see Subscriber
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    /**
     * Удаляет абонента по его идентификатору.
     *
     * @param msisdn идентификатор абонента
     */
    void deleteById(String msisdn);
}
