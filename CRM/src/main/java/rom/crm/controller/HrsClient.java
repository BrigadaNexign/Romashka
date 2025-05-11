package rom.crm.controller;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.CreateTariffRequest;
import rom.crm.dto.response.TariffResponse;

import java.util.List;

/**
 * Feign клиент для взаимодействия с HRS сервисом.
 */
@FeignClient(
        name = "hrs",
        url = "${services.hrs.url}" + "${services.hrs.api.mappings.tariff.base}"
)
public interface HrsClient {

    @GetMapping()
    ResponseEntity<List<TariffResponse>> getAllTariffs (
            @RequestParam(required = false) String sortBy
    );

    @GetMapping("${services.hrs.api.mappings.tariff.by-id}")
    ResponseEntity<TariffResponse> getTariffById(
            @PathVariable long tariffId
    );

    @PostMapping("${services.hrs.api.mappings.tariff.create}")
    ResponseEntity<Void> createTariff(
            @RequestBody @Valid CreateTariffRequest request
    );

    @GetMapping("${services.hrs.api.mappings.tariff.delete}")
    ResponseEntity<Void> deleteTariff(
            @PathVariable long tariffId
    );
}