package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffInterval;
import rom.hrs.entity.TariffIntervalId;

@Repository
public interface TariffIntervalRepository extends JpaRepository<TariffInterval, TariffIntervalId> {}
