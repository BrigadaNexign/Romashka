package rom.hrs.service;

import rom.hrs.entity.CallPricing;
import rom.hrs.repository.CallPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallPricingService {
    private final CallPricingRepository callPricingRepository;

    public List<CallPricing> getCallPricingListByTariffId(Integer tariffId) {
        return callPricingRepository.findByTariffId(tariffId);
    }
}
