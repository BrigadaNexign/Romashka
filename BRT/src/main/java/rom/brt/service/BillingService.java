package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.dto.CalculationResponse;
import rom.brt.entity.User;
import rom.brt.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(BillingService.class);

    public void processBilling(User user, CalculationResponse response) {
        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(response.getCost())));
        user.getUserParams().setPaymentDay(response.getNextPaymentDate());
        user.getUserParams().setMinutes(response.getRemainingMinutes());

        userRepository.save(user);
        logger.info("User information updated: {}\n", user);
    }
}