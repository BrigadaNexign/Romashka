package org.example.service.fragment;

import lombok.RequiredArgsConstructor;
import org.example.entity.Fragment;
import org.example.entity.Subscriber;
import org.example.service.subscriber.SubscriberService;
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
    private FragmentService fragmentService;

    @Autowired
    private FragmentEditor fragmentEditor;

    @Autowired
    private FragmentGenerator fragmentGenerator;

    @Autowired
    private SubscriberService subscriberService;

    private final Random random = new Random();

    private final FragmentBlockingQueue fragmentQueue;
    
    private List<Subscriber> ourSubscriberList;
    private List<Subscriber> subscriberList;

    private static final int years = 1;

    public void createForYears() {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusYears(years);
            LocalDateTime endTime = LocalDateTime.now();

            while (startTime.isBefore(endTime)) {
                startTime = createFragment(startTime);
                // fragmentQueue.put(Optional.of(fragment));
                // startTime = startTime.plusMinutes(random.nextInt(60));
            }
            //fragmentQueue.put(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fragment records generation interrupted", e);
        }
    }

    public LocalDateTime createFragment(LocalDateTime startTime) throws InterruptedException {
        Fragment firstFragment = fragmentGenerator.generateConflictFreeFragment(startTime);

        if (checkIfOurReceiver(firstFragment.getReceiverMsisdn())) {
            Fragment secondFragment = fragmentEditor.createFragment(
                    firstFragment.getCallType().equals("01") ? "02" : "01",
                    firstFragment.getReceiverMsisdn(),
                    firstFragment.getCallerMsisdn(),
                    firstFragment.getStartTime(),
                    firstFragment.getEndTime()
            );
            processFragment(secondFragment);
        }

        processFragment(firstFragment);

        return startTime.plusMinutes(1 + random.nextInt(59));
    }


    private void processFragment(Fragment fragment) throws InterruptedException {
        if (checkMidnight(fragment)) {
            splitMidnightFragmentAndSave(fragment);
        } else {
            putToQueueAndSave(fragment);
        }
    }

    private boolean checkIfOurReceiver(String receiverMsisdn) {
        ourSubscriberList = subscriberService.fetchOurSubscriberList();
        Optional<Subscriber> ourSubscriber = ourSubscriberList.stream()
                .filter(s -> s.getMsisdn().equals(receiverMsisdn)).findFirst();
        return ourSubscriber.isPresent();
    }

    public void splitMidnightFragmentAndSave(Fragment fragment) throws InterruptedException {
        putToQueueAndSave(
                fragmentEditor.splitFragmentBeforeMidnight(fragment)
        );

        putToQueueAndSave(
                fragmentEditor.splitFragmentAfterMidnight(fragment)
        );
    }

    public void putToQueueAndSave(Fragment fragment) throws InterruptedException {
        fragmentQueue.put(fragment);
        fragmentService.saveCDR(fragment);
    }

    private boolean checkMidnight(Fragment fragment) {
        return fragment.getStartTime().getDayOfYear() != fragment.getEndTime().getDayOfYear()
                || !fragment.getStartTime().toLocalDate().equals(fragment.getEndTime().toLocalDate());
    }
}