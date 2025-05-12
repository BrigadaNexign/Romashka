package rom.brt.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rom.brt.dto.request.CalculationRequest;
import rom.brt.dto.response.CalculationResponse;


@FeignClient(name = "hrs-client", url = "${hrs.url}")
public interface HRSClient {
    @PostMapping("/calculate")
    CalculationResponse calculateCost(@RequestBody CalculationRequest request);
}

