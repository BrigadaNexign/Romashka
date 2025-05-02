package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffInterval;
import rom.hrs.entity.TariffIntervalId;

import java.util.List;

@Repository
public interface TariffIntervalRepository extends JpaRepository<TariffInterval, TariffIntervalId> {
    List<TariffInterval> findByTariffId(Integer tariffId);
}
