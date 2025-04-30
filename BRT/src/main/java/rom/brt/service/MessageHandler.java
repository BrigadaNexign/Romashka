package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.client.HRSClient;
import rom.brt.dto.*;
import rom.brt.entity.CallRecord;
import rom.brt.entity.User;
import rom.brt.repository.CallRecordRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MessageHandler {
    private final HRSClient hrsClient;
    private final UserService userService;
    private final CallRecordRepository callRecordRepository;
    private final BillingService billingService;
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private static final Pattern CDR_PATTERN = Pattern.compile(
            "^\\d{2},\\s*\\d{11},\\s*\\d{11},\\s*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2},\\s*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
    );

    public void handleMessage(String message) {
        Arrays.stream(message.split("\n"))
                .map(String::trim)
                .filter(line -> CDR_PATTERN.matcher(line).matches())
                .forEach(this::handleFragment);
    }

    private void handleFragment(String fragmentStr) {
        logger.info("Trying to handle message \"{}\"", fragmentStr);
        try {
            Fragment fragment = Fragment.fromString(fragmentStr);
            processCall(fragment);
        } catch (IllegalArgumentException e) {
            logger.error(
                    "Fragment \"{}\" parsing resulted in exception \"{}\"",
                    fragmentStr,
                    e.getLocalizedMessage()
            );
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private void processCall(Fragment fragment) {
        User caller = userService.findUser(fragment.getCallerMsisdn());
        User receiver = userService.findUser(fragment.getReceiverMsisdn());

        if (caller.getUserId() == null) {
            logger.error(
                    "Caller is not serviced by Romashka. Terminating fragment processing for fragment: \"{}\"", fragment
            );
            return;
        }

        if (receiver.getUserId() == null) logger.error("Receiver is not serviced by Romashka");

        CalculationRequest request = buildCalculationRequest(fragment, caller, receiver);
        CalculationResponse response = hrsClient.calculateCost(request);

        if (response != null) {
            handleCalculationResponse(caller, fragment, response);
        } else {
            logger.error("Got null response from HRS for fragment \"{}\"", fragment);
        }
    }

    private CalculationRequest buildCalculationRequest(Fragment fragment, User caller, User receiver) {
        long seconds = Duration.between(fragment.getStartTime(), fragment.getEndTime()).getSeconds();
        int durationMinutes = (int) Math.ceil(seconds / 60.0);

        return new CalculationRequest(
                fragment.getCallType(),
                new Subscriber(caller.getUserId(), caller.getMsisdn(), caller.getUserId()!=null),
                new Subscriber(receiver.getUserId(), receiver.getMsisdn(), receiver.getUserId()!=null),
                durationMinutes,
                caller.getTariffId(),
                fragment.getStartTime().toLocalDate(),
                caller.getUserParams().getPaymentDay()
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