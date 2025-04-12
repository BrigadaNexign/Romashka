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

    @Id
    private String msisdn;

    private boolean type;
}
