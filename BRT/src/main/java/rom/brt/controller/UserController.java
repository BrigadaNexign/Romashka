package rom.brt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.brt.dto.request.BalanceUpdate;
import rom.brt.dto.request.ChangeTariffRequest;
import rom.brt.dto.response.UserResponse;
import rom.brt.dto.request.UserUpdateRequest;
import rom.brt.exception.UserNotFoundException;
import rom.brt.service.UserService;

/**
 * Контроллер для управления пользователями (абонентами) системы.
 * Обрабатывает HTTP-запросы, связанные с операциями над пользователями.
 */
@RestController
@RequestMapping("${services.brt.api.mappings.user.base}")
@RequiredArgsConstructor
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Получает информацию о пользователе по номеру телефона (MSISDN).
     *
     * @param msisdn Номер телефона пользователя в формате строки
     * @return ResponseEntity с данными пользователя или ошибкой 400 если пользователь не найден
     */
    @GetMapping("/{msisdn}")
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

    /**
     * Создает нового пользователя в системе.
     *
     * @param request DTO с данными для создания пользователя
     * @return ResponseEntity с кодом 200 при успешном создании
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createUser(
            @RequestBody @Valid UserUpdateRequest request
    ) {
        logger.info("Got createUser request");
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Пополняет баланс пользователя.
     *
     * @param msisdn Номер телефона пользователя
     * @param dto DTO с суммой пополнения
     * @return ResponseEntity с кодом 200 при успехе или 400 если пользователь не найден
     */
    @PostMapping("/{msisdn}/balance/top-up")
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

    /**
     * Изменяет тарифный план пользователя.
     *
     * @param msisdn Номер телефона пользователя
     * @param request DTO с ID нового тарифа
     * @return ResponseEntity с кодом 200 при успехе или 400 если пользователь не найден
     */
    @PostMapping("/{msisdn}/tariff/change")
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