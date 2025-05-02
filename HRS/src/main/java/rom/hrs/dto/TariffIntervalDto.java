package rom.hrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rom.hrs.entity.TariffInterval;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffIntervalDto {
    private Integer interval;
    private BigDecimal price;

    public static TariffIntervalDto fromEntity(TariffInterval entity) {
        return new TariffIntervalDto(
                entity.getId().getInterval(),
                entity.getPrice()
        );
    }
}
