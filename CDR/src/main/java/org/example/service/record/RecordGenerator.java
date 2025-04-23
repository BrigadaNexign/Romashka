package org.example.service.record;

import org.example.entity.Fragment;
import org.example.service.fragment.FragmentEditor;
import org.example.service.fragment.FragmentService;
import org.example.service.sender.ReportQueueSender;
import org.example.service.subscriber.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecordGenerator {
    private static final int CDR_BATCH_SIZE = 10;
    private static final Random random = new Random();

    @Autowired
    private FragmentService fragmentService;
    @Autowired
    private FragmentEditor fragmentEditor;
    @Autowired
    private ReportQueueSender reportQueueSender;
    @Autowired
    private SubscriberService subscriberService;

    /**
     * Асинхронно генерирует CDR данные за указанное количество лет
     *
     * @param years количество лет для генерации данных
     */
    @Async
    public void generateCDRData(int years) {
        LocalDateTime startTime = LocalDateTime.now().minusYears(years);
        LocalDateTime endTime = LocalDateTime.now();

        List<String> msisdns = subscriberService.getAllMsisdns();

        while (startTime.isBefore(endTime)) {
            generateFragmentWithMidnightCheck(
                    startTime,
                    startTime.plusMinutes(1 + random.nextInt(59)),
                    msisdns
            );

            startTime = startTime.plusMinutes(1 + random.nextInt(59));
        }

        List<Fragment> allFragments = new ArrayList<>(fragmentService.fetchCDRList());
        allFragments.sort(Comparator.comparing(Fragment::getStartTime));

        for (int i = 0; i < allFragments.size(); i += CDR_BATCH_SIZE) {
            int endIndex = Math.min(i + CDR_BATCH_SIZE, allFragments.size());
            List<Fragment> cdrBatch = allFragments.subList(i, endIndex);
            sendCDRFile(cdrBatch);
        }
    }

    private void generateFragmentWithMidnightCheck(
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<String> msisdns
    ) {
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            Fragment firstPart = fragmentEditor.createFragment(
                    random.nextBoolean() ? "01" : "02",
                    getRandomMsisdn(msisdns),
                    getRandomMsisdn(msisdns),
                    startTime,
                    startTime.toLocalDate().atTime(23, 59, 59)
            );


            Fragment secondPart = fragmentEditor.createFragment(
                    firstPart.getCallType(),
                    firstPart.getCallerMsisdn(),
                    firstPart.getReceiverMsisdn(),
                    endTime.toLocalDate().atStartOfDay(),
                    endTime
            );

            checkConflictsAndSaveMidnight(firstPart, secondPart);
            return;
        }

        checkConflictsAndSave(
                fragmentEditor.createFragment(
                random.nextBoolean() ? "01" : "02",
                getRandomMsisdn(msisdns),
                getRandomMsisdn(msisdns),
                startTime,
                endTime
            )
        );
    }

    private void checkConflictsAndSaveMidnight(Fragment firstPart, Fragment secondPart) {
        if (!checkConflicts(firstPart) && !checkConflicts(secondPart)) {
            fragmentService.saveCDR(firstPart);
            fragmentService.saveCDR(secondPart);

            Fragment mirroredFirst = createMirrorFragment(firstPart);
            Fragment mirroredSecond = createMirrorFragment(secondPart);

            fragmentService.saveCDR(mirroredFirst);
            fragmentService.saveCDR(mirroredSecond);
        }
    }

    private void checkConflictsAndSave(Fragment fragment) {
        if (!checkConflicts(fragment)) {
            fragmentService.saveCDR(fragment);
            Fragment mirroredFragment = createMirrorFragment(fragment);
            fragmentService.saveCDR(mirroredFragment);
        }
    }

    private void sendCDRFile(List<Fragment> fragments) {
        String cdrFileContent = fragments.stream()
                .map(fragmentEditor::formatFragment)
                .collect(Collectors.joining("\n"));

        reportQueueSender.sendReport(cdrFileContent);
    }

    private boolean checkConflicts(Fragment fragment) {
        return fragmentService.hasConflictingCalls(
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime());
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
}
