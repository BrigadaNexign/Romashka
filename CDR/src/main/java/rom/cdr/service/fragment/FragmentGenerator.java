package rom.cdr.service.fragment;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.cdr.entity.Fragment;
import rom.cdr.exception.ConflictingCallsException;
import rom.cdr.service.subscriber.SubscriberService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис для генерации тестовых фрагментов звонков.
 * Обеспечивает создание реалистичных CDR записей с проверкой на конфликты.
 */
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

    /**
     * Генерирует фрагменты звонков с учетом пересечения полуночи.
     *
     * @param startTime начало периода генерации
     * @param endTime конец периода генерации
     * @return список сгенерированных фрагментов
     */
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

    /**
     * Обрабатывает случай звонка, пересекающего полночь.
     *
     * @param startTime время начала звонка
     * @param endTime время окончания звонка
     * @return список из двух фрагментов (до и после полуночи)
     */
    public List<Fragment> handleMidnightCrossing(LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Fragment crosses midnight");
        Fragment firstPart = createDayPart(startTime, startTime.toLocalDate().atTime(23, 59, 59));
        Fragment secondPart = createAfterMidnightFragment(firstPart, endTime);

        return checkAndSaveFragments(firstPart, secondPart);
    }

    /**
     * Обрабатывает генерацию фрагмента в пределах одного дня (без пересечения полуночи).
     *
     * @param startTime время начала звонка
     * @param endTime время окончания звонка
     * @return список сгенерированных фрагментов (с зеркальной записью)
     */
    public List<Fragment> handleSingleDayFragment(LocalDateTime startTime, LocalDateTime endTime) {
        Fragment fragment = createRandomFragment(startTime, endTime);
        return checkAndSaveFragment(fragment);
    }

    /**
     * Проверяет на валидность и сохраняет несколько фрагментов с обработкой зеркальных записей.
     *
     * @param fragments фрагменты для проверки и сохранения
     * @return список успешно сохраненных фрагментов
     */
    private List<Fragment> checkAndSaveFragments(Fragment... fragments) {
        List<Fragment> result = new ArrayList<>();
        if (allFragmentsValid(fragments)) {
            for (Fragment fragment : fragments) {
                result.addAll(saveWithMirror(fragment));
            }
            return result;
        } else logger.error("Not all fragments are valid");
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

    /**
     * Создает зеркальный фрагмент для входящего/исходящего звонка.
     *
     * @param original оригинальный фрагмент звонка
     * @return зеркальный фрагмент с измененным типом вызова
     */
    public Fragment createMirrorFragment(Fragment original) {
        return fragmentEditor.createFragment(
                original.getCallType().equals("01") ? "02" : "01",
                original.getReceiverMsisdn(),
                original.getCallerMsisdn(),
                original.getStartTime(),
                original.getEndTime()
        );
    }

    /**
     * Создает фрагмент для части звонка после полуночи.
     *
     * @param firstPart первый фрагмент звонка (до полуночи)
     * @param endTime полное время окончания звонка
     * @return фрагмент для части после полуночи
     */
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

    /**
     * Проверяет фрагмент на конфликты с существующими записями.
     *
     * @param fragment фрагмент для проверки
     * @return true если конфликтов нет, false если есть конфликты
     */
    public boolean checkConflicts(Fragment fragment) {
        synchronized(getLockObject(fragment.getCallerMsisdn(), fragment.getReceiverMsisdn())) {
            try {
                boolean hasConflict = hasConflicts(fragment);
                if (hasConflict) {
                    logger.error("Conflict detected for call between {} and {} ({} - {})",
                            fragment.getCallerMsisdn(),
                            fragment.getReceiverMsisdn(),
                            fragment.getStartTime(),
                            fragment.getEndTime()
                    );
                    return false;
                }
                return true;
            } catch (ConflictingCallsException e) {
                return false;
            } catch (Exception e) {
                logger.error("Unknown exception during checking conflicts: {}", e.getMessage());
                return false;
            }
        }
    }

    boolean hasConflicts(Fragment fragment) throws ConflictingCallsException {
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

    /**
     * Возвращает объект для синхронизации по параметрам MSISDN.
     * Используется для предотвращения конфликтов при параллельной генерации.
     *
     * @param msisdn1 первый номер абонента
     * @param msisdn2 второй номер абонента
     * @return объект блокировки для данной пары абонентов
     */
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
