package rom.hrs.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для ответа с информацией о параметре тарифа.
 */
@Data
@Getter
@Setter
@Builder
public class TariffParamResponse {
    private String name;
    private String description;
    private Double value;
    private String units;
}
       

