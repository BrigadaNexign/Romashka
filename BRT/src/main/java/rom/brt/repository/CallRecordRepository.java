package rom.brt.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rom.brt.entity.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
    boolean existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
            String callType,
            String callerMsisdn,
            String receiverMsisdn,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}