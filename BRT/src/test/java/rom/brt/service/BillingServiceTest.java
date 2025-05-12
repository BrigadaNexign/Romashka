package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import rom.brt.dto.response.CalculationResponse;
import rom.brt.entity.User;
import rom.brt.entity.UserParams;
import rom.brt.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private Logger logger;

    @InjectMocks
    private BillingService billingService;

    @Test
    void processBilling_shouldUpdateUserDataCorrectly() {
        User user = createTestUser(100.0);
        CalculationResponse response = createTestResponse(15.5, 50, LocalDate.now().plusMonths(1));

        billingService.processBilling(user, response);

        assertEquals(BigDecimal.valueOf(84.5), user.getBalance()); // 100 - 15.5 = 84.5
        assertEquals(50, user.getUserParams().getMinutes());
        assertEquals(response.getNextPaymentDate(), user.getUserParams().getPaymentDay());

        verify(userRepository).save(user);
    }

    @Test
    void processBilling_handleZeroCost() {
        User user = createTestUser(50.0);
        CalculationResponse response = createTestResponse(0.0, 100, LocalDate.now().plusMonths(1));

        billingService.processBilling(user, response);

        assertEquals(BigDecimal.valueOf(50.0), user.getBalance()); // Баланс не изменился
        verify(userRepository).save(user);
    }

    @Test
    void processBilling_handleNegativeBalance() {
        User user = createTestUser(10.0);
        CalculationResponse response = createTestResponse(15.0, 5, LocalDate.now().plusMonths(1));

        billingService.processBilling(user, response);

        assertEquals(BigDecimal.valueOf(-5.0), user.getBalance()); // 10 - 15 = -5
        verify(userRepository).save(user);
    }

    @Test
    void processBilling_updatePaymentDate() {
        User user = createTestUser(100.0);
        LocalDate newPaymentDate = LocalDate.of(2023, 12, 31);
        CalculationResponse response = createTestResponse(10.0, 75, newPaymentDate);

        billingService.processBilling(user, response);

        assertEquals(newPaymentDate, user.getUserParams().getPaymentDay());
    }

    @Test
    void processBilling_shouldUpdateRemainingMinutes() {
        User user = createTestUser(100.0);
        CalculationResponse response = createTestResponse(10.0, 42, LocalDate.now().plusMonths(1));

        billingService.processBilling(user, response);

        assertEquals(42, user.getUserParams().getMinutes());
    }

    private User createTestUser(double initialBalance) {
        User user = new User();
        user.setBalance(BigDecimal.valueOf(initialBalance));
        user.setUserParams(new UserParams());
        return user;
    }

    private CalculationResponse createTestResponse(double cost, int minutes, LocalDate nextPaymentDate) {
        return CalculationResponse.builder()
                .cost(cost)
                .remainingMinutes(minutes)
                .nextPaymentDate(nextPaymentDate)
                .build();
    }
}