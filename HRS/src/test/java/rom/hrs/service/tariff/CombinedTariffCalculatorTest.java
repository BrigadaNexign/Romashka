package rom.hrs.service.tariff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.Subscriber;
import rom.hrs.entity.PricingType;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.BusinessException;
import rom.hrs.exception.NoIntervalsFoundException;
import rom.hrs.exception.NoPricingsFoundException;
import rom.hrs.exception.PricingNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CombinedTariffCalculatorTest {

    @Mock
    private IntervalTariffCalculator intervalCalculator;

    @Mock
    private PerMinuteTariffCalculator perMinuteCalculator;

    @InjectMocks
    private CombinedTariffCalculator calculator;

    private CalculationRequest request;
    private Tariff tariff;
    private CalculationResponse response;

    @BeforeEach
    void setUp() {
        Subscriber caller = new Subscriber(1, "79991234567", true, 1L, 100, LocalDate.now().minusDays(1));
        Subscriber receiver = new Subscriber(2, "79998765432", true, 1L, 50, LocalDate.now());

        request = new CalculationRequest(
                "01",
                caller,
                receiver,
                10,
                LocalDate.now()
        );

        tariff = new Tariff(1L, "Combined", "Combined tariff", 3);
        response = new CalculationResponse();
    }

    @Test
    void calculate_ShouldCallBothCalculatorsInOrder_WhenSuccess() throws BusinessException {
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    resp.setCost(100.0);
                    resp.setNextPaymentDate(LocalDate.now().plusDays(30));
                    return resp;
                });

        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    resp.setCost(resp.getCost() + 50.0);
                    resp.setRemainingMinutes(90);
                    return resp;
                });

        CalculationResponse result = calculator.calculate(request, tariff, response);

        assertSame(response, result);
        assertEquals(150.0, result.getCost(), 0.001);
        assertEquals(LocalDate.now().plusDays(30), result.getNextPaymentDate());
        assertEquals(90, result.getRemainingMinutes());

        InOrder inOrder = inOrder(intervalCalculator, perMinuteCalculator);
        inOrder.verify(intervalCalculator).calculate(eq(request), eq(tariff), any());
        inOrder.verify(perMinuteCalculator).calculate(eq(request), eq(tariff), any());
    }

    @Test
    void calculate_ShouldCombineResultsCorrectly_WhenBothCalculatorsModifyResponse() throws BusinessException {
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    resp.setCost(200.0);
                    resp.setNextPaymentDate(LocalDate.now().plusDays(60));
                    resp.setTariffType("Interval");
                    return resp;
                });

        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    resp.setCost(resp.getCost() + 75.0);
                    resp.setRemainingMinutes(25);
                    resp.setTariffType("PerMinute");
                    return resp;
                });

        CalculationResponse result = calculator.calculate(request, tariff, response);

        assertSame(response, result);
        assertEquals(275.0, result.getCost(), 0.001);
        assertEquals(LocalDate.now().plusDays(60), result.getNextPaymentDate());
        assertEquals(25, result.getRemainingMinutes());
        assertEquals("PerMinute", result.getTariffType());
    }

    @Test
    void calculate_ShouldThrowBusinessExceptionSubtype_WhenIntervalCalculatorFails() throws BusinessException {
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenThrow(new NoIntervalsFoundException(tariff.getId()));

        Exception exception = assertThrows(Exception.class,
                () -> calculator.calculate(request, tariff, response));

        assertInstanceOf(BusinessException.class, exception, "Exception should be a subtype of BusinessException");
        verify(perMinuteCalculator, never()).calculate(any(), any(), any());
    }

    @Test
    void calculate_ShouldThrowBusinessExceptionSubtype_WhenPerMinuteCalculatorFails() throws BusinessException {
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));
        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenThrow(new NoPricingsFoundException(tariff.getId()));

        Exception exception = assertThrows(Exception.class,
                () -> calculator.calculate(request, tariff, response));

        assertInstanceOf(BusinessException.class, exception, "Exception should be a subtype of BusinessException");
        verify(intervalCalculator).calculate(any(), any(), any());
    }

    @Test
    void calculate_ShouldThrowBusinessExceptionSubtype() throws BusinessException {
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));
        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenThrow(new PricingNotFoundException(tariff.getId(), PricingType.OUTGOING_BOTH_SERVICED));

        Exception exception = assertThrows(Exception.class,
                () -> calculator.calculate(request, tariff, response));

        assertInstanceOf(BusinessException.class, exception, "Exception should be a subtype of BusinessException");
        verify(intervalCalculator).calculate(any(), any(), any());
    }

    @Test
    void calculate_ShouldMaintainSingleResponseObject_ThroughAllCalculators() throws BusinessException {
        // Arrange
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));

        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertSame(response, result);
        verify(intervalCalculator).calculate(eq(request), eq(tariff), same(response));
        verify(perMinuteCalculator).calculate(eq(request), eq(tariff), same(response));
    }

    @Test
    void calculate_ShouldHandleNullInitialCost_Properly() throws BusinessException {
        // Arrange
        when(intervalCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    resp.setCost(100.0); // устанавливаем начальную стоимость
                    return resp;
                });

        when(perMinuteCalculator.calculate(any(), any(), any()))
                .thenAnswer(invocation -> {
                    CalculationResponse resp = invocation.getArgument(2);
                    // безопасное увеличение стоимости, даже если изначально было null
                    double currentCost = resp.getCost() != null ? resp.getCost() : 0.0;
                    resp.setCost(currentCost + 50.0);
                    return resp;
                });

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertEquals(150.0, result.getCost(), 0.001);
    }
}