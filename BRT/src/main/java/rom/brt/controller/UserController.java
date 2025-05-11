package rom.brt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.brt.dto.BalanceUpdate;
import rom.brt.dto.ChangeTariffRequest;
import rom.brt.dto.UserResponse;
import rom.brt.dto.UserUpdateRequest;
import rom.brt.exception.UserNotFoundException;
import rom.brt.service.UserService;

@RestController
@RequestMapping("${services.brt.api.mappings.user.base}")
@RequiredArgsConstructor
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("${services.brt.api.mappings.user.get}")
    public ResponseEntity<UserResponse> getUserByMsisdn(
            @PathVariable String msisdn
    ) {
        logger.info("Got getUserByMsisdn request");
        try {
            UserResponse user = userService.getUserByMsisdn(msisdn);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            logError(e);
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("${services.brt.api.mappings.user.create}")
    public ResponseEntity<Void> createUser(
            @RequestBody @Valid UserUpdateRequest request
    ) {
        logger.info("Got createUser request");
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("${services.brt.api.mappings.top-up}")
    public ResponseEntity<Void> topUpBalance(
            @PathVariable String msisdn,
            @RequestBody @Valid BalanceUpdate dto
    ) {
        logger.info("Got topUpBalance request");
        try {
            userService.topUpBalance(msisdn, dto);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            logError(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("${services.brt.api.mappings.tariff.change}")
    public ResponseEntity<Void> changeUserTariff(
            @PathVariable String msisdn,
            @RequestBody @Valid ChangeTariffRequest request
    ) {
        logger.info("Got changeUserTariff request");
        try {
            userService.changeUserTariff(msisdn, request);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            logError(e);
            return ResponseEntity.badRequest().build();
        }
    }

    private void logError(Exception e) {
        logger.error("Got error: {}", e.getMessage());
    }
}
