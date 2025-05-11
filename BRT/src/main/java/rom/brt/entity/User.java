package rom.brt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность абонента телефонной сети.
 * Хранит основную информацию о пользователе и его параметры.
 */
@Entity
@Table(name = "users_saved")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "tariff_id")
    private Long tariffId;

    @Column(name = "msisdn", nullable = false, length = 11, unique = true)
    private String msisdn;

    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserParams userParams;

    public void setUserParams(UserParams params) {
        params.setUser(this);
        this.userParams = params;
    }
}
