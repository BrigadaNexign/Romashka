package rom.hrs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.Subscriber;
import rom.hrs.entity.*;
import rom.hrs.exception.*;
import rom.hrs.repository.CallPricingRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallPricingServiceTest {

    @Mock
    private CallPricingRepository callPricingRepository;

    @InjectMocks
    private CallPricingService callPricingService;

    private CalculationRequest request;
    private Tariff tariff;
    private CalculationResponse response;
    private Subscriber servicedReceiver;
    private Subscriber unservicedReceiver;

    @BeforeEach
    void setUp() {
        Subscriber servicedCaller = new Subscriber(1, "79991234567", true, 1L, 100, null);
        servicedReceiver = new Subscriber(2, "79998765432", true, 2L, 50, null);
        unservicedReceiver = new Subscriber(null, "79997654321", false, null, null, null);

        request = new CalculationRequest(
                "01", // OUTGOING
                servicedCaller,
                servicedReceiver,
                10,
                null
        );

        tariff = new Tariff(1L, "Test", "Test Tariff", 1);
        response = new CalculationResponse();
        response.setCost(0.0);
    }

    @Test
    void applyCallPricing_ShouldApplyCost_WhenPricingFound() throws BusinessException {
        CallPricing pricing = new CallPricing(
                new CallPricingId(1L, 1),
                BigDecimal.valueOf(1.5),
                tariff
        );
        when(callPricingRepository.findByTariffId(anyLong()))
                .thenReturn(List.of(pricing));

        callPricingService.applyCallPricing(request, tariff, response);

        assertEquals(15.0, response.getCost(), 0.001);
        verify(callPricingRepository).findByTariffId(tariff.getId());
    }

    @Test
    void applyCallPricing_ShouldThrowException_WhenNoPricingsFound() {
        when(callPricingRepository.findByTariffId(anyLong()))
                .thenReturn(List.of());

        assertThrows(NoPricingsFoundException.class, () ->
                callPricingService.applyCallPricing(request, tariff, response)
        );
    }

    @Test
    void applyCallPricing_ShouldThrowException_WhenPricingTypeNotFound() {
        CallPricing wrongPricing = new CallPricing(
                new CallPricingId(1L, 2),
                BigDecimal.valueOf(1.5),
                tariff
        );
        when(callPricingRepository.findByTariffId(anyLong()))
                .thenReturn(List.of(wrongPricing));

        assertThrows(PricingNotFoundException.class, () ->
                callPricingService.applyCallPricing(request, tariff, response)
        );
    }

    @Test
    void resolvePricingType_ShouldReturnOutgoingBothServiced_WhenBothServiced() throws BusinessException {
        request.setCallType("01");
        request.setReceiver(servicedReceiver);

        PricingType result = callPricingService.resolvePricingType(request);

        assertEquals(PricingType.OUTGOING_BOTH_SERVICED, result);
    }

    @Test
    void resolvePricingType_ShouldReturnOutgoingCallerServiced_WhenOnlyCallerServiced() throws BusinessException {
        request.setCallType("01");
        request.setReceiver(unservicedReceiver);

        PricingType result = callPricingService.resolvePricingType(request);

        assertEquals(PricingType.OUTGOING_CALLER_SERVICED, result);
    }

    @Test
    void resolvePricingType_ShouldReturnIncomingBothServiced_WhenBothServiced() throws BusinessException {
        request.setCallType("02");
        request.setReceiver(servicedReceiver);

        PricingType result = callPricingService.resolvePricingType(request);

        assertEquals(PricingType.INCOMING_BOTH_SERVICED, result);
    }

    @Test
    void resolvePricingType_ShouldReturnIncomingCallerServiced_WhenOnlyCallerServiced() throws BusinessException {
        request.setCallType("02");
        request.setReceiver(unservicedReceiver);

        PricingType result = callPricingService.resolvePricingType(request);

        assertEquals(PricingType.INCOMING_CALLER_SERVICED, result);
    }

    @Test
    void resolvePricingType_ShouldThrowException_WhenUnsupportedCallType() {
        request.setCallType("03");

        assertThrows(InvalidCallTypeException.class, () ->
                callPricingService.resolvePricingType(request)
        );
    }

    @Test
    void resolvePricingType_ShouldThrowException_WhenUnsupportedCombination() {
        request.setCallType("01");
        request.setCaller(unservicedCaller());

        assertThrows(UnsupportedCallServiceCombinationException.class, () ->
                callPricingService.resolvePricingType(request)
        );
    }

    @Test
    void applyCallPricing_ShouldAddToExistingCost_WhenCostAlreadySet() throws BusinessException {
        CallPricing pricing = new CallPricing(
                new CallPricingId(1L, 1),
                BigDecimal.valueOf(1.5),
                tariff
        );
        when(callPricingRepository.findByTariffId(anyLong()))
                .thenReturn(List.of(pricing));
        response.setCost(10.0);

        callPricingService.applyCallPricing(request, tariff, response);

        assertEquals(25.0, response.getCost(), 0.001);
    }

    @Test
    void applyCallPricing_ShouldHandleZeroDuration_WhenNoAdditionalCost() throws BusinessException {
        CallPricing pricing = new CallPricing(
                new CallPricingId(1L, 1),
                BigDecimal.valueOf(1.5),
                tariff
        );
        when(callPricingRepository.findByTariffId(anyLong()))
                .thenReturn(List.of(pricing));
        request.setDurationMinutes(0);
        response.setCost(10.0);

        callPricingService.applyCallPricing(request, tariff, response);

        assertEquals(10.0, response.getCost(), 0.001);
    }

    private Subscriber unservicedCaller() {
        return new Subscriber(null, "79990000000", false, null, null, null);
    }
}