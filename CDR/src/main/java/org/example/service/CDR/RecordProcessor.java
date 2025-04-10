package org.example.service.CDR;

import lombok.AllArgsConstructor;
import org.example.entity.Fragment;
import org.example.service.fragment.FragmentEditor;
import org.example.service.sender.ReportQueueSender;
import org.example.util.FragmentBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class RecordProcessor {
    @Autowired
    private FragmentEditor fragmentEditor;
    @Autowired
    private final FragmentBlockingQueue fragmentQueue;
    @Autowired
    private ReportQueueSender reportQueueSender;

    private static final int RECORDS_PER_FILE = 10;
    private final AtomicInteger fileCounter = new AtomicInteger(1);

    public void processCdrQueue() {
        try {
            List<Fragment> buffer = new ArrayList<>(RECORDS_PER_FILE);

            while (true) {
                Optional<Fragment> cdrOpt = fragmentQueue.take();

                if (cdrOpt.isEmpty()) {
                    break;
                }

                buffer.add(cdrOpt.get());
                if (buffer.size() >= RECORDS_PER_FILE) {
                    writeSortedBatchToFile(buffer);
                    buffer = new ArrayList<>(RECORDS_PER_FILE);

                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fragment queue processing interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Fragment batch to file", e);
        }
    }

    private void writeSortedBatchToFile(List<Fragment> fragments) throws IOException {
        fragments.sort(Comparator.comparing(Fragment::getStartTime));

        Path reports = Paths.get("reports");
        if (!Files.exists(reports)) {
            Files.createDirectories(reports);
        }

        String filename = "reports/cdr_batch_" + fileCounter.getAndIncrement() + ".txt";

        reportQueueSender.sendReport(recordToMessage(fragments));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Fragment fragment : fragments) {
                writer.write(fragmentEditor.formatFragment(fragment));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to write Fragment batch to file: " + filename, e);
        }
    }

    public String recordToMessage(List<Fragment> fragments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Fragment fragment: fragments) {
            stringBuilder.append(fragmentEditor.formatFragment(fragment)).append("\n");
        }
        return stringBuilder.toString();
    }
}