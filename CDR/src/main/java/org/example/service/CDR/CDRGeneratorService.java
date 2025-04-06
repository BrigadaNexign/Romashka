package org.example.service.CDR;

import org.example.entity.CDR;
import org.example.entity.Subscriber;
import org.example.service.subscriber.SubscriberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Сервис для генерации CDR записей.
 * Предоставляет методы для создания CDR записей для абонентов за определенный период.
 */
@Service
public class CDRGeneratorService {

    @Autowired
    private CDRServiceImpl cdrService;

    @Autowired
    private SubscriberServiceImpl subscriberService;

    private final Random random = new Random();
    private static final int RECORDS_PER_FILE = 10;
    private static final int QUEUE_CAPACITY = 10;
    private static final int MAX_GENERATION_ATTEMPTS = 10;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final BlockingQueue<Optional<CDR>> cdrQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final AtomicInteger fileCounter = new AtomicInteger(1);
    private ExecutorService executorService;
    private List<Subscriber> subscribersList;

    public void generateAllBatches() {
        executorService = Executors.newFixedThreadPool(2);

        try {
            subscribersList = subscriberService.fetchSubscriberList();

            if (subscribersList == null || subscribersList.isEmpty()) {
                throw new IllegalStateException("No subscribers available for CDR generation");
            }

            Future<?> generationFuture = executorService.submit(this::generateCdrRecords);
            Future<?> processingFuture = executorService.submit(this::processCdrQueue);

            generationFuture.get();
            processingFuture.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CDR generation was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error during CDR generation", e.getCause());
        } finally {
            shutdownExecutorService();
        }
    }

    private void shutdownExecutorService() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void generateCdrRecords() {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusYears(1);
            LocalDateTime endDate = LocalDateTime.now();

            while (startDate.isBefore(endDate)) {
                CDR cdr = generateConflictFreeCDR(
                        subscribersList.get(random.nextInt(subscribersList.size())),
                        startDate
                );
                cdrQueue.put(Optional.of(cdr));
                startDate = startDate.plusMinutes(random.nextInt(60));
            }

            cdrQueue.put(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CDR records generation interrupted", e);
        }
    }

    public CDR generateConflictFreeCDR(Subscriber subscriber, LocalDateTime startTime) {
        String caller = subscriber.getMsisdn();
        String receiver = getRandomReceiverMsisdn(caller);

        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
            LocalDateTime endTime = startTime.plusSeconds(random.nextInt(3600));

            if (endTime.isBefore(startTime)) {
                throw new IllegalStateException("Generated invalid time range: endTime before startTime");
            }

            if (!cdrService.hasConflictingCalls(caller, receiver, startTime, endTime)) {
                CDR cdr = createCDR(random.nextBoolean() ? "01" : "02", caller, receiver, startTime, endTime);
                cdrService.saveCDR(cdr);
                return cdr;
            }

            startTime = endTime.plusSeconds(1);
        }
        throw new IllegalStateException(String.format(
                "Failed to create conflict-free CDR after %d attempts for subscriber %s",
                MAX_GENERATION_ATTEMPTS, caller));
    }

    private void processCdrQueue() {
        try {
            List<CDR> buffer = new ArrayList<>(RECORDS_PER_FILE);
            while (true) {
                Optional<CDR> cdrOpt = cdrQueue.take();

                if (cdrOpt.isEmpty()) {
                    if (!buffer.isEmpty()) {
                        writeCdrBatchToFile(buffer);
                    }
                    break;
                }

                buffer.add(cdrOpt.get());
                if (buffer.size() >= RECORDS_PER_FILE) {
                    writeCdrBatchToFile(buffer);
                    buffer = new ArrayList<>(RECORDS_PER_FILE);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CDR queue processing interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CDR batch to file", e);
        }
    }

    private void writeCdrBatchToFile(List<CDR> cdrBatch) throws IOException {
        Path reports = Paths.get("reports");
        if (!Files.exists(reports)) {
            Files.createDirectories(reports);
        }

        String filename = "reports/cdr_batch_" + fileCounter.getAndIncrement() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CDR cdr : cdrBatch) {
                writer.write(formatCdr(cdr));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to write CDR batch to file: " + filename, e);
        }
    }

    private String formatCdr(CDR cdr) {
        return String.join(",",
                cdr.getCallType(),
                cdr.getCallerMsisdn(),
                cdr.getReceiverMsisdn(),
                cdr.getStartTime().toString(),
                cdr.getEndTime().toString()
        );
    }

    public String getRandomReceiverMsisdn(String callerMsisdn) {
        List<Subscriber> possibleReceivers = subscribersList.stream()
                .filter(s -> !s.getMsisdn().equals(callerMsisdn))
                .toList();

        if (possibleReceivers.isEmpty()) {
            throw new IllegalStateException("No available receivers for caller: " + callerMsisdn);
        }

        return possibleReceivers.get(random.nextInt(possibleReceivers.size())).getMsisdn();
    }

    public CDR createCDR(
            String callType,
            String callerMsisdn,
            String receiverMsisdn,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        CDR cdr = new CDR();
        cdr.setCallType(callType);
        cdr.setCallerMsisdn(callerMsisdn);
        cdr.setReceiverMsisdn(receiverMsisdn);
        cdr.setStartTime(startTime);
        cdr.setEndTime(endTime);
        return cdr;
    }
}