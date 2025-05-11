package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.TariffParameter;

import java.util.List;


@Repository
public interface TariffParameterRepository extends JpaRepository<TariffParameter, TariffParameter.TariffParameterId> {
    List<TariffParameter> findByTariff_Id(Long tariffId);
}
