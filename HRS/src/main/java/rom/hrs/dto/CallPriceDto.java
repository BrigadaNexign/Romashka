package rom.hrs.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * DTO для цены звонка определенного типа.
 */
@Data
@Setter
@Getter
@Builder
public class CallPriceDto {
    private @NotNull Integer callType;
    private @NotNull @Positive Double pricePerMinute;
}
