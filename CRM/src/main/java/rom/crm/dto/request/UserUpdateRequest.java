package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Запрос на создание/обновление пользователя
public record UserUpdateRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        Long tariffId,
        Double balance,
        Integer minutes,
        LocalDate paymentDay
) {
    public UserUpdateRequest {
        balance = balance != null ? balance : 100.0;
        minutes = minutes != null ? minutes : 0;
    }
}
