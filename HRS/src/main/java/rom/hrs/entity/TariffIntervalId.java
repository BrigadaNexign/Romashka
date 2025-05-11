package rom.hrs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TariffIntervalId implements Serializable {
    @Column(name = "tariff_id")
    private Long tariffId;

    @Column(name = "interval")
    private Integer interval;
}
