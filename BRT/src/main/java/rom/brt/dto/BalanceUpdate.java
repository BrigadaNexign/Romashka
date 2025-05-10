package rom.brt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceUpdate {
    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "MSISDN must be 11 digits")
    private String msisdn;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}