package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriber_tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberTariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "msisdn", nullable = false, unique = true)
    private String msisdn;

    @Column(name = "tariff_id", nullable = false)
    private Integer tariffId;
}
