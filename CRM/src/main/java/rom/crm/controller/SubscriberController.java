package rom.crm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.response.SubscriberInfoResponse;
import rom.crm.dto.response.TariffResponse;
import rom.crm.dto.response.UserResponse;
import rom.crm.entity.User;
import rom.crm.exception.ExternalServiceException;
import rom.crm.exception.TariffNotFoundException;
import rom.crm.service.BrtProxyService;
import rom.crm.service.HrsProxyService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Subscriber Operations")
public class SubscriberController {
    private final BrtProxyService brtProxyService;

    @PostMapping("/subscriber/balance/top-up")
    public ResponseEntity<Void> topUpBalance(@RequestBody @Valid BalanceUpdate request) {
        String authenticatedMsisdn = getAuthenticatedMsisdn();
        if (!authenticatedMsisdn.equals(request.msisdn())) {
            throw new SecurityException("Unauthorized: Can only top up own balance");
        }
        brtProxyService.updateBalance(request.msisdn(), request);
        return ResponseEntity.ok().build();
    }

    private String getAuthenticatedMsisdn() {
        var authentication = SecurityContextHolder.getContext();
        var userDetails = (User) authentication.getAuthentication().getPrincipal();
        return userDetails.getMsisdn();
    }
}
