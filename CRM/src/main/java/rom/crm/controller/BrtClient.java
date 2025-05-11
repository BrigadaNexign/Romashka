package rom.crm.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.UserResponse;

/**
 * Feign клиент для взаимодействия с BRT.
 * Предоставляет методы для работы с абонентами и их тарифами.
 */
@FeignClient(
        name = "brt-service",
        url = "${services.brt.url}${services.brt.api.mappings.user.base}"
)
public interface BrtClient {

    @GetMapping("${services.brt.api.mappings.user.get}")
    ResponseEntity<UserResponse> getUserByMsisdn(
            @PathVariable String msisdn
    );

    @PostMapping("${services.brt.api.mappings.top-up}")
    ResponseEntity<Void> topUpBalance(
            @PathVariable String msisdn,
            @RequestBody BalanceUpdate dto
    );

    @PostMapping("${services.brt.api.mappings.user.create}")
    ResponseEntity<Void> createUser(
            @RequestBody UserUpdateRequest request
    );

    @PostMapping("${services.brt.api.mappings.tariff.change}")
    ResponseEntity<Void> changeUserTariff(
            @PathVariable String msisdn,
            @RequestBody ChangeTariffRequest request
    );
}