package org.example.service.record;

import org.example.service.fragment.FragmentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class GenerationExecutor {

    private ExecutorService executorService;

    @Autowired
    private FragmentProcessor fragmentProcessor;

    @Autowired
    private RecordProcessor recordProcessor;

    private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

    public void generateAllBatches() {
        executorService = Executors.newFixedThreadPool(2);
        try {
            Future<?> generationFuture = executorService.submit(fragmentProcessor::generateAndPutToQueue);
            Future<?> processingFuture = executorService.submit(recordProcessor::processCdrQueue);

            generationFuture.get();
            processingFuture.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fragment generation was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error during Fragment generation", e.getCause());
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
}
