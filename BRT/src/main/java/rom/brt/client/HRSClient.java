package rom.brt.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rom.brt.dto.CalculationRequest;
import rom.brt.dto.CalculationResponse;

@FeignClient(name = "hrs-client", url = "${hrs.url}")
public interface HRSClient {

    @PostMapping("/hrs/calculate")
    CalculationResponse calculateCost(@RequestBody CalculationRequest request);

}

