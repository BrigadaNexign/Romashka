package rom.brt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        Long tariffId,
        Double balance,
        Integer minutes,
        LocalDate paymentDay
) { }
