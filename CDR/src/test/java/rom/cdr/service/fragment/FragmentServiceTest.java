package rom.cdr.service.fragment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.provider.MethodSource;
import rom.cdr.exception.ConflictingCallsException;
import rom.cdr.repository.FragmentRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentServiceTest {
    @Mock
    private FragmentRepository fragmentRepository;
    @InjectMocks
    private FragmentService fragmentService;

    private final LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 12, 0);
    private final LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 12, 5);
    private final String caller = "79991112233";
    private final String receiver = "79992223344";

    @Test
    void hasConflictingCalls_ConflictExists() throws ConflictingCallsException {
        when(fragmentRepository.existsConflictingCalls(
                caller, receiver, startTime, endTime))
                .thenReturn(true);

        boolean result = fragmentService.hasConflictingCalls(
                caller, receiver, startTime, endTime);

        assertTrue(result);
        verify(fragmentRepository, times(1))
                .existsConflictingCalls(caller, receiver, startTime, endTime);
    }

    @Test
    void hasConflictingCalls_NoConflict() throws ConflictingCallsException {
        when(fragmentRepository.existsConflictingCalls(
                caller, receiver, startTime, endTime))
                .thenReturn(false);

        boolean result = fragmentService.hasConflictingCalls(
                caller, receiver, startTime, endTime);

        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("provideNullParameters")
    void hasConflictingCalls_AnyParameterIsNull(
            String caller, String receiver, LocalDateTime start, LocalDateTime end
    ) {
        assertThrows(ConflictingCallsException.class, () ->
                fragmentService.hasConflictingCalls(caller, receiver, start, end)
        );
    }

    @Test
    void hasConflictingCalls_EndBeforeStart() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusMinutes(5);

        ConflictingCallsException exception = assertThrows(
                ConflictingCallsException.class,
                () -> fragmentService.hasConflictingCalls("79991112233", "79992223344", start, end)
        );
    }

    @Test
    void hasConflictingCalls_ShouldAcceptEqualStartAndEndTime() {
        LocalDateTime time = LocalDateTime.now();
        when(fragmentRepository.existsConflictingCalls(any(), any(), any(), any()))
                .thenReturn(false);

        assertDoesNotThrow(() ->
                fragmentService.hasConflictingCalls("79991112233", "79992223344", time, time)
        );
    }

    @Test
    void hasConflictingCalls_ShouldWorkWithValidParameters() throws ConflictingCallsException {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);

        when(fragmentRepository.existsConflictingCalls("79991112233", "79992223344", start, end))
                .thenReturn(true);

        boolean result = fragmentService.hasConflictingCalls(
                "79991112233", "79992223344", start, end
        );

        assertTrue(result);
    }

    private static Stream<Arguments> provideNullParameters() {
        LocalDateTime validStart = LocalDateTime.now();
        LocalDateTime validEnd = validStart.plusMinutes(5);
        String validCaller = "79991112233";
        String validReceiver = "79992223344";

        return Stream.of(
                Arguments.of(null, validReceiver, validStart, validEnd),
                Arguments.of(validCaller, null, validStart, validEnd),
                Arguments.of(validCaller, validReceiver, null, validEnd),
                Arguments.of(validCaller, validReceiver, validStart, null)
        );
    }
}