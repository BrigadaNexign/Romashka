package org.example.service.fragment;

import lombok.AllArgsConstructor;
import org.example.entity.Fragment;
import org.example.entity.Subscriber;
import org.example.service.subscriber.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class FragmentGenerator {

    @Autowired
    private FragmentService fragmentService;

    @Autowired
    private FragmentEditor fragmentEditor;

    @Autowired
    private SubscriberService subscriberService;

    private final Random random = new Random();
    private List<Subscriber> subscribersList;

    private static final int MAX_GENERATION_ATTEMPTS = 10;


    public Fragment generateConflictFreeFragment(LocalDateTime startTime) {
        subscribersList = subscriberService.fetchSubscriberList();

        if (subscribersList == null || subscribersList.isEmpty()) {
            throw new IllegalStateException("No subscribers available for Fragment generation");
        }

        Subscriber subscriber = subscribersList.get(random.nextInt(subscribersList.size()));
        String caller = subscriber.getMsisdn();
        String receiver = getRandomReceiverMsisdn(caller);

        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
            LocalDateTime endTime = startTime.plusSeconds(random.nextInt(3600));

            if (endTime.isBefore(startTime)) {
                throw new IllegalStateException("Generated invalid time range: endTime before startTime");
            }

            if (!fragmentService.hasConflictingCalls(caller, receiver, startTime, endTime)) {
                return fragmentEditor.createFragment(random.nextBoolean() ? "01" : "02", caller, receiver, startTime, endTime);
            }

            startTime = endTime.plusSeconds(1);
        }
        throw new IllegalStateException(String.format(
                "Failed to create conflict-free Fragment after %d attempts for subscriber %s",
                MAX_GENERATION_ATTEMPTS, caller));
    }

    public String getRandomReceiverMsisdn(String callerMsisdn) throws IllegalStateException {
        List<Subscriber> possibleReceivers = subscribersList.stream()
                .filter(s -> !s.getMsisdn().equals(callerMsisdn))
                .toList();

        if (possibleReceivers.isEmpty()) {
            throw new IllegalStateException("No available receivers for caller: " + callerMsisdn);
        }

        return possibleReceivers.get(random.nextInt(possibleReceivers.size())).getMsisdn();
    }
}
