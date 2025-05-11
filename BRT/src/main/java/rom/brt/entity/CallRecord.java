package rom.brt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность для хранения информации о телефонных звонках.
 * Содержит детали вызовов и их стоимость для HRS.
 */
@Entity
@Table(name = "call_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_id")
    private Long callId;

    @Column(name = "call_type", nullable = false, length = 2)
    private String callType;

    @Column(name = "caller_msisdn", nullable = false, length = 11)
    private String callerMsisdn;

    @Column(name = "receiver_msisdn", nullable = false, length = 11)
    private String receiverMsisdn;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}