package rom.cdr.service.fragment;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.service.subscriber.SubscriberService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FragmentGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FragmentGenerator.class);
    private static final Random random = new Random();
    private List<String> msisdns;

    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private FragmentEditor fragmentEditor;
    @Autowired
    private FragmentService fragmentService;

    @PostConstruct
    public void init() {
        this.msisdns = subscriberService.getAllMsisdns();
        logger.info("Retrieved {} MSISDNs for generation", msisdns.size());
    }

    public List<Fragment> generateFragmentWithMidnightCheck(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            logger.debug("Generating fragment between {} and {}", startTime, endTime);
            return isCrossingMidnight(startTime, endTime)
                    ? handleMidnightCrossing(startTime, endTime)
                    : handleSingleDayFragment(startTime, endTime);
        } catch (Exception e) {
            logger.error("Error in generating fragments: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Fragment> handleMidnightCrossing(LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Fragment crosses midnight");
        Fragment firstPart = createDayPart(startTime, startTime.toLocalDate().atTime(23, 59, 59));
        Fragment secondPart = createAfterMidnightFragment(firstPart, endTime);

        return checkAndSaveFragments(firstPart, secondPart);
    }

    public List<Fragment> handleSingleDayFragment(LocalDateTime startTime, LocalDateTime endTime) {
        Fragment fragment = createRandomFragment(startTime, endTime);
        return checkAndSaveFragment(fragment);
    }

    private List<Fragment> checkAndSaveFragments(Fragment... fragments) {
        List<Fragment> result = new ArrayList<>();
        if (allFragmentsValid(fragments)) {
            for (Fragment fragment : fragments) {
                result.addAll(saveWithMirror(fragment));
            }
        }
        return result;
    }

    private List<Fragment> checkAndSaveFragment(Fragment fragment) {
        return checkConflicts(fragment)
                ? saveWithMirror(fragment)
                : Collections.emptyList();
    }

    private List<Fragment> saveWithMirror(Fragment fragment) {
        return Arrays.asList(
                fragmentService.saveFragment(fragment),
                fragmentService.saveFragment(createMirrorFragment(fragment))
        );
    }

    public Fragment createMirrorFragment(Fragment original) {
        return fragmentEditor.createFragment(
                original.getCallType().equals("01") ? "02" : "01",
                original.getReceiverMsisdn(),
                original.getCallerMsisdn(),
                original.getStartTime(),
                original.getEndTime()
        );
    }

    public Fragment createAfterMidnightFragment(
            Fragment firstPart,
            LocalDateTime endTime
    ) {
        return fragmentEditor.createFragment(
                firstPart.getCallType(),
                firstPart.getCallerMsisdn(),
                firstPart.getReceiverMsisdn(),
                endTime.toLocalDate().atStartOfDay(),
                endTime
        );
    }

    private boolean allFragmentsValid(Fragment... fragments) {
        return Arrays.stream(fragments).allMatch(this::checkConflicts);
    }

    public boolean checkConflicts(Fragment fragment) {
        synchronized(getLockObject(fragment.getCallerMsisdn(), fragment.getReceiverMsisdn())) {
            boolean hasConflict = hasConflicts(fragment);
            if (hasConflict) {
                logger.error("Conflict detected for call between {} and {} ({} - {})",
                        fragment.getCallerMsisdn(),
                        fragment.getReceiverMsisdn(),
                        fragment.getStartTime(),
                        fragment.getEndTime());
            }
            return !hasConflict;
        }
    }

    boolean hasConflicts(Fragment fragment) {
        return fragmentService.hasConflictingCalls(
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime()
        );
    }

    private Fragment createDayPart(LocalDateTime start, LocalDateTime end) {
        return createRandomFragment(start, end);
    }

    private Fragment createRandomFragment(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return fragmentEditor.createFragment(
                random.nextBoolean() ? "01" : "02",
                getRandomMsisdn(),
                getRandomMsisdn(),
                startTime,
                endTime
        );
    }

    private Object getLockObject(String msisdn1, String msisdn2) {
        String[] keys = {msisdn1, msisdn2};
        Arrays.sort(keys);
        return (keys[0] + "|" + keys[1]).intern();
    }

    public boolean isCrossingMidnight(LocalDateTime start, LocalDateTime end) {
        return !start.toLocalDate().equals(end.toLocalDate());
    }

    private String getRandomMsisdn() {
        return msisdns.get(random.nextInt(msisdns.size()));
    }
}
