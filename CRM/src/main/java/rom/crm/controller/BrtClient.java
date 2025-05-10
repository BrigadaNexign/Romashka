package rom.crm.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.UserResponse;

@FeignClient(
        name = "brt-service",
        url = "${services.brt.url}"
)
public interface BrtClient {

    @GetMapping("/users/{msisdn}")
    ResponseEntity<UserResponse> getUserByMsisdn(
            @PathVariable String msisdn

    );
    @PatchMapping("/users/{msisdn}/balance")
    ResponseEntity<Void> updateBalance(
            @PathVariable String msisdn,
            @RequestBody BalanceUpdate dto,
            @RequestHeader("Authorization") String authHeader
    );

    @PatchMapping("/users/create")
    ResponseEntity<Void> createUser(
            @RequestBody UserUpdateRequest request
    );
}