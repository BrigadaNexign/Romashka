package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rom.brt.dto.CalculationResponse;
import rom.brt.dto.Fragment;
import rom.brt.entity.CallRecord;
import rom.brt.entity.User;
import rom.brt.exception.BusinessException;
import rom.brt.exception.EmptyResponseFieldException;
import rom.brt.exception.FailedResponseException;

@Service
@RequiredArgsConstructor
public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private final CallRecordService callRecordService;
    private final BillingService billingService;

    public void handleCalculationResponse(User caller, Fragment fragment, CalculationResponse response)
            throws BusinessException
    {
        validate(response);
        billingService.processBilling(caller, response);

        CallRecord callRecord = callRecordService.saveCallRecord(fragment, response);
        logger.info("Saved call record: {}", callRecord);
    }

    public void validate(CalculationResponse calculationResponse) throws BusinessException {
        if (calculationResponse == null) throw new EmptyResponseFieldException("Response");

        if (!calculationResponse.isSuccess()) throw new FailedResponseException(
                calculationResponse.getErrorCode(), calculationResponse.getErrorMessage()
        );

        if (calculationResponse.getCost() == null) throw new EmptyResponseFieldException("cost");
        if (calculationResponse.getTariffType() == null) throw new EmptyResponseFieldException("tariffType");
        if (calculationResponse.getDescription() == null) throw new EmptyResponseFieldException("description");
        if (calculationResponse.getRemainingMinutes() == null) throw new EmptyResponseFieldException("remainingMinutes");
        if (calculationResponse.getNextPaymentDate() == null) throw new EmptyResponseFieldException("nextPaymentDate");
    }
}
