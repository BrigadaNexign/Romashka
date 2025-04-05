package org.example.repository;

import org.example.entity.CDR;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class CDRRepositoryTest {

    @Autowired
    private CDRRepository cdrRepository;

    @Test
    void findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween_CDR_CallerExists() {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setCallerMsisdn(msisdn);
        cdr.setReceiverMsisdn("79993331133");
        cdr.setStartTime(startDate);
        cdr.setEndTime(endDate);
        cdrRepository.save(cdr);

        List<CDR> cdrs = cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startDate, endDate);

        assertFalse(cdrs.isEmpty());
        assertEquals(msisdn, cdrs.get(0).getCallerMsisdn());
    }

    @Test
    void findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween_CDR_ReceiverExists() {
        String msisdn = "79992221122";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setCallerMsisdn(msisdn);
        cdr.setReceiverMsisdn("79993331133");
        cdr.setStartTime(startDate);
        cdr.setEndTime(endDate);
        cdrRepository.save(cdr);

        List<CDR> cdrs = cdrRepository.findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
                msisdn, startDate, endDate);

        assertFalse(cdrs.isEmpty());
        assertEquals(msisdn, cdrs.get(0).getCallerMsisdn());
    }
}
