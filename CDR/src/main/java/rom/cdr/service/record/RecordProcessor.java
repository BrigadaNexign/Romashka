package rom.cdr.service.record;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.exception.EmptyFieldException;
import rom.cdr.service.fragment.FragmentEditor;
import rom.cdr.service.sender.ReportQueueSender;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecordProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RecordProcessor.class);
    private static final int CDR_BATCH_SIZE = 10;
    private static final String[] HEADER = {"call_type", "caller_msisdn", "receiver_msisdn", "start_time", "end_time"};

    @Autowired
    private FragmentEditor fragmentEditor;
    @Autowired
    private ReportQueueSender reportQueueSender;

    public void processAndSendFragments(List<Fragment> fragments) {
        fragments.sort(Comparator.comparing(Fragment::getStartTime));
        sendBatches(fragments);
    }

    public void sendBatches(List<Fragment> dailyFragments) {
        if (isInvalidInput(dailyFragments)) {
            logger.warn("Empty fragments list provided");
            return;
        }

        List<String[]> currentBatchRecords = new ArrayList<>();
        currentBatchRecords.add(HEADER);

        for (int i = 0; i < dailyFragments.size(); i++) {
            try {
                Fragment fragment = dailyFragments.get(i);
                fragmentEditor.checkFragment(fragment);
                currentBatchRecords.add(fragmentEditor.formatFragment(fragment));

                if (currentBatchRecords.size()  == CDR_BATCH_SIZE + 1) {
                    sendCurrentBatch(currentBatchRecords, i);
                    currentBatchRecords = new ArrayList<>();
                    currentBatchRecords.add(HEADER);
                }
            } catch (EmptyFieldException e) {
                logger.error("Fragment skipped (index {}): {}", i, e.getMessage());
            }
        }
    }

    private void sendCurrentBatch(List<String[]> batchRecords, int currentIndex) {
        try (StringWriter sw = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(sw)) {

            csvWriter.writeAll(batchRecords);
            reportQueueSender.sendReport(sw.toString());

            int batchNumber = currentIndex / CDR_BATCH_SIZE + 1;
            int successCount = batchRecords.size() - 1; // минус заголовок

            logger.info("Sent batch {} with {} records", batchNumber, successCount);
        } catch (Exception e) {
            logger.error("Failed to send batch", e);
        }
    }

    private boolean isInvalidInput(List<Fragment> fragments) {
        return fragments == null || fragments.isEmpty();
    }
}

