package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffParameter;
import rom.hrs.entity.TariffParameterId;

@Repository
public interface TariffParameterRepository extends JpaRepository<TariffParameter, TariffParameterId> {}
