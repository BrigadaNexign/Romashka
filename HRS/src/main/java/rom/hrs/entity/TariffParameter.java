package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tariff_params")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TariffParameterId implements Serializable {
        @Column(name = "tariff_id")
        private Long tariffId;

        @Column(name = "param_id")
        private Long paramId;
    }
}

