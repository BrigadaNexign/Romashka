package rom.brt.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.dto.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
import rom.brt.repository.CallRecordRepository;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CallRecordService {
    private static final Logger logger = LoggerFactory.getLogger(CallRecordService.class);
    private final CallRecordRepository callRecordRepository;

    public CallRecord saveCallRecord(Fragment fragment, CalculationResponse response) {
        long seconds = Duration.between(fragment.getStartTime(), fragment.getEndTime()).getSeconds();
        int durationMinutes = (int) Math.ceil(seconds / 60.0);

        CallRecord record = CallRecord.builder()
                .callType(fragment.getCallType())
                .callerMsisdn(fragment.getCallerMsisdn())
                .receiverMsisdn(fragment.getReceiverMsisdn())
                .startTime(fragment.getStartTime())
                .endTime(fragment.getEndTime())
                .durationMinutes(durationMinutes)
                .cost(BigDecimal.valueOf(response.getCost()))
                .build();

        return callRecordRepository.save(record);
    }
}
