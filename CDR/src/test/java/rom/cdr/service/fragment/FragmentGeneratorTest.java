package rom.cdr.service.fragment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.cdr.entity.Fragment;
import rom.cdr.exception.ConflictingCallsException;
import rom.cdr.service.subscriber.SubscriberService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentGeneratorTest {
    @Mock
    private SubscriberService subscriberService;
    @Mock
    private FragmentEditor fragmentEditor;
    @Mock
    private FragmentService fragmentService;

    @InjectMocks
    private FragmentGenerator fragmentGenerator;

    private final LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 23, 30);
    private final LocalDateTime endTime = LocalDateTime.of(2024, 1, 2, 0, 30);
    private final Fragment testFragment = new Fragment();

    @BeforeEach
    void setUp() throws Exception {
        setPrivateMsisdns(Arrays.asList("79991112233", "79992223344", "79993334455"));

        testFragment.setCallType("01");
        testFragment.setCallerMsisdn("79991112233");
        testFragment.setReceiverMsisdn("79992223344");
        testFragment.setStartTime(startTime);
        testFragment.setEndTime(endTime);
    }

    @Test
    void generateFragmentWithMidnightCheck_CrossingMidnight() throws ConflictingCallsException {
        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.saveFragment(any())).thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(false);

        List<Fragment> result = fragmentGenerator.generateFragmentWithMidnightCheck(
                startTime, endTime);

        assertEquals(4, result.size());
        verify(fragmentService, times(4)).saveFragment(any());
    }

    @Test
    void generateFragmentWithMidnightCheck_SingleDay() throws ConflictingCallsException {
        LocalDateTime sameDayEnd = startTime.plusMinutes(30);
        testFragment.setStartTime(sameDayEnd);

        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.saveFragment(any())).thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(false);

        List<Fragment> result = fragmentGenerator.generateFragmentWithMidnightCheck(
                sameDayEnd, endTime);

        assertEquals(2, result.size());
    }

    @Test
    void generateFragmentWithMidnightCheck_WhenConflict() throws ConflictingCallsException {
        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(true);

        List<Fragment> result = fragmentGenerator.generateFragmentWithMidnightCheck(
                startTime, endTime);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideEdgeCases")
    void generateFragmentWithMidnightCheck_EdgeCases(
            LocalDateTime start, LocalDateTime end, int expectedCount) throws ConflictingCallsException {

        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.saveFragment(any())).thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(false);

        List<Fragment> result = fragmentGenerator.generateFragmentWithMidnightCheck(start, end);

        assertEquals(expectedCount, result.size());
    }

    @Test
    void generateFragmentWithMidnightCheck_NullException() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () ->
                        fragmentGenerator.generateFragmentWithMidnightCheck(null, endTime)),
                () -> assertThrows(NullPointerException.class, () ->
                        fragmentGenerator.generateFragmentWithMidnightCheck(startTime, null))
        );
    }

    private static Stream<Arguments> provideEdgeCases() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 12, 0);
        return Stream.of(
                Arguments.of(time, time, 2),

                Arguments.of(time, time.plusNanos(1), 2),

                Arguments.of(
                        LocalDateTime.MIN,
                        LocalDateTime.MAX,
                        4)
        );
    }

    @Test
    void handleMidnightCrossing() throws ConflictingCallsException {
        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.saveFragment(any())).thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(false);

        List<Fragment> result = fragmentGenerator.handleMidnightCrossing(startTime, endTime);

        assertEquals(4, result.size());
    }

    @Test
    void handleSingleDayFragment() throws ConflictingCallsException {
        when(fragmentEditor.createFragment(any(), any(), any(), any(), any()))
                .thenReturn(testFragment);
        when(fragmentService.saveFragment(any())).thenReturn(testFragment);
        when(fragmentService.hasConflictingCalls(any(), any(), any(), any())).thenReturn(false);

        List<Fragment> result = fragmentGenerator.handleSingleDayFragment(startTime, endTime);

        assertEquals(2, result.size());
    }

    @Test
    void createMirrorFragment() {
        Fragment original = new Fragment();
        original.setCallType("01");
        original.setCallerMsisdn("79991112233");
        original.setReceiverMsisdn("79992223344");

        Fragment mirrored = new Fragment();
        mirrored.setCallType("02");
        mirrored.setCallerMsisdn("79992223344");
        mirrored.setReceiverMsisdn("79991112233");

        when(fragmentEditor.createFragment("02", "79992223344", "79991112233",
                original.getStartTime(), original.getEndTime()))
                .thenReturn(mirrored);

        Fragment result = fragmentGenerator.createMirrorFragment(original);

        assertEquals("02", result.getCallType());
        assertEquals("79992223344", result.getCallerMsisdn());
        assertEquals("79991112233", result.getReceiverMsisdn());
    }

    @Test
    void createAfterMidnightFragment() {
        LocalDateTime newStart = endTime.toLocalDate().atStartOfDay();
        Fragment expected = new Fragment();
        expected.setCallType(testFragment.getCallType());
        expected.setCallerMsisdn(testFragment.getCallerMsisdn());
        expected.setReceiverMsisdn(testFragment.getReceiverMsisdn());
        expected.setStartTime(newStart);
        expected.setEndTime(endTime);

        when(fragmentEditor.createFragment(
                testFragment.getCallType(),
                testFragment.getCallerMsisdn(),
                testFragment.getReceiverMsisdn(),
                newStart,
                endTime))
                .thenReturn(expected);

        Fragment result = fragmentGenerator.createAfterMidnightFragment(testFragment, endTime);

        assertEquals(newStart, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
    }

    @Test
    void checkConflicts_NoConflict() throws ConflictingCallsException {
        when(fragmentService.hasConflictingCalls(
                testFragment.getCallerMsisdn(),
                testFragment.getReceiverMsisdn(),
                testFragment.getStartTime(),
                testFragment.getEndTime()))
                .thenReturn(false);

        boolean result = fragmentGenerator.checkConflicts(testFragment);

        assertTrue(result);
    }

    @Test
    void checkConflicts_ConflictExists() throws ConflictingCallsException {
        when(fragmentService.hasConflictingCalls(
                testFragment.getCallerMsisdn(),
                testFragment.getReceiverMsisdn(),
                testFragment.getStartTime(),
                testFragment.getEndTime()))
                .thenReturn(true);

        boolean result = fragmentGenerator.checkConflicts(testFragment);

        assertFalse(result);
    }

    @Test
    void hasConflicts() throws ConflictingCallsException {
        when(fragmentService.hasConflictingCalls(
                testFragment.getCallerMsisdn(),
                testFragment.getReceiverMsisdn(),
                testFragment.getStartTime(),
                testFragment.getEndTime()))
                .thenReturn(true);

        boolean result = fragmentGenerator.hasConflicts(testFragment);

        assertTrue(result);
        verify(fragmentService).hasConflictingCalls(
                testFragment.getCallerMsisdn(),
                testFragment.getReceiverMsisdn(),
                testFragment.getStartTime(),
                testFragment.getEndTime());
    }

    private void setPrivateMsisdns(List<String> msisdns) throws Exception {
        Field field = FragmentGenerator.class.getDeclaredField("msisdns");
        field.setAccessible(true);
        field.set(fragmentGenerator, msisdns);
    }
}