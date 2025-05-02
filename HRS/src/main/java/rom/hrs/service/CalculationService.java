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
            Tariff tariff = tariffService.findTariffById(request.getCaller().getTariffId());
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
                        request.getCaller().getTariffId()
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
        
        return response;
    }

    private CalculationResponse handlePerMinuteTariff(CalculationRequest request, Tariff tariff) {
        CalculationResponse response = new CalculationResponse();

        if (hasFreeMinutes(request)) {
            feeHasMinutes(request, tariff, response);
        } else {
            feeNoMinutes(request, tariff, response);
        }

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

        return response;
    }

    private void feeIntervalPayment(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        List<TariffIntervalDto> tariffIntervalDtoList = getIntervalsByTariffId(tariff.getId());
        if (tariffIntervalDtoList.size() == 1) {
            feeCostForInterval(request, tariffIntervalDtoList.get(0), response);
        } else {
            logger.error("Tariff with id={} has multiple or none intervals", tariff.getId());
        }
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
        if (callPricingList.size() == 1) {
            feeCostForMinutes(request.getDurationMinutes(), callPricingList.get(0), response);
        } else {
            logger.error("Tariff with id={} has multiple or none pricings", tariff.getId());
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
        return request.getCaller().getMinutes() > 0;
    }

    public List<TariffIntervalDto> getIntervalsByTariffId(Integer tariffId) {
        return tariffIntervalRepository.findByTariffId(tariffId).stream()
                .map(TariffIntervalDto::fromEntity)
                .collect(Collectors.toList());
    }

    private int feeMinutesForMinutes(CalculationRequest request) {
        return request.getCaller().getMinutes() - request.getDurationMinutes();
    }

    private void feeCostForInterval(
            CalculationRequest request,
            TariffIntervalDto tariffIntervalData,
            CalculationResponse response
    ) {
         response.setNextPaymentDate(
                 request.getCaller().getPaymentDay().plusDays(tariffIntervalData.getInterval())
         );
         response.setCost(
                 response.getCost() + tariffIntervalData.getPrice().doubleValue()
         );
    }

    private boolean checkIntervalPayment(CalculationRequest request) {
        return request.getCaller().getPaymentDay().isAfter(request.getCurrentDate());
    }

    private CalculationResponse handleError() {
        // TODO()
        return null;
    }
}
