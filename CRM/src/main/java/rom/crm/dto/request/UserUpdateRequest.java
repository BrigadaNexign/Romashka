package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Запрос на создание/обновление пользователя
public record UserUpdateRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        Long tariffId, Double balance
) { }
