package rom.cdr.service.record;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.cdr.entity.Fragment;
import rom.cdr.service.fragment.FragmentGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecordGeneratorTest {

    @Mock
    private FragmentGenerator fragmentGenerator;

    @Mock
    private RecordProcessor recordProcessor;

    @InjectMocks
    private RecordGenerator recordGenerator;

    private final LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
    private final LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 2, 0);

    @Test
    void generateForPeriod_Correct() throws Exception {
        Fragment testFragment = new Fragment();
        when(fragmentGenerator.generateFragmentWithMidnightCheck(any(), any()))
                .thenReturn(List.of(testFragment));

        recordGenerator.generateForPeriod(startTime, endTime.plusDays(7));

        Thread.sleep(500);

        verify(recordProcessor).processAndSendFragments(anyList());
        verify(fragmentGenerator, atLeastOnce()).generateFragmentWithMidnightCheck(any(), any());
    }

    @Test
    void scheduleGenerationTasks_CorrectNumberOfTasks() {
        List<CompletableFuture<List<Fragment>>> futures =
                recordGenerator.scheduleGenerationTasks(startTime, endTime);

        assertFalse(futures.isEmpty());
        assertTrue(futures.size() >= 2);
    }

    @Test
    void calculateCallEndTime_ShouldHandleEdgeCases() {
        LocalDateTime result1 = recordGenerator.calculateCallEndTime(
                startTime, endTime);
        assertTrue(result1.isAfter(startTime) && result1.isBefore(endTime));

        LocalDateTime edgeStart = endTime.minusSeconds(30);
        assertEquals(endTime, recordGenerator.calculateCallEndTime(edgeStart, endTime));
    }

    @Test
    void combineFutures_ShouldMergeAllResults() throws Exception {
        Fragment frag1 = new Fragment();
        Fragment frag2 = new Fragment();
        CompletableFuture<List<Fragment>> future1 = CompletableFuture.completedFuture(List.of(frag1));
        CompletableFuture<List<Fragment>> future2 = CompletableFuture.completedFuture(List.of(frag2));

        CompletableFuture<List<Fragment>> combined =
                recordGenerator.combineFutures(List.of(future1, future2));

        assertEquals(2, combined.get().size());
    }

    @Test
    void processGeneratedFragments_ShouldHandleEmptyResults() {
        CompletableFuture<List<Fragment>> emptyFuture =
                CompletableFuture.completedFuture(Collections.emptyList());

        recordGenerator.processGeneratedFragments(List.of(emptyFuture), 0);

        verify(recordProcessor).processAndSendFragments(List.of());
    }

    @Test
    void shutdown_ShouldTerminateExecutor() {
        recordGenerator.shutdown();

        assertTrue(recordGenerator.fragmentExecutor.isShutdown());
    }

    @Test
    void createFragmentGenerationTask_ShouldHandleAsyncExecution() throws Exception {
        Fragment testFragment = new Fragment();
        when(fragmentGenerator.generateFragmentWithMidnightCheck(any(), any()))
                .thenReturn(List.of(testFragment));

        CompletableFuture<List<Fragment>> future =
                recordGenerator.createFragmentGenerationTask(startTime, endTime);
        List<Fragment> result = future.get();

        assertEquals(1, result.size());
        assertEquals(testFragment, result.get(0));
    }

    @Test
    void generateForPeriod_ShouldCompleteWithinTimeout() {
        assertTimeout(Duration.ofSeconds(2), () -> {
            recordGenerator.generateForPeriod(startTime, endTime);
            Thread.sleep(500);
        });
    }
}