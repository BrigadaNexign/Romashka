package rom.crm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.UserResponse;

@FeignClient(
        name = "brt",
        url = "${services.brt.url}${services.brt.api.mappings.user.base}"
)
public interface BrtClient {

    @GetMapping("/{msisdn}")
    ResponseEntity<UserResponse> getUserByMsisdn(
            @PathVariable String msisdn
    );

    @PostMapping("/{msisdn}/balance/top-up")
    ResponseEntity<Void> topUpBalance(
            @PathVariable String msisdn,
            @RequestBody BalanceUpdate dto
    );

    @PostMapping("/create")
    ResponseEntity<Void> createUser(
            @RequestBody UserUpdateRequest request
    );

    @PostMapping("/{msisdn}/tariff/change")
    ResponseEntity<Void> changeUserTariff(
            @PathVariable String msisdn,
            @RequestBody ChangeTariffRequest request
    );

    @GetMapping("/health")
    ResponseEntity<Void> checkHealth();
}