package org.example.service.fragment;

import lombok.RequiredArgsConstructor;
import org.example.entity.Fragment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FragmentEditor {


    public Fragment splitFragmentBeforeMidnight(Fragment fragment) {
        LocalDateTime midnight = fragment.getStartTime().toLocalDate().plusDays(1).atStartOfDay();

        return createFragment(
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                midnight.minusSeconds(1)
        );

    }

    public Fragment splitFragmentAfterMidnight(Fragment fragment) {
        LocalDateTime midnight = fragment.getStartTime().toLocalDate().plusDays(1).atStartOfDay();

        return createFragment(
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                midnight.plusSeconds(0),
                fragment.getEndTime()
        );

    }

    public Fragment createFragment(
            String callType,
            String callerMsisdn,
            String receiverMsisdn,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        Fragment fragment = new Fragment();
        fragment.setCallType(callType);
        fragment.setCallerMsisdn(callerMsisdn);
        fragment.setReceiverMsisdn(receiverMsisdn);
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);
        return fragment;
    }

    public String formatFragment(Fragment fragment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return String.join(", ",
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime().format(formatter),
                fragment.getEndTime().format(formatter)
        );
    }
}
