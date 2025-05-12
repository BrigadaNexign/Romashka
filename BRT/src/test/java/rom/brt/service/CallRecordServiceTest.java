package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import rom.brt.dto.response.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
import rom.brt.exception.DuplicateCallException;
import rom.brt.repository.CallRecordRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallRecordServiceTest {

    @Mock
    private CallRecordRepository callRecordRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private CallRecordService callRecordService;

    @Test
    void saveCallRecord_shouldSaveRecordWithCorrectData() {
        Fragment fragment = createTestFragment();
        CalculationResponse response = createTestResponse(15.5);

        CallRecord expectedRecord = CallRecord.builder()
                .callType("01")
                .callerMsisdn("79001112233")
                .receiverMsisdn("79002223344")
                .startTime(fragment.getStartTime())
                .endTime(fragment.getEndTime())
                .durationMinutes(5)
                .cost(BigDecimal.valueOf(15.5))
                .build();

        when(callRecordRepository.save(any(CallRecord.class))).thenReturn(expectedRecord);

        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        assertNotNull(result);
        assertEquals("01", result.getCallType());
        assertEquals("79001112233", result.getCallerMsisdn());
        assertEquals("79002223344", result.getReceiverMsisdn());
        assertEquals(5, result.getDurationMinutes());
        assertEquals(BigDecimal.valueOf(15.5), result.getCost());

        verify(callRecordRepository).save(any(CallRecord.class));
    }

    @Test
    void saveCallRecord_shouldRoundUpDurationMinutes() {
        Fragment fragment = createTestFragmentWithDuration(4, 31);
        CalculationResponse response = createTestResponse(10.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        assertEquals(5, result.getDurationMinutes());
    }

    @Test
    void saveCallRecord_shouldHandleExactMinutes() {
        Fragment fragment = createTestFragmentWithDuration(5, 0);
        CalculationResponse response = createTestResponse(10.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        assertEquals(5, result.getDurationMinutes());
    }

    @Test
    void saveCallRecord_shouldHandleZeroCost() {
        Fragment fragment = createTestFragment();
        CalculationResponse response = createTestResponse(0.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        assertEquals(0.0, result.getCost().doubleValue());
    }

    @Test
    void existsDuplicate_shouldReturnTrue_whenDuplicateExists() {
        // Given
        Fragment fragment = createTestFragment();
        when(callRecordRepository.existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime()
        )).thenReturn(true);

        // When
        boolean result = callRecordService.existsDuplicate(fragment);

        // Then
        assertTrue(result);
        verify(callRecordRepository).existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime()
        );
    }

    @Test
    void existsDuplicate_shouldReturnFalse_whenNoDuplicateExists() {
        // Given
        Fragment fragment = createTestFragment();
        when(callRecordRepository.existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        // When
        boolean result = callRecordService.existsDuplicate(fragment);

        // Then
        assertFalse(result);
    }

    @Test
    void existsDuplicate_shouldHandleNullFragment() {
        assertThrows(NullPointerException.class, () -> callRecordService.existsDuplicate(null));
    }

    @Test
    void existsDuplicate_shouldHandlePartialNullFields() {
        // Given
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn(null);
        fragment.setReceiverMsisdn("79002223344");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(null);

        // When/Then
        assertDoesNotThrow(() -> callRecordService.existsDuplicate(fragment));
        verify(callRecordRepository).existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
                "01", null, "79002223344", fragment.getStartTime(), null
        );
    }

    private Fragment createTestFragment() {
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(4).plusSeconds(30));
        return fragment;
    }

    private Fragment createTestFragmentWithDuration(int minutes, int seconds) {
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(minutes).plusSeconds(seconds));
        return fragment;
    }

    private CalculationResponse createTestResponse(double cost) {
        return CalculationResponse.builder()
                .cost(cost)
                .build();
    }
}