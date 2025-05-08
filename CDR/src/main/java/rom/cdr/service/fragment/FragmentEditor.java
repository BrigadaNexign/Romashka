package rom.cdr.service.fragment;

import lombok.RequiredArgsConstructor;
import rom.cdr.entity.Fragment;
import org.springframework.stereotype.Service;
import rom.cdr.exceptions.EmptyFieldException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public String[] formatFragment(Fragment fragment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return new String[]{
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime().format(formatter),
                fragment.getEndTime().format(formatter)
        };
    }

    public void checkFragment(Fragment fragment) throws EmptyFieldException {
        if (fragment == null) {
            throw new EmptyFieldException("Fragment is null");
        }

        if (fragment.getCallType() == null || fragment.getCallType().trim().isEmpty()) {
            throw new EmptyFieldException("callType");
        }
        if (fragment.getCallerMsisdn() == null || fragment.getCallerMsisdn().trim().isEmpty()) {
            throw new EmptyFieldException("callerMsisdn");
        }
        if (fragment.getReceiverMsisdn() == null || fragment.getReceiverMsisdn().trim().isEmpty()) {
            throw new EmptyFieldException("receiverMsisdn");
        }
        if (fragment.getStartTime() == null) {
            throw new EmptyFieldException("startTime");
        }
        if (fragment.getEndTime() == null) {
            throw new EmptyFieldException("endTime");
        }
    }
}
