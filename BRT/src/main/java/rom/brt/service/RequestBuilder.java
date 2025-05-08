package rom.brt.service;

import org.springframework.stereotype.Component;
import rom.brt.dto.CalculationRequest;
import rom.brt.dto.Fragment;
import rom.brt.dto.Subscriber;

import java.time.Duration;

@Component
public class RequestBuilder {
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
