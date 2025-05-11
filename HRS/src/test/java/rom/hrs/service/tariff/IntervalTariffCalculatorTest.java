package rom.hrs.service.tariff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.hrs.dto.*;
import rom.hrs.entity.Tariff;
import rom.hrs.entity.TariffInterval;
import rom.hrs.entity.TariffIntervalId;
import rom.hrs.exception.NoIntervalsFoundException;
import rom.hrs.repository.TariffIntervalRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntervalTariffCalculatorTest {

    @Mock
    private TariffIntervalRepository tariffIntervalRepository;

    @InjectMocks
    private IntervalTariffCalculator calculator;

    private CalculationRequest request;
    private Tariff tariff;
    private Subscriber caller;
    private Subscriber receiver;
    private CalculationResponse response;

    @BeforeEach
    void setUp() {
        caller = new Subscriber(3, "89996666666", true, 1L, 3, LocalDate.now().minusDays(1));
        receiver = new Subscriber(2, "79992223344", true, 1L, 0, LocalDate.now());

        request = new CalculationRequest(
                "01",
                caller,
                receiver,
                10,
                LocalDate.now()
        );

        tariff = new Tariff(1L, "Test", "Test", 1);
        response = new CalculationResponse();
    }

    @Test
    void calculate_ShouldApplyIntervalFee_WhenPaymentDue() throws NoIntervalsFoundException {

        // Arrange
        TariffInterval intervalEntity = createTariffInterval(1, 30, BigDecimal.valueOf(100.0));
        when(tariffIntervalRepository.findAllByTariffId(anyLong()))
                .thenReturn(List.of(intervalEntity));

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertSame(response, result);
        assertEquals(100.0, result.getCost(), 0.001);
        assertEquals(caller.paymentDay().plusDays(30), result.getNextPaymentDate());
        verify(tariffIntervalRepository).findAllByTariffId(tariff.getId());
    }

    @Test
    void calculate_ShouldThrowException_WhenNoIntervalsFound() {
        // Arrange
        when(tariffIntervalRepository.findAllByTariffId(anyLong()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(NoIntervalsFoundException.class,
                () -> calculator.calculate(request, tariff, response));
        verify(tariffIntervalRepository).findAllByTariffId(tariff.getId());
    }

    @Test
    void calculate_ShouldUseFirstInterval_WhenMultipleIntervalsFound() throws NoIntervalsFoundException {
        // Arrange
        TariffInterval interval1 = createTariffInterval(1, 30, BigDecimal.valueOf(100.0));
        TariffInterval interval2 = createTariffInterval(2, 60, BigDecimal.valueOf(180.0));

        when(tariffIntervalRepository.findAllByTariffId(anyLong()))
                .thenReturn(List.of(interval1, interval2));

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertSame(response, result);
        assertEquals(100.0, result.getCost(), 0.001);
        verify(tariffIntervalRepository).findAllByTariffId(tariff.getId());
    }

    @Test
    void calculate_ShouldNotApplyFee_WhenPaymentNotDue() throws NoIntervalsFoundException {
        // Arrange
        Subscriber currentCaller = new Subscriber(3, "89996666666", true, 1L, 3, LocalDate.now());
        request = new CalculationRequest(
                "01",
                currentCaller,
                receiver,
                10,
                LocalDate.now()
        );

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertSame(response, result);
        assertNull(result.getCost());
        assertNull(result.getNextPaymentDate());
        verifyNoInteractions(tariffIntervalRepository);
    }

    @Test
    void calculate_ShouldAddToExistingCost_WhenCostAlreadySet() throws NoIntervalsFoundException {
        // Arrange
        TariffInterval intervalEntity = createTariffInterval(1, 30, BigDecimal.valueOf(100.0));
        when(tariffIntervalRepository.findAllByTariffId(anyLong()))
                .thenReturn(List.of(intervalEntity));

        response.setCost(50.0);

        // Act
        CalculationResponse result = calculator.calculate(request, tariff, response);

        // Assert
        assertEquals(150.0, result.getCost(), 0.001);
    }

    @Test
    void isIntervalPaymentDue_ShouldReturnTrue_WhenPaymentOverdue() {
        // Act & Assert
        assertTrue(calculator.isIntervalPaymentDue(request));
    }

    @Test
    void isIntervalPaymentDue_ShouldReturnFalse_WhenPaymentNotDue() {
        // Arrange
        Subscriber currentCaller = new Subscriber(3, "89996666666", true, 1L, 3, LocalDate.now());
        CalculationRequest currentRequest = new CalculationRequest(
                "01",
                currentCaller,
                receiver,
                10,
                LocalDate.now()
        );

        // Act & Assert
        assertFalse(calculator.isIntervalPaymentDue(currentRequest));
    }

    @Test
    void getIntervalsByTariffId_ShouldReturnConvertedDtos() {
        // Arrange
        TariffInterval intervalEntity = createTariffInterval(1, 30, BigDecimal.valueOf(100.0));
        when(tariffIntervalRepository.findAllByTariffId(anyLong()))
                .thenReturn(List.of(intervalEntity));

        // Act
        List<TariffIntervalDto> result = calculator.getIntervalsByTariffId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(30, result.get(0).getInterval());
        assertEquals(100.0, result.get(0).getPrice().doubleValue(), 0.001);
    }

    private TariffInterval createTariffInterval(long id, int interval, BigDecimal price) {
        TariffInterval intervalEntity = new TariffInterval();
        intervalEntity.setId(new TariffIntervalId(id, interval));
        intervalEntity.setPrice(price);
        return intervalEntity;
    }
}