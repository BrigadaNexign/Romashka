package rom.crm.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.CreateTariffRequest;
import rom.crm.dto.response.TariffResponse;

import java.util.List;

@FeignClient(
        name = "hrs",
        url = "${services.hrs.url}${services.hrs.api.mappings.tariff.base}"
)
public interface HrsClient {

    @GetMapping
    ResponseEntity<List<TariffResponse>> getAllTariffs(
            @RequestParam(required = false) String sortBy
    );

    @GetMapping("/{tariffId}")
    ResponseEntity<TariffResponse> getTariffById(
            @PathVariable long tariffId
    );

    @PostMapping("/create")
    ResponseEntity<Void> createTariff(
            @RequestBody @Valid CreateTariffRequest request
    );

    @DeleteMapping("/delete/{tariffId}")
    ResponseEntity<Void> deleteTariff(
            @PathVariable long tariffId
    );

    @GetMapping("/health")
    ResponseEntity<Void> checkHealth();
}