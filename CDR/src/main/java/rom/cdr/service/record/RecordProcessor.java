package rom.cdr.service.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.service.fragment.FragmentEditor;
import rom.cdr.service.sender.ReportQueueSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RecordProcessor.class);
    private static final int CDR_BATCH_SIZE = 10;
    @Autowired
    private FragmentEditor fragmentEditor;
    @Autowired
    private ReportQueueSender reportQueueSender;

    public void processAndSendFragments(List<Fragment> fragments) {
        fragments.sort(Comparator.comparing(Fragment::getStartTime));
        sendBatches(fragments);
    }

    public void sendBatches(List<Fragment> dailyFragments) {
        for (int i = 0; i < dailyFragments.size(); i += CDR_BATCH_SIZE) {
            int endIndex = Math.min(i + CDR_BATCH_SIZE, dailyFragments.size());
            List<Fragment> batch = dailyFragments.subList(i, endIndex);

            if (batch.size() >= CDR_BATCH_SIZE) {
                try {
                    String cdrContent = batch.stream()
                            .map(fragmentEditor::formatFragment)
                            .collect(Collectors.joining("\n"));

                    reportQueueSender.sendReport(cdrContent);
                } catch (Exception e) {
                    logger.error("Error during batch formating: {}", e.getLocalizedMessage());
                }
            }
        }
    }
}
