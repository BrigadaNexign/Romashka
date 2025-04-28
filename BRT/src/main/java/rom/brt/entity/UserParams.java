package rom.brt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_params")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserParams {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "payment_day")
    private LocalDate paymentDay;

    @Column(name = "minutes")
    private Integer minutes;
}
