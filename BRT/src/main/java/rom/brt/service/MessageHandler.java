package rom.brt.service;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.brt.client.HRSClient;
import rom.brt.dto.*;
import rom.brt.entity.CallRecord;
import rom.brt.entity.User;
import rom.brt.repository.CallRecordRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 2025-05-01T16:39:07.615Z ERROR 1 --- [nio-8082-exec-8] rom.hrs.service.CalculationService : Error handling request: "CalculationRequest(callType=02, caller=Subscriber(id=2, msisdn=79992224466, isServiced=false, tariffId=12, minutes=50, paymentDay=2024-05-13), receiver=Subscriber(id=4, msisdn=79994446688, isServiced=false, tariffId=12, minutes=50, paymentDay=2024-05-13), durationMinutes=59, currentDate=2025-04-30)": "Cannot invoke "java.lang.Double.doubleValue()" because the return value of "rom.hrs.dto.CalculationResponse.getCost()" is null"
 * 2025-05-01T16:39:07.688Z ERROR 1 --- [ntContainer#0-1] rom.brt.service.MessageHandler : Cannot invoke "rom.brt.entity.UserParams.getMinutes()" because the return value of "rom.brt.entity.User.getUserParams()" is null
 */
@Service
@RequiredArgsConstructor
public class MessageHandler {
    @Autowired
    private final HRSClient hrsClient;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CallRecordRepository callRecordRepository;
    @Autowired
    private final BillingService billingService;
    @Autowired
    private final FragmentMapper fragmentMapper;
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private static final Pattern CDR_PATTERN = Pattern.compile(
            "^\\d{2},\\s*\\d{11},\\s*\\d{11},\\s*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2},\\s*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
    );

    public void handleMessage(String message) {
        try {
            fragmentMapper.parseCsv(message).forEach(this::processCall);
        } catch (Exception e) {
            // TODO: exception handling
            logger.error(e.getLocalizedMessage());
        }
    }

    private void processCall(Fragment fragment) {
        User callerRecord = userService.findUser(fragment.getCallerMsisdn());
        logger.info("Got callerRecord: {}", callerRecord);
        User receiverRecord = userService.findUser(fragment.getReceiverMsisdn());
        logger.info("Got receiverRecord: {}", receiverRecord);

        Subscriber  caller;
        Subscriber receiver;

        if (callerRecord.getUserId() == -1) {
            logger.info("Caller is not serviced by Romashka. Terminating fragment processing");
            return;
        }

        caller = userService.createServicedSubscriberFromRecord(callerRecord);
        logger.info("Created caller: {}", caller);

        if (receiverRecord.getUserId() == -1) {
            logger.info("Receiver {} is not serviced by Romashka", receiverRecord.getMsisdn());
            receiver = userService.createForeignSubscriberFromRecord(receiverRecord);
        } else {
            receiver = userService.createServicedSubscriberFromRecord(receiverRecord);
        }

        logger.info("Created receiver: {}", receiver);

        CalculationRequest request = buildCalculationRequest(fragment, caller, receiver);
        logger.info("Sent request: {}", request);
        CalculationResponse response = hrsClient.calculateCost(request);
        logger.info("Received response: {}", response);

        if (response != null) {
            handleCalculationResponse(callerRecord, fragment, response);
        } else {
            logger.error("Got null response from HRS");
        }
    }

    private CalculationRequest buildCalculationRequest(Fragment fragment, Subscriber caller, Subscriber receiver) {
        long seconds = Duration.between(fragment.getStartTime(), fragment.getEndTime()).getSeconds();
        int durationMinutes = (int) Math.ceil(seconds / 60.0);

        return new CalculationRequest(
                fragment.getCallType(),
                caller,
                receiver,
                durationMinutes,
                fragment.getStartTime().toLocalDate()
        );
    }

    private void handleCalculationResponse(User caller, Fragment fragment, CalculationResponse response) {
        if ("REJECTED".equals(response.tariffType())) {
            logRejectedCall(fragment, response.description());
            return;
        }

        if (response.cost() != null) {
            billingService.processBilling(caller, response);
        }

        saveCallRecord(fragment, response);
        logger.info("Saved fragment: {}\nResponse: {}", fragment, response);
    }

    private void saveCallRecord(Fragment fragment, CalculationResponse response) {
        long seconds = Duration.between(fragment.getStartTime(), fragment.getEndTime()).getSeconds();
        int durationMinutes = (int) Math.ceil(seconds / 60.0);

        CallRecord record = CallRecord.builder()
                .callType(fragment.getCallType())
                .callerMsisdn(fragment.getCallerMsisdn())
                .receiverMsisdn(fragment.getReceiverMsisdn())
                .startTime(fragment.getStartTime())
                .endTime(fragment.getEndTime())
                .durationMinutes(durationMinutes)
                .cost(BigDecimal.valueOf(response.cost()))
                .build();

        callRecordRepository.save(record);
    }

    private void logRejectedCall(Fragment fragment, String reason) {
        logger.warn("Failed to process fragment {}\nResponse: {}\n", fragment, reason);
    }
}