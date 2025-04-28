package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.Tariff;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Integer> {}

