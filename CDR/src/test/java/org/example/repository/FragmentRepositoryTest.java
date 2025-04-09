package org.example.repository;

import org.example.entity.Fragment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class FragmentRepositoryTest {

    @Autowired
    private CDRRepository cdrRepository;

    @Test
    void findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween_CDR_CallerExists() {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn(msisdn);
        fragment.setReceiverMsisdn("79993331133");
        fragment.setStartTime(startDate);
        fragment.setEndTime(endDate);
        cdrRepository.save(fragment);

        List<Fragment> fragments = cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startDate, endDate);

        assertFalse(fragments.isEmpty());
        assertEquals(msisdn, fragments.get(0).getCallerMsisdn());
    }

    @Test
    void findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween_CDR_ReceiverExists() {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        Fragment fragment = new Fragment();
        fragment.setCallType("01");
        fragment.setCallerMsisdn(msisdn);
        fragment.setReceiverMsisdn("79993331133");
        fragment.setStartTime(startDate);
        fragment.setEndTime(endDate);
        cdrRepository.save(fragment);

        List<Fragment> fragments = cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startDate, endDate);

        assertFalse(fragments.isEmpty());
        assertEquals(msisdn, fragments.get(0).getCallerMsisdn());
    }
}
