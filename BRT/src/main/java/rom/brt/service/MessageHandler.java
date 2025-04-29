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
import rom.brt.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MessageHandler {
    private final HRSClient hrsClient;
    private final UserRepository userRepository;
    private final CallRecordRepository callRecordRepository;
    private final BillingService billingService;
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public void handleMessage(String message) {
        Arrays.stream(message.split("\n"))
                .forEach(this::handleFragment);
    }

    private void handleFragment(String fragmentStr) {
        try {
            Fragment fragment = Fragment.fromString(fragmentStr);
            if (fragment != null) {
                processCall(fragment);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private void processCall(Fragment fragment) {
        User caller = userRepository.findByMsisdn(fragment.getCallerMsisdn());
        User receiver = userRepository.findByMsisdn(fragment.getReceiverMsisdn());

        if (caller == null || receiver == null) {
            return;
        }

        CalculationRequest request = buildCalculationRequest(fragment, caller, receiver);
        CalculationResponse response = hrsClient.calculateCost(request);

        if (response != null) {
            handleCalculationResponse(caller, fragment, response);
        }
    }

    private CalculationRequest buildCalculationRequest(Fragment fragment, User caller, User receiver) {
        long seconds = Duration.between(fragment.getStartTime(), fragment.getEndTime()).getSeconds();
        int durationMinutes = (int) Math.ceil(seconds / 60.0);

        return new CalculationRequest(
                fragment.getCallType(),
                new Subscriber(caller.getUserId(), caller.getMsisdn(), true),
                new Subscriber(receiver.getUserId(), receiver.getMsisdn(), true),
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