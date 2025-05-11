package rom.brt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record BalanceUpdate(
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        @NotNull @Positive Double amount
) {}