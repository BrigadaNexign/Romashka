package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность CDR.
 * CDR содержит информацию о звонке, включая тип вызова, номера абонентов и временные метки.
 */
@Data
@Entity
public class CDR {

    /**
     * Уникальный идентификатор записи CDR.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип вызова:
     * - "01" — исходящий вызов,
     * - "02" — входящий вызов.
     */
    private String callType;

    /**
     * Номер абонента, инициировавшего звонок.
     */
    private String callerMsisdn;

    /**
     * Номер абонента, принимающего звонок.
     */
    private String receiverMsisdn;

    /**
     * Время начала звонка.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * Время окончания звонка.
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

}
