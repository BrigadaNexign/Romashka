package rom.cdr.service.record;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.service.fragment.FragmentGenerator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RecordGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RecordGenerator.class);
    private static final int FRAGMENT_GENERATION_THREADS = 10;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Autowired
    private FragmentGenerator fragmentGenerator;
    @Autowired
    private RecordProcessor recordProcessor;

    final ExecutorService fragmentExecutor = createThreadPool();

    @Async
    public void generateForPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Starting generation for period: {} - {}", startTime, endTime);
        long startMs = System.currentTimeMillis();

        try {
            List<CompletableFuture<List<Fragment>>> futures = scheduleGenerationTasks(startTime, endTime);
            processGeneratedFragments(futures, startMs);
        } catch (Exception e) {
            handleGenerationError(e);
        }
    }

    public List<CompletableFuture<List<Fragment>>> scheduleGenerationTasks(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        List<CompletableFuture<List<Fragment>>> futures = new ArrayList<>();
        LocalDateTime currentStart = startTime;

        while (currentStart.isBefore(endTime)) {
            LocalDateTime callEndTime = calculateCallEndTime(currentStart, endTime);
            futures.add(createFragmentGenerationTask(currentStart, callEndTime));
            currentStart = calculateNextStartTime(callEndTime);
        }

        logger.info("Scheduled {} fragment generation tasks", futures.size());
        return futures;
    }

    public LocalDateTime calculateCallEndTime(LocalDateTime currentStart, LocalDateTime endTime) {
        LocalDateTime callEndTime = currentStart.plusMinutes(1 + random.nextInt(59));
        return callEndTime.isAfter(endTime) ? endTime : callEndTime;
    }

    CompletableFuture<List<Fragment>> createFragmentGenerationTask(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        return CompletableFuture.supplyAsync(
                () -> fragmentGenerator.generateFragmentWithMidnightCheck(startTime, endTime),
                fragmentExecutor
        ).exceptionally(e -> {
            logger.error("Failed to generate fragment for interval {} - {}: {}",
                    startTime, endTime, e.getMessage());
            return Collections.emptyList();
        });
    }

    private LocalDateTime calculateNextStartTime(LocalDateTime callEndTime) {
        return callEndTime.plusMinutes(random.nextInt(60));
    }

    void processGeneratedFragments(
            List<CompletableFuture<List<Fragment>>> futures,
            long startMs
    ) {
        CompletableFuture<List<Fragment>> combinedFragments = combineFutures(futures);

        combinedFragments.thenAccept(fragments -> {
            logger.info("Processing {} generated fragments", fragments.size());
            recordProcessor.processAndSendFragments(fragments);
            logger.info("Successfully processed fragments. Total duration: {}ms",
                    System.currentTimeMillis() - startMs);
        }).exceptionally(e -> {
            logger.error("Failed to process fragments: {}", e.getMessage(), e);
            return null;
        });
    }

    CompletableFuture<List<Fragment>> combineFutures(
            List<CompletableFuture<List<Fragment>>> futures) {

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }

    private void handleGenerationError(Exception e) {
        logger.error("Critical error in generateForPeriod: {}", e.getMessage(), e);
    }

    private ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(FRAGMENT_GENERATION_THREADS,
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "fragment-gen-" + counter.getAndIncrement());
                    }
                });
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down fragment executor");
        fragmentExecutor.shutdown();
        try {
            if (!fragmentExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                fragmentExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            fragmentExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}