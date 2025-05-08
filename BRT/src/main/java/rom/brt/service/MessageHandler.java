package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.client.HRSClient;
import rom.brt.dto.*;
import rom.brt.entity.User;
import rom.brt.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class MessageHandler {
    private final HRSClient hrsClient;
    private final UserService userService;
    private final FragmentMapper fragmentMapper;
    private final RequestBuilder requestBuilder;
    private final ResponseHandler responseHandler;

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public void handleMessage(String message) {
        try {
            for (Fragment fragment: fragmentMapper.parseCsv(message)) {
                processCall(fragment);
            }
        } catch (Exception e) {
            // TODO: exception handling
            logger.error(e.getLocalizedMessage());
        }
    }

    public void processCall(Fragment fragment) throws BusinessException {
        User callerRecord = userService.findUser(fragment.getCallerMsisdn());
        logger.debug("Got callerRecord: {}", callerRecord);
        User receiverRecord = userService.findUser(fragment.getReceiverMsisdn());
        logger.debug("Got receiverRecord: {}", receiverRecord);

        Subscriber caller;
        Subscriber receiver;

        if (callerRecord.getUserId() == -1) {
            logger.info("Caller is not serviced by Romashka. Terminating fragment processing");
            return;
        }

        caller = userService.createServicedSubscriberFromRecord(callerRecord);
        logger.debug("Created caller: {}", caller);

        if (receiverRecord.getUserId() == -1) {
            logger.debug("Receiver {} is not serviced by Romashka", receiverRecord.getMsisdn());
            receiver = userService.createForeignSubscriberFromRecord(receiverRecord);
        } else {
            receiver = userService.createServicedSubscriberFromRecord(receiverRecord);
        }

        logger.debug("Created receiver: {}", receiver);

        CalculationRequest request = requestBuilder.build(fragment, caller, receiver);
        logger.info("Sent request: {}", request);
        CalculationResponse response = hrsClient.calculateCost(request);
        logger.info("Received response: {}", response);


        responseHandler.handleCalculationResponse(callerRecord, fragment, response);
    }
}