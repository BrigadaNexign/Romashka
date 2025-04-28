package rom.hrs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class TariffParameterId implements Serializable {
    @Column(name = "tariff_id")
    private Integer tariffId;

    @Column(name = "param_id")
    private Integer paramId;
}
