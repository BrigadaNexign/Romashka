package rom.cdr.service.record;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.service.fragment.FragmentEditor;
import rom.cdr.service.fragment.FragmentService;
import rom.cdr.service.sender.ReportQueueSender;
import rom.cdr.service.subscriber.SubscriberService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RecordGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RecordGenerator.class);
    private static final int FRAGMENT_GENERATION_THREADS = 10;
    private static final int CDR_BATCH_SIZE = 10;

    private final SubscriberService subscriberService;
    private final FragmentService fragmentService;
    private final FragmentEditor fragmentEditor;
    private final ThreadLocalRandom random;
    private final ExecutorService fragmentExecutor;
    private final ReportQueueSender reportQueueSender;

    public RecordGenerator(SubscriberService subscriberService,
                           FragmentService fragmentService,
                           FragmentEditor fragmentEditor,
                           ReportQueueSender reportQueueSender) {
        this.subscriberService = subscriberService;
        this.fragmentService = fragmentService;
        this.fragmentEditor = fragmentEditor;
        this.reportQueueSender = reportQueueSender;
        this.random = ThreadLocalRandom.current();
        this.fragmentExecutor = Executors.newFixedThreadPool(FRAGMENT_GENERATION_THREADS,
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "fragment-gen-" + counter.getAndIncrement());
                    }
                });
    }

    @Async
    public CompletableFuture<Void> generateForPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Starting generation for period: {} - {}", startTime, endTime);
        long startMs = System.currentTimeMillis();

        try {
            logger.debug("Retrieving all MSISDNs");
            List<String> msisdns = subscriberService.getAllMsisdns();
            logger.info("Retrieved {} MSISDNs for generation", msisdns.size());

            List<CompletableFuture<List<Fragment>>> fragmentFutures = new ArrayList<>();
            LocalDateTime currentStart = startTime;

            while (currentStart.isBefore(endTime)) {
                LocalDateTime callEndTime = currentStart.plusMinutes(1 + random.nextInt(59));
                callEndTime = callEndTime.isAfter(endTime) ? endTime : callEndTime;

                logger.debug("Generating fragment for interval: {} - {}", currentStart, callEndTime);

                LocalDateTime finalStart = currentStart;
                LocalDateTime finalEnd = callEndTime;

                CompletableFuture<List<Fragment>> future = CompletableFuture.supplyAsync(
                        () -> generateFragmentWithMidnightCheck(finalStart, finalEnd, msisdns),
                        fragmentExecutor
                ).exceptionally(e -> {
                    logger.error("Failed to generate fragment for interval {} - {}: {}",
                            finalStart, finalEnd, e.getMessage());
                    return Collections.emptyList();
                });

                fragmentFutures.add(future);
                currentStart = callEndTime.plusMinutes(random.nextInt(120));
            }

            logger.info("Scheduled {} fragment generation tasks", fragmentFutures.size());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    fragmentFutures.toArray(new CompletableFuture[0])
            );

            CompletableFuture<List<Fragment>> combinedFragments = allFutures.thenApply(v -> {
                List<Fragment> result = fragmentFutures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                logger.debug("Combined {} fragments from all tasks", result.size());
                return result;
            });

            combinedFragments.thenAccept(fragments -> {
                try {
                    logger.info("Processing {} generated fragments", fragments.size());
                    processAndSendFragments(fragments);
                    logger.info("Successfully processed fragments. Total duration: {}ms",
                            System.currentTimeMillis() - startMs);
                } catch (Exception e) {
                    logger.error("Failed to process fragments: {}", e.getMessage(), e);
                }
            });

            return combinedFragments.thenApply(f -> null);
        } catch (Exception e) {
            logger.error("Critical error in generateForPeriod: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<Fragment> generateFragmentWithMidnightCheck(
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<String> msisdns) {
        try {
            logger.debug("Generating fragment between {} and {}", startTime, endTime);
            List<Fragment> result = new ArrayList<>();

            if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
                logger.debug("Fragment crosses midnight");
                Fragment firstPart = createRandomFragment(
                        startTime,
                        startTime.toLocalDate().atTime(23, 59, 59),
                        msisdns
                );

                Fragment secondPart = fragmentEditor.createFragment(
                        firstPart.getCallType(),
                        firstPart.getCallerMsisdn(),
                        firstPart.getReceiverMsisdn(),
                        endTime.toLocalDate().atStartOfDay(),
                        endTime
                );

                if (checkAndLogConflicts(firstPart) && checkAndLogConflicts(secondPart)) {
                    logger.debug("Saving fragments crossing midnight");
                    result.add(saveFragmentWithLogging(firstPart));
                    result.add(saveFragmentWithLogging(createMirrorFragment(firstPart)));
                    result.add(saveFragmentWithLogging(secondPart));
                    result.add(saveFragmentWithLogging(createMirrorFragment(secondPart)));
                }
            } else {
                Fragment fragment = createRandomFragment(startTime, endTime, msisdns);
                if (checkAndLogConflicts(fragment)) {
                    logger.debug("Saving single day fragment");
                    result.add(saveFragmentWithLogging(fragment));
                    result.add(saveFragmentWithLogging(createMirrorFragment(fragment)));
                }
            }

            logger.debug("Generated {} fragments for interval {} - {}",
                    result.size(), startTime, endTime);
            return result;
        } catch (Exception e) {
            logger.error("Error in generateFragmentWithMidnightCheck: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean checkAndLogConflicts(Fragment fragment) {
        synchronized(getLockObject(fragment.getCallerMsisdn(), fragment.getReceiverMsisdn())) {
            boolean hasConflict = hasConflicts(fragment);
            if (hasConflict) {
                logger.warn("Conflict detected for call between {} and {} ({} - {})",
                        fragment.getCallerMsisdn(),
                        fragment.getReceiverMsisdn(),
                        fragment.getStartTime(),
                        fragment.getEndTime());
            }
            return !hasConflict;
        }
    }

    private Object getLockObject(String msisdn1, String msisdn2) {
        String[] keys = {msisdn1, msisdn2};
        Arrays.sort(keys);
        return (keys[0] + "|" + keys[1]).intern();
    }

    private Fragment saveFragmentWithLogging(Fragment fragment) {
        try {
            logger.debug("Saving fragment: {}", fragment);
            Fragment saved = fragmentService.saveCDR(fragment);
            logger.trace("Fragment saved successfully: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Failed to save fragment: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Fragment createRandomFragment(
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<String> msisdns
    ) {
        return fragmentEditor.createFragment(
                random.nextBoolean() ? "01" : "02",
                getRandomMsisdn(msisdns),
                getRandomMsisdn(msisdns),
                startTime,
                endTime
        );
    }

    private void processAndSendFragments(List<Fragment> fragments) {
        fragments.stream()
                .sorted(Comparator.comparing(Fragment::getStartTime))
                .collect(Collectors.groupingBy(
                        fragment -> fragment.getStartTime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .forEach(this::sendBatches);
    }

    private void sendBatches(List<Fragment> dailyFragments) {
        for (int i = 0; i < dailyFragments.size(); i += CDR_BATCH_SIZE) {
            int endIndex = Math.min(i + CDR_BATCH_SIZE, dailyFragments.size());
            List<Fragment> batch = dailyFragments.subList(i, endIndex);

            if (batch.size() >= 10) {
                String cdrContent = batch.stream()
                        .map(fragmentEditor::formatFragment)
                        .collect(Collectors.joining("\n"));

                reportQueueSender.sendReport(cdrContent);
            }
        }
    }

    private boolean hasConflicts(Fragment fragment) {
        return fragmentService.hasConflictingCalls(
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime()
        );
    }

    private Fragment createMirrorFragment(Fragment original) {
        return fragmentEditor.createFragment(
                original.getCallType().equals("01") ? "02" : "01",
                original.getReceiverMsisdn(),
                original.getCallerMsisdn(),
                original.getStartTime(),
                original.getEndTime()
        );
    }

    private String getRandomMsisdn(List<String> msisdns) {
        return msisdns.get(random.nextInt(msisdns.size()));
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