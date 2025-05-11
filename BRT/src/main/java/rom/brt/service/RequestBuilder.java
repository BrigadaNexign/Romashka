package rom.brt.service;

import org.springframework.stereotype.Component;
import rom.brt.dto.CalculationRequest;
import rom.brt.dto.Fragment;
import rom.brt.dto.Subscriber;

import java.time.Duration;

/**
 * Компонент для построения запросов на расчет стоимости.
 */
@Component
public class RequestBuilder {
    /**
     * Создает запрос на расчет стоимости звонка.
     *
     * @param fragment данные о звонке
     * @param caller информация об инициаторе звонка
     * @param receiver информация о получателе звонка
     * @return сформированный запрос на расчет
     */
    public CalculationRequest build(Fragment fragment, Subscriber caller, Subscriber receiver) {
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
}
