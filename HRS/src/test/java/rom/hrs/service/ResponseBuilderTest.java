package rom.hrs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.Subscriber;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.BusinessException;
import rom.hrs.exception.IncompleteResponseException;
import rom.hrs.exception.InvalidCallTypeException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @InjectMocks
    private ResponseBuilder responseBuilder;

    private CalculationRequest request;
    private Tariff tariff;
    private CalculationResponse response;
    private Subscriber caller;

    @BeforeEach
    void setUp() {
        caller = new Subscriber(1, "79991234567", true, 1L, 100, LocalDate.now());
        request = new CalculationRequest(
                "01",
                caller,
                new Subscriber(2, "79998765432", true, 2L, 50, LocalDate.now()),
                10,
                LocalDate.now()
        );
        tariff = new Tariff(1L, "Test", "Test Tariff", 1);
        response = new CalculationResponse();
    }

    @Test
    void initResponse_ShouldCreateResponseWithCorrectInitialValues() {
        CalculationResponse result = responseBuilder.initResponse(request, tariff);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(0.0, result.getCost());
        assertEquals(String.valueOf(tariff.getType()), result.getTariffType());
        assertEquals(tariff.getDescription(), result.getDescription());
        assertEquals(caller.minutes(), result.getRemainingMinutes());
        assertEquals(caller.paymentDay(), result.getNextPaymentDate());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());
    }

    @Test
    void fillDefaultFields_ShouldSetSuccessAndDefaultValues() {
        response.setSuccess(false);
        response.setNextPaymentDate(null);

        CalculationResponse result = responseBuilder.fillDefaultFields(request, tariff, response);

        assertSame(response, result);
        assertTrue(result.isSuccess());
        assertEquals(tariff.getDescription(), result.getDescription());
        assertEquals(String.valueOf(tariff.getType()), result.getTariffType());
        assertEquals(caller.paymentDay(), result.getNextPaymentDate());
    }

    @Test
    void fillDefaultFields_ShouldNotOverrideExistingNextPaymentDate() {
        LocalDate customDate = LocalDate.now().plusDays(10);
        response.setNextPaymentDate(customDate);

        CalculationResponse result = responseBuilder.fillDefaultFields(request, tariff, response);

        assertEquals(customDate, result.getNextPaymentDate());
    }

    @Test
    void createErrorResponse_ShouldCreateBusinessErrorResponse() {
        BusinessException exception = new InvalidCallTypeException("Invalid call type");

        CalculationResponse result = responseBuilder.createErrorResponse(exception);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(exception.getErrorCode().getCode(), result.getErrorCode());
        assertEquals(exception.getMessage(), result.getErrorMessage());
    }

    @Test
    void createErrorResponse_ShouldCreateGenericErrorResponse() {
        Exception exception = new RuntimeException("Unexpected error");

        CalculationResponse result = responseBuilder.createErrorResponse(exception);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("INTERNAL_ERROR", result.getErrorCode());
        assertEquals("Internal server error", result.getErrorMessage());
    }

    @Test
    void validateSuccessfulResponse_ShouldNotThrow_WhenAllRequiredFieldsPresent() {
        response.setSuccess(true);
        response.setCost(10.0);
        response.setTariffType("1");
        response.setRemainingMinutes(100);
        response.setNextPaymentDate(LocalDate.now());

        assertDoesNotThrow(() ->
                responseBuilder.validateSuccessfulResponse(response,
                        "cost", "tariffType", "remainingMinutes", "nextPaymentDate")
        );
    }

    @Test
    void validateSuccessfulResponse_ShouldThrow_WhenResponseIsNull() {
        IncompleteResponseException exception = assertThrows(IncompleteResponseException.class,
                () -> responseBuilder.validateSuccessfulResponse(null, "cost"));

        assertTrue(exception.getMessage().contains("All fields"));
    }

    @Test
    void validateSuccessfulResponse_ShouldNotValidate_WhenNotSuccess() {
        response.setSuccess(false);

        assertDoesNotThrow(() ->
                responseBuilder.validateSuccessfulResponse(response, "cost")
        );
    }

    @Test
    void validateSuccessfulResponse_ShouldThrow_WhenFieldsMissing() {
        response.setSuccess(true);
        response.setCost(null);
        response.setTariffType(null);
        response.setRemainingMinutes(100);
        response.setNextPaymentDate(LocalDate.now());

        IncompleteResponseException exception = assertThrows(IncompleteResponseException.class,
                () -> responseBuilder.validateSuccessfulResponse(response,
                        "cost", "tariffType", "remainingMinutes", "nextPaymentDate"));

        assertTrue(exception.getMessage().contains("cost"));
        assertTrue(exception.getMessage().contains("tariffType"));
        assertFalse(exception.getMessage().contains("remainingMinutes"));
        assertFalse(exception.getMessage().contains("nextPaymentDate"));
    }

    @Test
    void validateSuccessfulResponse_ShouldThrow_WhenUnknownFieldRequested() {
        response.setSuccess(true);

        assertThrows(IllegalArgumentException.class,
                () -> responseBuilder.validateSuccessfulResponse(response, "unknownField"));
    }
}