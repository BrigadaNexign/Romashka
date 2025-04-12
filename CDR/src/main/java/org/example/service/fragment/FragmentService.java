package org.example.service.fragment;

import lombok.AllArgsConstructor;
import org.example.entity.Fragment;
import org.example.repository.CDRRepository;
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
    private final CDRRepository cdrRepository;

    public Fragment saveCDR(Fragment fragment) {
        return cdrRepository.save(fragment);
    }

    public List<Fragment> fetchCDRList() {
        return cdrRepository.findAll();
    }

    public void deleteCDRByID(Long CDRId) {
        cdrRepository.deleteById(CDRId);
    }

    public List<Fragment> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn) {
        return cdrRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn);
    }

    public <S extends Fragment> List<S> saveAllFragments(Iterable<S> entities) {
        return cdrRepository.saveAll(entities);
    }

    public List<Fragment> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        return cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startOfMonth, endOfMonth
        );
    }

    public boolean hasConflictingCalls(
            String caller,
            String receiver,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return cdrRepository.existsConflictingCalls(caller, receiver, start, end);
    }
}
