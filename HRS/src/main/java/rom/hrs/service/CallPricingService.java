package rom.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.CallPriceDto;
import rom.hrs.entity.CallPricing;
import rom.hrs.entity.CallType;
import rom.hrs.entity.PricingType;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;
import rom.hrs.repository.CallPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с ценами звонков.
 * Определяет стоимость звонков по типам вызовов.
 */
@Service
@RequiredArgsConstructor
public class CallPricingService {
    private static final Logger logger = LoggerFactory.getLogger(CallPricingService.class);
    private final CallPricingRepository callPricingRepository;

    /**
     * Применяет стоимость звонка к расчету.
     * @param request данные запроса
     * @param tariff тариф абонента
     * @param response объект ответа для обновления
     * @throws BusinessException при ошибках расчета
     */
    public void applyCallPricing(CalculationRequest request, Tariff tariff, CalculationResponse response)
            throws BusinessException {

        List<CallPricing> pricingList = callPricingRepository.findByTariffId(tariff.getId());
        if (pricingList.isEmpty()) {
            throw new NoPricingsFoundException(tariff.getId());
        }

        PricingType pricingType = resolvePricingType(request);

        CallPricing pricing = pricingList.stream()
                .filter(it -> it.getId().getCallType() == pricingType.getCode())
                .findAny()
                .orElseThrow(() -> {
                    logger.error("No pricing found for tariff {} and pricing type {}",
                            tariff.getId(), pricingType);
                    return new PricingNotFoundException(tariff.getId(), pricingType);
                });

        response.setCost(response.getCost() + pricing.getCostPerMin().doubleValue() * request.getDurationMinutes());
    }

    public List<CallPriceDto> findListOfDtoById(Long tariffId) {
        return callPricingRepository.findByTariffId(tariffId).stream()
                .map(callPricing -> CallPriceDto.builder()
                        .callType(callPricing.getId().getCallType())
                        .pricePerMinute(callPricing.getCostPerMin().doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    public PricingType resolvePricingType(CalculationRequest request) throws InvalidCallTypeException, UnsupportedCallServiceCombinationException {
        CallType callType = request.getCallTypeAsEnum();
        boolean isCallerServiced = request.getCaller().isServiced();
        boolean isReceiverServiced = request.getReceiver().isServiced();

        if (callType == CallType.OUTGOING) {
            if (isCallerServiced && isReceiverServiced) {
                return PricingType.OUTGOING_BOTH_SERVICED;
            } else if (isCallerServiced) {
                return PricingType.OUTGOING_CALLER_SERVICED;
            }
        } else if (callType == CallType.INCOMING) {
            if (isCallerServiced && isReceiverServiced) {
                return PricingType.INCOMING_BOTH_SERVICED;
            } else if (isCallerServiced) {
                return PricingType.INCOMING_CALLER_SERVICED;
            }
        }

        throw new UnsupportedCallServiceCombinationException(
                callType,
                isCallerServiced,
                isReceiverServiced
        );
    }
}
