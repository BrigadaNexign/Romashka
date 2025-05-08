package rom.cdr.service.fragment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.cdr.entity.Fragment;
import rom.cdr.exceptions.EmptyFieldException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FragmentEditorTest {

    @InjectMocks
    private FragmentEditor fragmentEditor;

    private final LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 12, 0);
    private final LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 12, 5);
    private final String validCaller = "79991112233";
    private final String validReceiver = "79992223344";

    @Test
    void createFragment_Valid() {
        Fragment fragment = fragmentEditor.createFragment(
                "01", validCaller, validReceiver, startTime, endTime);

        assertAll(
                () -> assertEquals("01", fragment.getCallType()),
                () -> assertEquals(validCaller, fragment.getCallerMsisdn()),
                () -> assertEquals(validReceiver, fragment.getReceiverMsisdn()),
                () -> assertEquals(startTime, fragment.getStartTime()),
                () -> assertEquals(endTime, fragment.getEndTime())
        );
    }

    @Test
    void createFragment_Exception() {
        LocalDateTime invalidEndTime = startTime.minusMinutes(5);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fragmentEditor.createFragment(
                        "01", validCaller, validReceiver, startTime, invalidEndTime)
        );

        assertEquals("End time cannot be before start time", exception.getMessage());
    }

    @Test
    void createFragment_EqualStartEndTime() {
        assertDoesNotThrow(() -> {
            Fragment fragment = fragmentEditor.createFragment(
                    "02", validCaller, validReceiver, startTime, startTime);

            assertNotNull(fragment);
            assertEquals(startTime, fragment.getStartTime());
            assertEquals(startTime, fragment.getEndTime());
        });
    }

    @Test
    void formatFragment_ValidFormat() {
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn(validCaller);
        fragment.setReceiverMsisdn(validReceiver);
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);

        String expected = "[01, 79991112233, 79992223344, 2024-01-01T12:00:00, 2024-01-01T12:05:00]";
        String actual = Arrays.toString(fragmentEditor.formatFragment(fragment));

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideNullFragmentFields")
    void checkFragment_FragmentFieldNull(Fragment fragment, String expectedMessage) {
        EmptyFieldException exception = assertThrows(
                EmptyFieldException.class,
                () -> fragmentEditor.checkFragment(fragment)
        );
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private static Stream<Arguments> provideNullFragmentFields() {
        Fragment fullFragment = FragmentTestData.createTestFragment();

        return Stream.of(
                Arguments.of(createFragmentWithNullField(fullFragment, "callType"), "callType"),
                Arguments.of(createFragmentWithNullField(fullFragment, "callerMsisdn"), "callerMsisdn"),
                Arguments.of(createFragmentWithNullField(fullFragment, "receiverMsisdn"), "receiverMsisdn"),
                Arguments.of(createFragmentWithNullField(fullFragment, "startTime"), "startTime"),
                Arguments.of(createFragmentWithNullField(fullFragment, "endTime"), "endTime"),
                Arguments.of(null, "Fragment")
        );
    }

    private static Fragment createFragmentWithNullField(Fragment original, String fieldName) {
        Fragment fragment = new Fragment();
        fragment.setCallType(fieldName.equals("callType") ? null : original.getCallType());
        fragment.setCallerMsisdn(fieldName.equals("callerMsisdn") ? null : original.getCallerMsisdn());
        fragment.setReceiverMsisdn(fieldName.equals("receiverMsisdn") ? null : original.getReceiverMsisdn());
        fragment.setStartTime(fieldName.equals("startTime") ? null : original.getStartTime());
        fragment.setEndTime(fieldName.equals("endTime") ? null : original.getEndTime());
        return fragment;
    }
}
