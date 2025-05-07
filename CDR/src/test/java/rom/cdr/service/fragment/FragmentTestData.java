package rom.cdr.service.fragment;

import rom.cdr.entity.Fragment;

import java.time.LocalDateTime;

public class FragmentTestData {
    static Fragment createTestFragment() {
        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn("79991112233");
        fragment.setReceiverMsisdn("79992223344");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(5));
        return fragment;
    }
}