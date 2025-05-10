package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "tariff_params")
@Builder
@Getter
public class TariffParameter {
    @EmbeddedId
    private TariffParameterId id;

    @Column(name = "param_value", nullable = false)
    private BigDecimal value;

    @MapsId("tariffId")
    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @MapsId("paramId")
    @ManyToOne
    @JoinColumn(name = "param_id", nullable = false)
    private Parameter parameter;
}

