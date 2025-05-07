package rom.cdr.service.fragment;

import lombok.RequiredArgsConstructor;
import rom.cdr.entity.Fragment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FragmentEditor {

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

    public String formatFragment(Fragment fragment) throws IllegalArgumentException {
        checkFragment(fragment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return String.join(", ",
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime().format(formatter),
                fragment.getEndTime().format(formatter)
        );
    }

    private void checkFragment(Fragment fragment) throws IllegalArgumentException {
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        if (fragment.getCallType() == null) {
            throw new IllegalArgumentException("CallType cannot be null");
        }
        if (fragment.getCallerMsisdn() == null) {
            throw new IllegalArgumentException("CallerMsisdn cannot be null");
        }
        if (fragment.getReceiverMsisdn() == null) {
            throw new IllegalArgumentException("ReceiverMsisdn cannot be null");
        }
        if (fragment.getStartTime() == null) {
            throw new IllegalArgumentException("StartTime cannot be null");
        }
        if (fragment.getEndTime() == null) {
            throw new IllegalArgumentException("EndTime cannot be null");
        }
    }
}
