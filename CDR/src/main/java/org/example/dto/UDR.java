package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс, представляющий UDR — отчет о звонках абонента.
 * Содержит информацию о номере абонента и деталях входящих и исходящих звонков.
 */
@Data
public class UDR {

    /**
     * Номер абонента.
     */
    private String msisdn;

    /**
     * Подробности входящих звонков.
     */
    private CallDetail incomingCall;

    /**
     * Подробности исходящих звонков.
     */
    private CallDetail outcomingCall;

    /**
     * Вложенный класс, представляющий подробности звонков (входящих или исходящих).
     */
    @Data
    @AllArgsConstructor
    public static class CallDetail {
        /**
         * Общая продолжительность звонков в формате "часы:минуты:секунды".
         */
        private String totalTime;
    }
}
