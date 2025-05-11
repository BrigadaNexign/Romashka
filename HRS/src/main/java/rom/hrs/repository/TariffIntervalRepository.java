package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.Tariff;
import rom.hrs.entity.TariffInterval;
import rom.hrs.entity.TariffIntervalId;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffIntervalRepository extends JpaRepository<TariffInterval, TariffIntervalId> {
    @Query("SELECT ti FROM TariffInterval ti WHERE ti.id.tariffId = :tariffId")
    List<TariffInterval> findAllByTariffId(@Param("tariffId") Long tariffId);
}
