package rom.cdr.service.fragment;

import lombok.AllArgsConstructor;
import rom.cdr.entity.Fragment;
import rom.cdr.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация сервиса для работы с Fragment.
 * Предоставляет методы для сохранения, поиска, удаления и инициализации данных Fragment.
 */
@Service
@AllArgsConstructor
public class FragmentService {
    @Autowired
    private final FragmentRepository fragmentRepository;

    public Fragment saveCDR(Fragment fragment) {
        return fragmentRepository.save(fragment);
    }

    public List<Fragment> fetchCDRList() {
        return fragmentRepository.findAll();
    }

    public boolean hasConflictingCalls(
            String caller,
            String receiver,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return fragmentRepository.existsConflictingCalls(caller, receiver, start, end);
    }
}
