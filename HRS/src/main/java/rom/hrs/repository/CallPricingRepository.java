package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.CallPricing;

import java.util.List;

@Repository
public interface CallPricingRepository extends JpaRepository<CallPricing, Long> {
    List<CallPricing> findByTariffId(Long tariffId);
}
