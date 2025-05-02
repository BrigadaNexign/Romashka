package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffType;

@Repository
public interface TariffTypeRepository extends JpaRepository<TariffType, Integer> {
}
