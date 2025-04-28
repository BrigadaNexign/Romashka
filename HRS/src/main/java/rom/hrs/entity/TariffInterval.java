package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tariff_intervals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffInterval {
    @EmbeddedId
    private TariffIntervalId id;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @MapsId("tariffId")
    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;
}

