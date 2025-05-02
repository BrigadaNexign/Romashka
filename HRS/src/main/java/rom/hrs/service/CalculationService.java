package rom.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.TariffIntervalDto;
import rom.hrs.entity.CallPricing;
import rom.hrs.entity.Tariff;
import rom.hrs.repository.TariffIntervalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculationService {
    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    @Autowired
    private TariffService tariffService;
    @Autowired
    private TariffTypeService tariffTypeService;
    @Autowired
    private CallPricingService callPricingService;
    @Autowired
    private TariffIntervalRepository tariffIntervalRepository;

    /**
     * Основной метод расчета стоимости звонка
     */
    public CalculationResponse calculate(CalculationRequest request) {
        try {
            Tariff tariff = tariffService.findTariffById(request.getCaller().tariffId());
            logger.info("Trying to handle request: \"{}\"", request);
            return handleCalculationRequest(request, tariff);
        } catch (Exception e) {
            logger.error("Error handling request: \"{}\": \"{}\"", request, e.getMessage());
            return handleError();
        }
    }

    private CalculationResponse handleCalculationRequest(CalculationRequest request, Tariff tariff) {
        logger.info(
                "Tariff for request \"{}\" is of type {}",
                request,
                tariffTypeService.getTypeNameById(tariff.getType())
        );

        return switch (tariff.getType()) {
            case 1 -> handleIntervalTariff(request, tariff);
            case 2 -> handlePerMinuteTariff(request, tariff);
            case 3 -> handleCombinedTariff(request, tariff);
            default -> {
                logger.error(
                        "Error handling request: \"{}\": Tariff with id={} exists, but not supported by current version",
                        request,
                        request.getCaller().tariffId()
                );
                yield handleError();
            }
        };
    }

    private CalculationResponse handleIntervalTariff(CalculationRequest request, Tariff tariff) {
        CalculationResponse response = new CalculationResponse();
        if (checkIntervalPayment(request)) {
           feeIntervalPayment(request, tariff, response);
        }

        fillEmptyResponseFields(request, tariff, response);
        return response;
    }

    private CalculationResponse handlePerMinuteTariff(CalculationRequest request, Tariff tariff) {
        CalculationResponse response = new CalculationResponse();

        if (hasFreeMinutes(request)) {
            feeHasMinutes(request, tariff, response);
        } else {
            feeNoMinutes(request, tariff, response);
        }

        fillEmptyResponseFields(request, tariff, response);
        return response;
    }

    private CalculationResponse handleCombinedTariff(CalculationRequest request, Tariff tariff) {
        CalculationResponse response = new CalculationResponse();
        if (checkIntervalPayment(request)) {
            feeIntervalPayment(request, tariff, response);
        }

        if (hasFreeMinutes(request)) {
            feeHasMinutes(request, tariff, response);
        } else {
            feeNoMinutes(request, tariff, response);
        }

        fillEmptyResponseFields(request, tariff, response);
        return response;
    }

    private void fillEmptyResponseFields(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        logger.info("Filling empty fields for response: {}", response);
        if (response.getTariffType() == null) response.setTariffType(String.valueOf(tariff.getType()));
        if (response.getRemainingMinutes() == null) response.setRemainingMinutes(request.getCaller().minutes());
        if (response.getNextPaymentDate() == null) response.setNextPaymentDate(request.getCaller().paymentDay());
        logger.info("Finished filling empty fields for response: {}", response);
    }

    private void feeIntervalPayment(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        List<TariffIntervalDto> tariffIntervalDtoList = getIntervalsByTariffId(tariff.getId());
        if (tariffIntervalDtoList.size() != 1) logger.error(
                "Tariff with id={} has multiple or none intervals. Using one that was found first",
                tariff.getId()
        );
        feeCostForInterval(request, tariffIntervalDtoList.get(0), response);
    }

    private void feeHasMinutes(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        int remainingMinutes = feeMinutesForMinutes(request);
        if (remainingMinutes >= 0) {
            response.setRemainingMinutes(remainingMinutes);
        } else {
            feeNoMinutes(request, tariff, response);
        }
    }

    private void feeNoMinutes(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        List<CallPricing> callPricingList = callPricingService.getCallPricingListByTariffId(tariff.getId());
        int callPricingType = getCallPricingType(request);
        CallPricing callPricing = callPricingList.stream()
                .filter(it -> it.getId().getCallType().equals(callPricingType))
                .findAny()
                .orElse(null);

        if (callPricing != null) {
            feeCostForMinutes(request.getDurationMinutes(), callPricing, response);
        } else {
            logger.error("No pricings found for tariff with id={}", tariff.getId());
            throw new IllegalArgumentException("No pricings found for tariff with id=" + tariff.getId());
        }
    }

    private void feeCostForMinutes(
            int durationMinutes,
            CallPricing callPricing,
            CalculationResponse response
    ) {
        response.setCost(
                response.getCost() + callPricing.getCostPerMin().doubleValue() * durationMinutes
        );
    }

    private boolean hasFreeMinutes(CalculationRequest request) {
        return request.getCaller().minutes() > 0;
    }

    public List<TariffIntervalDto> getIntervalsByTariffId(Integer tariffId) {
        return tariffIntervalRepository.findByTariffId(tariffId).stream()
                .map(TariffIntervalDto::fromEntity)
                .collect(Collectors.toList());
    }

    private int feeMinutesForMinutes(CalculationRequest request) {
        return request.getCaller().minutes() - request.getDurationMinutes();
    }

    private void feeCostForInterval(
            CalculationRequest request,
            TariffIntervalDto tariffIntervalData,
            CalculationResponse response
    ) {
         response.setNextPaymentDate(
                 request.getCaller().paymentDay().plusDays(tariffIntervalData.getInterval())
         );
         response.setCost(
                 response.getCost() + tariffIntervalData.getPrice().doubleValue()
         );
    }

    private boolean checkIntervalPayment(CalculationRequest request) {
        return request.getCaller().paymentDay().isBefore(request.getCurrentDate());
    }

    private int getCallPricingType(CalculationRequest request) {
        /*
         * Тип вызова:
         * - "01" — исходящий вызов,
         * - "02" — входящий вызов.
         */
        String callType = request.getCallType();
        boolean isCallerServiced = request.getCaller().isServiced();
        boolean isReceiverServiced = request.getReceiver().isServiced();
        if (callType.equals("01") && isCallerServiced && isReceiverServiced) {
            return 1;
        } else if (callType.equals("01") && isCallerServiced) {
            return 2;
        } else if (callType.equals("02") && isCallerServiced && isReceiverServiced) {
            return 3;
        } else if (callType.equals("02") && isCallerServiced) {
            return 4;
        } else if (!(callType.equals("01") || callType.equals("02"))) {
            throw new IllegalArgumentException("Illegal call type in request");
        } else throw new IllegalArgumentException("Caller is not serviced by Romashka");
    }

    private CalculationResponse handleError() {
        // TODO()
        return null;
    }
}
