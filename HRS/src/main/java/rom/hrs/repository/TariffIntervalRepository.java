package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffInterval;
import rom.hrs.entity.TariffIntervalId;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffIntervalRepository extends JpaRepository<TariffInterval, TariffIntervalId> {
    Optional<TariffInterval> findByTariffId(Long tariffId);
}
