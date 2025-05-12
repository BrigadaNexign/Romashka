package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.brt.dto.response.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
import rom.brt.entity.User;
import rom.brt.exception.BusinessException;
import rom.brt.exception.EmptyResponseFieldException;
import rom.brt.exception.ErrorCode;
import rom.brt.exception.FailedResponseException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseHandlerTest {
    @Mock
    private CallRecordService callRecordService;
    @Mock
    private BillingService billingService;
    @InjectMocks
    private ResponseHandler responseHandler;

    @Test
    void handleCalculationResponse_shouldProcessValidResponse() throws BusinessException {
        User caller = new User();
        Fragment fragment = new Fragment();
        CalculationResponse response = createValidResponse();

        CallRecord expectedRecord = new CallRecord();
        when(callRecordService.saveCallRecord(fragment, response)).thenReturn(expectedRecord);

        responseHandler.handleCalculationResponse(caller, fragment, response);

        verify(billingService).processBilling(caller, response);
        verify(callRecordService).saveCallRecord(fragment, response);
    }

    @Test
    void handleCalculationResponse_shouldThrowWhenResponseIsNull() {
        assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.handleCalculationResponse(new User(), new Fragment(), null));
    }

    @Test
    void handleCalculationResponse_shouldThrowWhenResponseNotSuccessful() {
        CalculationResponse failedResponse = createValidResponse();
        failedResponse.setSuccess(false);
        failedResponse.setErrorCode("ERROR_01");
        failedResponse.setErrorMessage("Calculation failed");

        FailedResponseException exception = assertThrows(FailedResponseException.class,
                () -> responseHandler.handleCalculationResponse(new User(), new Fragment(), failedResponse));

        assertEquals(ErrorCode.FAILED_RESPONSE, exception.getErrorCode());
    }

    @Test
    void validate_shouldPassForValidResponse() throws BusinessException {
        responseHandler.validate(createValidResponse());
    }

    @Test
    void validate_shouldThrowWhenCostIsNull() {
        CalculationResponse response = createValidResponse();
        response.setCost(null);

        EmptyResponseFieldException exception = assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.validate(response));

        assertTrue(exception.getMessage().contains("cost"));
    }

    @Test
    void validate_shouldThrowWhenTariffTypeIsNull() {
        CalculationResponse response = createValidResponse();
        response.setTariffType(null);

        EmptyResponseFieldException exception = assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.validate(response));

        assertTrue(exception.getMessage().contains("tariffType"));
    }

    @Test
    void validate_shouldThrowWhenDescriptionIsNull() {
        CalculationResponse response = createValidResponse();
        response.setDescription(null);

        EmptyResponseFieldException exception = assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.validate(response));

        assertTrue(exception.getMessage().contains("description"));
    }

    @Test
    void validate_shouldThrowWhenRemainingMinutesIsNull() {
        CalculationResponse response = createValidResponse();
        response.setRemainingMinutes(null);

        EmptyResponseFieldException exception = assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.validate(response));

        assertTrue(exception.getMessage().contains("remainingMinutes"));
    }

    @Test
    void validate_shouldThrowWhenNextPaymentDateIsNull() {
        CalculationResponse response = createValidResponse();
        response.setNextPaymentDate(null);

        EmptyResponseFieldException exception = assertThrows(EmptyResponseFieldException.class,
                () -> responseHandler.validate(response));

        assertTrue(exception.getMessage().contains("nextPaymentDate"));
    }

    private CalculationResponse createValidResponse() {
        return CalculationResponse.builder()
                .success(true)
                .cost(10.0)
                .tariffType("type")
                .description("description")
                .remainingMinutes(100)
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .build();
    }
}