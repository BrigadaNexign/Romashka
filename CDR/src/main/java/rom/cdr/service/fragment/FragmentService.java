package rom.cdr.service.fragment;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rom.cdr.entity.Fragment;
import rom.cdr.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Реализация сервиса для работы с Fragment.
 * Предоставляет методы для сохранения, поиска, удаления и инициализации данных Fragment.
 */
@Service
@AllArgsConstructor
public class FragmentService {
    private static final Logger logger = LoggerFactory.getLogger(FragmentService.class);
    @Autowired
    private final FragmentRepository fragmentRepository;


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

    public boolean hasConflictingCalls(
            String caller,
            String receiver,
            LocalDateTime start,
            LocalDateTime end) {

        if (caller == null || receiver == null || start == null || end == null) {
            throw new IllegalArgumentException("None of the parameters can be null");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        return fragmentRepository.existsConflictingCalls(caller, receiver, start, end);
    }
}
