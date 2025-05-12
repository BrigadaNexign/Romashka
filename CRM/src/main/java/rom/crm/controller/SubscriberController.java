package rom.crm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.entity.User;
import rom.crm.service.BrtProxyService;

/**
 * Контроллер для операций абонентов
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Subscriber Operations", description = "API для операций абонентов")
public class SubscriberController {
    private final BrtProxyService brtProxyService;

    @Operation(
            summary = "Пополнить баланс",
            description = "Позволяет абоненту пополнить свой баланс. " +
                    "Номер для пополнения должен быть равен номеру пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Баланс успешно пополнен"),
            @ApiResponse(responseCode = "403", description = "Попытка пополнить чужой баланс"),
            @ApiResponse(responseCode = "404", description = "Абонент не найден")
    })
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
