package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rom.hrs.entity.SubscriberTariff;

import java.util.Optional;

public interface SubscriberTariffRepository extends JpaRepository<SubscriberTariff, Long> {
    Optional<SubscriberTariff> findByMsisdn(String msisdn);
}
