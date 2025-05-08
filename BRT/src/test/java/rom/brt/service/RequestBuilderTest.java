package rom.brt.service;

import org.junit.jupiter.api.Test;
import rom.brt.dto.Fragment;
import rom.brt.dto.Subscriber;
import rom.brt.dto.CalculationRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RequestBuilderTest {

    private final RequestBuilder requestBuilder = new RequestBuilder();

    @Test
    void build_shouldCreateCorrectRequestForOutgoingCall() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 10, 5, 30);

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001234567");
        fragment.setReceiverMsisdn("79007654321");
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001234567", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromServicedUser(2, "79007654321", 2, 200, LocalDate.now());

        CalculationRequest result = requestBuilder.build(fragment, caller, receiver);

        assertNotNull(result);
        assertEquals("01", result.getCallType());
        assertEquals(caller, result.getCaller());
        assertEquals(receiver, result.getReceiver());
        assertEquals(6, result.getDurationMinutes());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCurrentDate());
    }

    @Test
    void build_shouldCreateCorrectRequestForIncomingCall() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 10, 4, 59);

        Fragment fragment = new Fragment();
        fragment.setCallType("02");
        fragment.setCallerMsisdn("79001234567");
        fragment.setReceiverMsisdn("79007654321");
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001234567", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromServicedUser(2, "79007654321", 2, 200, LocalDate.now());

        CalculationRequest result = requestBuilder.build(fragment, caller, receiver);

        assertNotNull(result);
        assertEquals("02", result.getCallType());
        assertEquals(caller, result.getCaller());
        assertEquals(receiver, result.getReceiver());
        assertEquals(5, result.getDurationMinutes());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCurrentDate());
    }

    @Test
    void build_shouldHandleForeignReceiver() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 10, 1, 1);

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001234567");
        fragment.setReceiverMsisdn("79007654321");
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001234567", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromForeignUser("79007654321");

        CalculationRequest result = requestBuilder.build(fragment, caller, receiver);

        assertNotNull(result);
        assertEquals("01", result.getCallType());
        assertEquals(caller, result.getCaller());
        assertEquals(receiver, result.getReceiver());
        assertEquals(2, result.getDurationMinutes());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCurrentDate());
        assertFalse(receiver.isServiced());
    }

    @Test
    void build_shouldHandleExactMinuteDuration() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 10, 5, 0);

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001234567");
        fragment.setReceiverMsisdn("79007654321");
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001234567", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromServicedUser(2, "79007654321", 2, 200, LocalDate.now());

        CalculationRequest result = requestBuilder.build(fragment, caller, receiver);

        assertNotNull(result);
        assertEquals(5, result.getDurationMinutes());
    }

    @Test
    void build_shouldHandleLessThanMinuteDuration() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 18, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 10, 18, 1);

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001234567");
        fragment.setReceiverMsisdn("79007654321");
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001234567", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromServicedUser(2, "79007654321", 2, 200, LocalDate.now());

        CalculationRequest result = requestBuilder.build(fragment, caller, receiver);

        assertNotNull(result);
        assertEquals(1, result.getDurationMinutes());
    }
}