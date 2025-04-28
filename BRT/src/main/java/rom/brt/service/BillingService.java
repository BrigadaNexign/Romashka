package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.brt.dto.CalculationResponse;
import rom.brt.entity.User;
import rom.brt.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final UserRepository userRepository;

    public void processBilling(User user, CalculationResponse response) {
        if (response.cost() != null) {
            user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(response.cost())));
        }

        if (response.nextPaymentDate() != null) {
            user.getUserParams().setPaymentDay(response.nextPaymentDate());
        }

        if (response.remainingMinutes() != null) {
            user.getUserParams().setMinutes(response.remainingMinutes());
        }

        userRepository.save(user);
    }
}