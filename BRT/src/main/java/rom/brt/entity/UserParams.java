package rom.brt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Дополнительные параметры пользователя.
 * Связана one-to-one с сущностью User.
 */
@Entity
@Table(name = "user_params")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserParams {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "payment_day")
    private LocalDate paymentDay;

    @Column(name = "minutes")
    private Integer minutes;
}
