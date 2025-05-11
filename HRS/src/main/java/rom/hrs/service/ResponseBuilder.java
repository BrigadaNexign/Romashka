package rom.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.BusinessException;
import rom.hrs.exception.IncompleteResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Билдер для формирования ответов расчета.
 */
@Component
public class ResponseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);

    /**
     * Инициализирует базовый ответ для расчета.
     * @param request данные запроса
     * @param tariff тариф абонента
     * @return инициализированный ответ
     */
    public CalculationResponse initResponse(CalculationRequest request, Tariff tariff) {
        return new CalculationResponse(
                false,
                0.0,
                String.valueOf(tariff.getType()),
                tariff.getDescription(),
                request.getCaller().minutes(),
                request.getCaller().paymentDay(),
                null,
                null
        );
    }

    /**
     * Дозаполняет поля ответа
     * @return ответ с ошибкой
     */
    public CalculationResponse fillDefaultFields(CalculationRequest request, Tariff tariff, CalculationResponse response) {
        logger.debug("Filling default fields for response: {}", response);

        response.setSuccess(true);

        response.setDescription(tariff.getDescription());
        response.setTariffType(String.valueOf(tariff.getType()));

        if (response.getNextPaymentDate() == null) {
            response.setNextPaymentDate(request.getCaller().paymentDay());
        }

        logger.debug("Completed filling default fields: {}", response);
        return response;
    }

    /**
     * Создает ответ с ошибкой.
     * @return ответ с ошибкой
     */
    public CalculationResponse createErrorResponse(Exception exception) {
        CalculationResponse response = new CalculationResponse();

        response.setSuccess(false);

        if (exception instanceof BusinessException e) {
            response.setErrorCode(e.getErrorCode().getCode());
            response.setErrorMessage(e.getMessage());
        } else {
            response.setErrorCode("INTERNAL_ERROR");
            response.setErrorMessage("Internal server error");
        }

        return response;
    }

    /**
     * Валидация ответа
     */
    public void validateSuccessfulResponse(CalculationResponse response, String... requiredFields)
            throws IncompleteResponseException {

        if (response == null) {
            throw new IncompleteResponseException("All fields");
        }

        if (!response.isSuccess()) {
            return;
        }

        List<String> missingFields = new ArrayList<>();

        for (String field : requiredFields) {
            switch (field) {
                case "cost":
                    if (response.getCost() == null) missingFields.add(field);
                    break;
                case "tariffType":
                    if (response.getTariffType() == null) missingFields.add(field);
                    break;
                case "remainingMinutes":
                    if (response.getRemainingMinutes() == null) missingFields.add(field);
                    break;
                case "nextPaymentDate":
                    if (response.getNextPaymentDate() == null) missingFields.add(field);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field: " + field);
            }
        }

        if (!missingFields.isEmpty()) {
            throw new IncompleteResponseException(
                    "Response is missing required fields: " + String.join(", ", missingFields)
            );
        }
    }
}