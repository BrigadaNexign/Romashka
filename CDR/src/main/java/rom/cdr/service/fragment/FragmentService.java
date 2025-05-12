package rom.cdr.service.fragment;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rom.cdr.entity.Fragment;
import rom.cdr.exception.ConflictingCallsException;
import rom.cdr.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для работы с фрагментами звонков в базе данных.
 * Обеспечивает сохранение и проверку фрагментов на конфликты.
 */
@Service
@AllArgsConstructor
public class FragmentService {
    private static final Logger logger = LoggerFactory.getLogger(FragmentService.class);
    @Autowired
    private final FragmentRepository fragmentRepository;


    /**
     * Сохраняет фрагмент звонка в базу данных.
     *
     * @param fragment фрагмент для сохранения
     * @return сохраненный фрагмент
     */
    public Fragment saveFragment(Fragment fragment) {
        try {
            logger.debug("Saving fragment: {}", fragment);
            Fragment saved = fragmentRepository.save(fragment);
            logger.trace("Fragment saved successfully: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Failed to save fragment: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Проверяет наличие конфликтующих звонков для указанных параметров.
     *
     * @param caller номер вызывающего абонента
     * @param receiver номер принимающего абонента
     * @param start время начала проверяемого периода
     * @param end время окончания проверяемого периода
     * @return true если есть конфликтующие звонки, false если нет
     * @throws ConflictingCallsException если параметры невалидны
     */
    public boolean hasConflictingCalls(
            String caller,
            String receiver,
            LocalDateTime start,
            LocalDateTime end
    ) throws ConflictingCallsException {

        if (caller == null || receiver == null || start == null || end == null) {
            logger.error("None of the parameters can be null");
            throw new ConflictingCallsException("None of the parameters can be null");
        }

        if (end.isBefore(start)) {
            throw new ConflictingCallsException("End time cannot be before start time");
        }

        return fragmentRepository.existsConflictingCalls(caller, receiver, start, end);
    }
}
