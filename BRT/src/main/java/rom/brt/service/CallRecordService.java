package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.dto.response.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
import rom.brt.repository.CallRecordRepository;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Сервис для работы с записями о звонках.
 * Сохраняет детали звонков в базу данных.
 */
@Service
@RequiredArgsConstructor
public class CallRecordService {
    private static final Logger logger = LoggerFactory.getLogger(CallRecordService.class);
    private final CallRecordRepository callRecordRepository;

    /**
     * Сохраняет информацию о звонке на основе фрагмента CDR и ответа от HRS.
     *
     * @param fragment данные о звонке
     * @param response результат расчета стоимости
     * @return сохраненная запись о звонке
     */
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

    /**
     * Проверяет, существует ли поступивший фрагмент CDR в базе обработанных звонков.
     *
     * @param fragment данные о звонке
     */
    public boolean existsDuplicate(Fragment fragment) {
        return callRecordRepository.existsByCallTypeAndCallerMsisdnAndReceiverMsisdnAndStartTimeAndEndTime(
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime(),
                fragment.getEndTime()
        );
    }
}
