package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Класс, представляющий сущность абонента.
 * Абонент идентифицируется по номеру MSISDN.
 */
@Data
@Entity
public class Subscriber {

    /**
     * Номер абонента, используемый как уникальный идентификатор.
     */
    @Id
    private String msisdn;
}
