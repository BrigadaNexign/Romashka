package org.example.service.fragment;

import lombok.AllArgsConstructor;
import org.example.entity.Fragment;
import org.example.repository.FragmentRepository;
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

    public void deleteCDRByID(Long CDRId) {
        fragmentRepository.deleteById(CDRId);
    }

    public List<Fragment> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn) {
        return fragmentRepository.findByCallerMsisdnOrReceiverMsisdn(callerMsisdn, receiverMsisdn);
    }

    public <S extends Fragment> List<S> saveAllFragments(Iterable<S> entities) {
        return fragmentRepository.saveAll(entities);
    }

    public List<Fragment> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        return fragmentRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startOfMonth, endOfMonth
        );
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
