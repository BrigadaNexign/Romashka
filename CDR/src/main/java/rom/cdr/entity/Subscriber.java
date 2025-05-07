package rom.cdr.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

/**
 * Класс, представляющий сущность абонента.
 * Абонент идентифицируется по номеру MSISDN.
 */
@Data
@Entity
@Table(
        name = "subscriber",
        uniqueConstraints = { @UniqueConstraint(columnNames = "msisdn") }
)
@Getter
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "msisdn", nullable = false, length = 12)
    private String msisdn;

}
