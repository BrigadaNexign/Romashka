package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.CallPricing;
import rom.hrs.entity.CallPricingId;

@Repository
public interface CallPricingRepository extends JpaRepository<CallPricing, CallPricingId> {}
