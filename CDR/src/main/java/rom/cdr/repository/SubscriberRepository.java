package rom.cdr.repository;

import org.springframework.stereotype.Repository;
import rom.cdr.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с абонентами.
 * Предоставляет методы для выполнения операций с базой данных, связанных с абонентами.
 *
 * @see Subscriber
 */
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> { }
