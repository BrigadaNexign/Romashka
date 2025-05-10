package rom.crm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.SubscriberInfoResponse;
import rom.crm.dto.response.TariffResponse;
import rom.crm.dto.response.UserResponse;
import rom.crm.entity.ServiceName;
import rom.crm.entity.User;
import rom.crm.service.BrtProxyService;
import rom.crm.service.HrsProxyService;
import rom.crm.service.JwtService;

@RestController
@RequestMapping("/api/v1.1/subscribers")
@RequiredArgsConstructor
@Tag(name = "Subscriber Operations")
public class SubscriberController {
    private final BrtProxyService brtProxyService;
    private final HrsProxyService hrsProxyService;
    private final JwtService jwtService;

    @Operation(summary = "Top up balance for the authenticated subscriber")
    @PostMapping("/balance/topup")
    public ResponseEntity<Void> topUpBalance(@RequestBody @Valid BalanceUpdate request) {
        String authenticatedMsisdn = getAuthenticatedMsisdn();
        if (!authenticatedMsisdn.equals(request.msisdn())) {
            throw new SecurityException("Unauthorized: Can only top up own balance");
        }
        String jwt = jwtService.generateServiceToken(ServiceName.BRT, request.msisdn(), "balance.write");
        brtProxyService.updateBalance(request.msisdn(), request, jwt);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Top up balance for any subscriber (Manager only)")
    @PostMapping("/{msisdn}/balance")
    public ResponseEntity<Void> topUpBalanceForSubscriber(
            @PathVariable String msisdn,
            @RequestBody @Valid BalanceUpdate request) {
        String jwt = jwtService.generateServiceToken(ServiceName.BRT, msisdn, "balance.write");
        brtProxyService.updateBalance(msisdn, request, jwt);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change tariff for a subscriber (Manager only)")
    @PostMapping("/{msisdn}/tariff")
    public ResponseEntity<Void> changeTariff(
            @PathVariable String msisdn,
            @RequestBody @Valid ChangeTariffRequest request) {
        String jwt = jwtService.generateServiceToken(ServiceName.HRS, msisdn, "tariff.update");
        hrsProxyService.changeTariff(msisdn, request, jwt);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create a new subscriber (Manager only)")
    @PostMapping("/new")
    public ResponseEntity<Void> createSubscriber(@RequestBody @Valid UserUpdateRequest request) {
        String jwt = jwtService.generateServiceToken(ServiceName.BRT, request.msisdn(), "user.create");
        brtProxyService.createUser(request, jwt);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get subscriber and tariff info (Manager only)")
    @GetMapping("/{msisdn}/info")
    public ResponseEntity<SubscriberInfoResponse> getSubscriberInfo(@PathVariable String msisdn) {
        String brtJwt = jwtService.generateServiceToken(ServiceName.BRT, msisdn, "user.read");
        String hrsJwt = jwtService.generateServiceToken(ServiceName.HRS, msisdn, "tariff.read");
        UserResponse user = brtProxyService.getUserByMsisdn(msisdn, brtJwt);
        TariffResponse tariff = hrsProxyService.getTariff(user.tariffId(), hrsJwt);
        return ResponseEntity.ok(new SubscriberInfoResponse(user, tariff));
    }

    private String getAuthenticatedMsisdn() {
        var authentication = SecurityContextHolder.getContext();
        var userDetails = (User) authentication.getAuthentication().getPrincipal();
        return userDetails.getMsisdn();
    }
}
