package rom.hrs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.Subscriber;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.BusinessException;
import rom.hrs.exception.IncompleteResponseException;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.exception.UnsupportedTariffTypeException;
import rom.hrs.service.tariff.TariffCalculator;
import rom.hrs.service.tariff.TariffCalculatorFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private TariffService tariffService;

    @Mock
    private TariffCalculatorFactory calculatorFactory;

    @Mock
    private ResponseBuilder responseBuilder;

    @Mock
    private TariffCalculator tariffCalculator;

    @InjectMocks
    private CalculationService calculationService;

    private CalculationRequest request;
    private Tariff tariff;
    private CalculationResponse basicResponse;
    private CalculationResponse fullResponse;

    @BeforeEach
    void setUp() {
        Subscriber caller = new Subscriber(1, "79991234567", true, 1L, 100, LocalDate.now());
        request = new CalculationRequest(
                "01",
                caller,
                new Subscriber(2, "79998765432", true, 2L, 50, LocalDate.now()),
                10,
                LocalDate.now()
        );

        tariff = new Tariff(1L, "Test", "Test Tariff", 1);
        basicResponse = new CalculationResponse();
        fullResponse = new CalculationResponse();
        fullResponse.setSuccess(true);
    }

    @Test
    void calculate_ShouldReturnSuccessResponse_WhenAllStepsCompleted() throws Exception {
        when(tariffService.findTariffById(anyLong())).thenReturn(tariff);
        when(calculatorFactory.getCalculator(any())).thenReturn(tariffCalculator);
        when(responseBuilder.initResponse(any(), any())).thenReturn(basicResponse);
        when(tariffCalculator.calculate(any(), any(), any())).thenReturn(basicResponse);
        when(responseBuilder.fillDefaultFields(any(), any(), any())).thenReturn(fullResponse);

        ResponseEntity<CalculationResponse> result = calculationService.calculate(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(fullResponse, result.getBody());
        verify(responseBuilder).validateSuccessfulResponse(fullResponse);
    }

    @Test
    void calculate_ShouldReturnNotFound_WhenTariffNotFound() throws NoTariffFoundException {
        when(tariffService.findTariffById(anyLong())).thenReturn(null);
        when(responseBuilder.createErrorResponse(any(NoTariffFoundException.class)))
                .thenReturn(new CalculationResponse());

        ResponseEntity<CalculationResponse> result = calculationService.calculate(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(responseBuilder).createErrorResponse(any(NoTariffFoundException.class));
    }

    @Test
    void calculate_ShouldHandleRuntimeException_WhenFillFieldsFails() throws BusinessException {
        RuntimeException exception = new RuntimeException("Fill fields error");
        when(tariffService.findTariffById(anyLong())).thenReturn(tariff);
        when(calculatorFactory.getCalculator(any())).thenReturn(tariffCalculator);
        when(responseBuilder.initResponse(any(), any())).thenReturn(basicResponse);
        when(tariffCalculator.calculate(any(), any(), any())).thenReturn(basicResponse);

        when(responseBuilder.fillDefaultFields(any(), any(), any()))
                .thenThrow(exception);

        when(responseBuilder.createErrorResponse(any(Exception.class)))
                .thenReturn(new CalculationResponse());

        ResponseEntity<CalculationResponse> result = calculationService.calculate(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(responseBuilder).createErrorResponse(exception);
    }

    @Test
    void calculate_ShouldHandleGenericException_WithInternalServerError() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(tariffService.findTariffById(anyLong())).thenReturn(tariff);
        when(calculatorFactory.getCalculator(any())).thenReturn(tariffCalculator);
        when(responseBuilder.initResponse(any(), any())).thenReturn(basicResponse);
        when(tariffCalculator.calculate(any(), any(), any())).thenThrow(exception);
        when(responseBuilder.createErrorResponse(any(RuntimeException.class)))
                .thenReturn(new CalculationResponse());

        ResponseEntity<CalculationResponse> result = calculationService.calculate(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(responseBuilder).createErrorResponse(exception);
    }

    @Test
    void calculate_ShouldCallAllServicesInCorrectOrder() throws Exception {
        when(tariffService.findTariffById(anyLong())).thenReturn(tariff);
        when(calculatorFactory.getCalculator(any())).thenReturn(tariffCalculator);
        when(responseBuilder.initResponse(any(), any())).thenReturn(basicResponse);
        when(tariffCalculator.calculate(any(), any(), any())).thenReturn(basicResponse);
        when(responseBuilder.fillDefaultFields(any(), any(), any())).thenReturn(fullResponse);

        calculationService.calculate(request);

        InOrder inOrder = inOrder(tariffService, calculatorFactory, responseBuilder, tariffCalculator);
        inOrder.verify(tariffService).findTariffById(request.getCaller().tariffId());
        inOrder.verify(calculatorFactory).getCalculator(tariff);
        inOrder.verify(responseBuilder).initResponse(request, tariff);
        inOrder.verify(tariffCalculator).calculate(request, tariff, basicResponse);
        inOrder.verify(responseBuilder).fillDefaultFields(request, tariff, basicResponse);
        inOrder.verify(responseBuilder).validateSuccessfulResponse(fullResponse);
    }
}