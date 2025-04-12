package org.example.service.fragment;

import lombok.RequiredArgsConstructor;
import org.example.entity.Fragment;
import org.example.util.FragmentBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис для генерации Fragment записей.
 * Предоставляет методы для создания Fragment записей для абонентов за определенный период.
 */
@Service
@RequiredArgsConstructor
public class FragmentProcessor {

    @Autowired
    private FragmentService cdrService;

    @Autowired
    private FragmentEditor fragmentEditor;

    @Autowired
    private FragmentGenerator fragmentGenerator;

    private final Random random = new Random();

    private final FragmentBlockingQueue fragmentQueue;

    public void generateAndPutToQueue() {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusYears(1);
            LocalDateTime endTime = LocalDateTime.now();

            while (startTime.isBefore(endTime)) {
                startTime = generateFragment(startTime);
                // fragmentQueue.put(Optional.of(fragment));
                // startTime = startTime.plusMinutes(random.nextInt(60));
            }
            //fragmentQueue.put(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fragment records generation interrupted", e);
        }
    }

    public LocalDateTime generateFragment(LocalDateTime startTime) throws InterruptedException {
        Fragment fragment = fragmentGenerator.generateConflictFreeFragment(startTime);

        if (checkMidnight(fragment)) {
            splitMidnightFragment(fragment);
        } else {
            putToQueueAndSave(fragment);
        }

        return startTime.plusMinutes(1 + random.nextInt(59));
    }

    public void splitMidnightFragment(Fragment fragment) throws InterruptedException {
        putToQueueAndSave(
                fragmentEditor.splitFragmentBeforeMidnight(fragment)
        );

        putToQueueAndSave(
                fragmentEditor.splitFragmentAfterMidnight(fragment)
        );
    }

    public void putToQueueAndSave(Fragment fragment) throws InterruptedException {
        fragmentQueue.put(fragment);
        cdrService.saveCDR(fragment);
    }

    private boolean checkMidnight(Fragment fragment) {
        return fragment.getStartTime().getDayOfYear() != fragment.getEndTime().getDayOfYear()
                || !fragment.getStartTime().toLocalDate().equals(fragment.getEndTime().toLocalDate());
    }
}