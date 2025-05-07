package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.*;
import rom.hrs.exception.InvalidPricingTypeException;

import java.math.BigDecimal;

@Entity
@Table(name = "call_pricing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallPricing {
    @EmbeddedId
    private CallPricingId id;

    @Column(name = "cost_per_min", precision = 10, scale = 2)
    private BigDecimal costPerMin;

    @MapsId("tariffId")
    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Transient
    public PricingType getPricingType() throws InvalidPricingTypeException {
        return PricingType.fromCode(id.getCallType());
    }
}