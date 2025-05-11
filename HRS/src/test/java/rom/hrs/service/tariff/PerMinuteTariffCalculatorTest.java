package rom.hrs.service.tariff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.Subscriber;
import rom.hrs.entity.PricingType;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.BusinessException;
import rom.hrs.exception.PricingNotFoundException;
import rom.hrs.service.CallPricingService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerMinuteTariffCalculatorTest {

    @Mock
    private CallPricingService pricingService;

    @Mock
    private Logger logger;

    @InjectMocks
    private PerMinuteTariffCalculator calculator;

    private CalculationRequest request;
    private Tariff tariff;
    private CalculationResponse response;
    private Subscriber callerWithMinutes;
    private Subscriber callerWithoutMinutes;

    @BeforeEach
    void setUp() {
        callerWithMinutes = new Subscriber(1, "79991234567", true, 1L, 100, LocalDate.now());
        callerWithoutMinutes = new Subscriber(2, "79998765432", true, 1L, 0, LocalDate.now());

        request = new CalculationRequest(
                "01",
                callerWithMinutes,
                new Subscriber(3, "79997654321", true, 2L, 50, LocalDate.now()),
                10,
                LocalDate.now()
        );

        tariff = new Tariff(1L, "PerMinute", "Per minute tariff", 2);
        response = new CalculationResponse();
    }

    @Test
    void calculate_ShouldUseFreeMinutes_WhenEnoughMinutesAvailable() throws BusinessException {
        int initialMinutes = request.getCaller().minutes();
        int duration = request.getDurationMinutes();

        CalculationResponse result = calculator.calculate(request, tariff, response);

        assertSame(response, result);
        assertEquals(initialMinutes - duration, result.getRemainingMinutes());
        verifyNoInteractions(pricingService);
    }

    @Test
    void calculate_ShouldPartiallyUseFreeMinutes_WhenNotEnoughMinutes() throws BusinessException {
        request.setDurationMinutes(150);
        int expectedRemaining = 0;
        int expectedChargedDuration = 50;

        CalculationResponse result = calculator.calculate(request, tariff, response);

        assertSame(response, result);
        assertEquals(expectedRemaining, result.getRemainingMinutes());
        verify(pricingService).applyCallPricing(
                argThat(req -> req.getDurationMinutes() == expectedChargedDuration),
                eq(tariff),
                same(response)
        );
    }

    @Test
    void calculate_ShouldChargeFullCall_WhenNoFreeMinutes() throws BusinessException {
        request.setCaller(callerWithoutMinutes);

        CalculationResponse result = calculator.calculate(request, tariff, response);

        assertSame(response, result);
        verify(pricingService).applyCallPricing(
                argThat(req -> req.getDurationMinutes() == request.getDurationMinutes()),
                eq(tariff),
                same(response)
        );
    }

    @Test
    void calculate_ShouldPropagateException_WhenPricingFails() throws BusinessException {
        request.setCaller(callerWithoutMinutes);
        PricingNotFoundException exception = new PricingNotFoundException(tariff.getId(), PricingType.OUTGOING_BOTH_SERVICED);

        doThrow(exception)
                .when(pricingService)
                .applyCallPricing(any(), any(), any());

        PricingNotFoundException thrown = assertThrows(PricingNotFoundException.class,
                () -> calculator.calculate(request, tariff, response));

        assertEquals(tariff.getId(), thrown.getTariffId());
        assertEquals(PricingType.OUTGOING_BOTH_SERVICED, thrown.getPricingType());
    }

    @Test
    void hasFreeMinutes_ShouldReturnTrue_WhenMinutesAvailable() {
        assertTrue(calculator.hasFreeMinutes(request));
    }

    @Test
    void hasFreeMinutes_ShouldReturnFalse_WhenNoMinutes() {
        request.setCaller(callerWithoutMinutes);
        assertFalse(calculator.hasFreeMinutes(request));
    }

    @Test
    void applyFreeMinutes_ShouldSetZeroRemaining_WhenExactlyEnoughMinutes() throws BusinessException {
        request.setDurationMinutes(callerWithMinutes.minutes());

        calculator.applyFreeMinutes(request, tariff, response);

        assertEquals(0, response.getRemainingMinutes());
        verifyNoInteractions(pricingService);
    }

    @Test
    void applyFreeMinutes_ShouldNotCallPricing_WhenEnoughMinutes() throws BusinessException {
        calculator.applyFreeMinutes(request, tariff, response);

        verifyNoInteractions(pricingService);
    }

    @Test
    void applyFreeMinutes_ShouldModifyRequestDuration_WhenPartialFreeMinutes() throws BusinessException {
        int initialDuration = 150;
        request.setDurationMinutes(initialDuration);

        calculator.applyFreeMinutes(request, tariff, response);

        assertEquals(50, request.getDurationMinutes());
        verify(pricingService).applyCallPricing(any(), any(), any());
    }
}