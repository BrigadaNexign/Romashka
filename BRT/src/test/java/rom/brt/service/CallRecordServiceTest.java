package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import rom.brt.dto.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
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
                .durationMinutes(5) // 4:30 округляется до 5 минут
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
        // Given
        Fragment fragment = createTestFragmentWithDuration(4, 31); // 4 минуты 31 секунда
        CalculationResponse response = createTestResponse(10.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        // Then
        assertEquals(5, result.getDurationMinutes()); // Должно округлиться вверх
    }

    @Test
    void saveCallRecord_shouldHandleExactMinutes() {
        // Given
        Fragment fragment = createTestFragmentWithDuration(5, 0); // Ровно 5 минут
        CalculationResponse response = createTestResponse(10.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        // Then
        assertEquals(5, result.getDurationMinutes()); // Без округления
    }

    @Test
    void saveCallRecord_shouldHandleZeroCost() {
        // Given
        Fragment fragment = createTestFragment();
        CalculationResponse response = createTestResponse(0.0);

        when(callRecordRepository.save(any(CallRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CallRecord result = callRecordService.saveCallRecord(fragment, response);

        // Then
        assertEquals(0.0, result.getCost().doubleValue());
    }

    private Fragment createTestFragment() {
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(4).plusSeconds(30)); // 4 минуты 30 секунд
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