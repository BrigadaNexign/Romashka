package rom.cdr.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность Fragment.
 * Fragment содержит информацию о звонке, включая тип вызова, номера абонентов и временные метки.
 */
@Data
@Entity
@ToString
@Table(
        name = "fragment",
        indexes = {
                @Index(name = "idx_fragment_caller", columnList = "caller_msisdn"),
                @Index(name = "idx_fragment_time_range", columnList = "start_time, end_time")
        })
public class Fragment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип вызова:
     * - "01" — исходящий вызов,
     * - "02" — входящий вызов.
     */
    @Column(name = "call_type")
    private String callType;

    @Column(name = "caller_msisdn")
    private String callerMsisdn;

    @Column(name = "receiver_msisdn")
    private String receiverMsisdn;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

}
