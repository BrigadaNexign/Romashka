package rom.crm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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


/**
 * Контроллер для управления абонентами и тарифами (для менеджеров)
 */
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Tag(name = "Manager Operations", description = "API для управления абонентами и тарифами")
public class ManagerController {

    private final HrsProxyService hrsProxyService;
    private final BrtProxyService brtProxyService;

    @Operation(summary = "Создать абонента", description = "Создает нового абонента с указанным тарифом")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Абонент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные входные данные")
    })
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

    @Operation(summary = "Пополнить баланс абоненту", description = "Пополняет баланс указанного абонента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Баланс успешно пополнен"),
            @ApiResponse(responseCode = "404", description = "Абонент не найден")
    })
    @PostMapping("/subscriber/{msisdn}/balance/top-up")
    public ResponseEntity<Void> topUpBalanceForSubscriber(
            @PathVariable @NotNull @NotEmpty String msisdn,
            @RequestBody @Valid BalanceUpdate request
    ) {
        brtProxyService.updateBalance(msisdn, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Сменить тариф абоненту", description = "Изменяет тарифный план абонента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тариф успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Абонент или тариф не найден")
    })
    @PostMapping("/subscriber/{msisdn}/tariff/change-tariff")
    public ResponseEntity<Void> changeSubscriberTariff(
            @PathVariable @NotNull @NotEmpty String msisdn,
            @RequestBody @Valid ChangeTariffRequest request
    ) {
        brtProxyService.changeUserTariff(msisdn, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить информацию об абоненте", description = "Возвращает полную информацию об абоненте")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация об абоненте"),
            @ApiResponse(responseCode = "404", description = "Абонент не найден")
    })
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

    @Operation(summary = "Получить детали тарифа", description = "Возвращает информацию о конкретном тарифе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о тарифе"),
            @ApiResponse(responseCode = "404", description = "Тариф не найден")
    })
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

    @Operation(summary = "Получить все тарифы", description = "Возвращает список доступных тарифов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список тарифов"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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