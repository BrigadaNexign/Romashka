package rom.crm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.SubscriberInfoResponse;
import rom.crm.dto.response.TariffResponse;
import rom.crm.dto.response.UserResponse;
import rom.crm.exception.ExternalServiceException;
import rom.crm.exception.TariffNotFoundException;
import rom.crm.service.BrtProxyService;
import rom.crm.service.HrsProxyService;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final HrsProxyService hrsProxyService;
    private final BrtProxyService brtProxyService;

    @PostMapping("/subscriber/create")
    public ResponseEntity<Void> createSubscriber(@RequestBody @Valid UserUpdateRequest request) {
        try {
            TariffResponse tariffResponse = hrsProxyService.getTariffById(request.tariffId());
            brtProxyService.createUserWithTariff(request, tariffResponse);
        } catch (TariffNotFoundException | ExternalServiceException e) {
            brtProxyService.createUser(request);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriber/{msisdn}/balance/top-up")
    public ResponseEntity<Void> topUpBalanceForSubscriber(
            @PathVariable @NotNull @NotEmpty String msisdn,
            @RequestBody @Valid BalanceUpdate request
    ) {
        brtProxyService.updateBalance(msisdn, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("subscriber/{msisdn}/tariff/change-tariff")
    public ResponseEntity<Void> changeSubscriberTariff(
            @PathVariable @NotNull @NotEmpty String msisdn,
            @RequestBody @Valid ChangeTariffRequest request
    ) {
        brtProxyService.changeUserTariff(msisdn, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscriber/{msisdn}")
    public ResponseEntity<SubscriberInfoResponse> getSubscriberInfo(@PathVariable String msisdn) {
        UserResponse user = brtProxyService.getUserByMsisdn(msisdn);
        try {
            TariffResponse tariff = hrsProxyService.getTariffById(user.tariffId());
            return ResponseEntity.ok(new SubscriberInfoResponse(user, tariff));
        } catch (TariffNotFoundException | ExternalServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tariffs/{tariffId}")
    public ResponseEntity<TariffResponse> getTariffDetails(@PathVariable long tariffId) {
        try {
            return ResponseEntity.ok(hrsProxyService.getTariffById(tariffId));
        } catch (TariffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExternalServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/tariffs")
    public ResponseEntity<List<TariffResponse>> getAllAvailableTariffs(
            @RequestParam(required = false) String sortBy
    ) {
        try {
            return ResponseEntity.ok(hrsProxyService.getAllTariffs(sortBy));
        } catch (ExternalServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}