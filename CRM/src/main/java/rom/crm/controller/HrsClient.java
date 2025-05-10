package rom.crm.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.CreateTariffRequest;
import rom.crm.dto.response.TariffResponse;

@FeignClient(
        name = "hrs-service",
        url = "${services.hrs.url}"
)
public interface HrsClient {

    @GetMapping("/tariff/{tariffId}")
    ResponseEntity<TariffResponse> getTariff(
            @PathVariable Long tariffId
    );

    @PostMapping("/tariff/change")
    ResponseEntity<Void> changeTariff(
            @RequestBody ChangeTariffRequest request
    );

    @PostMapping("/tariff/create")
    ResponseEntity<Void> changeTariff(
            @RequestBody CreateTariffRequest request
    );
}
