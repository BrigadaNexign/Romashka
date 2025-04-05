package org.example.service.CDR;

import org.example.entity.CDR;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с CDR.
 * Предоставляет методы для сохранения, поиска и удаления записей CDR.
 */
public interface CDRService {

    /**
     * Сохраняет запись CDR в базе данных.
     *
     * @param cdr запись CDR для сохранения
     * @return сохраненная запись CDR
     */
    CDR saveCDR(CDR cdr);

    /**
     * Возвращает список всех записей CDR.
     *
     * @return список всех записей CDR
     */
    List<CDR> fetchCDRList();

    /**
     * Удаляет запись CDR по её идентификатору.
     *
     * @param CDRId идентификатор записи CDR
     */
    void deleteCDRByID(Long CDRId);

    /**
     * Возвращает список записей CDR, связанных с указанными номерами абонентов.
     *
     * @param callerMsisdn   номер вызывающего абонента
     * @param receiverMsisdn номер принимающего абонента
     * @return список записей CDR
     */
    List<CDR> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn);

    /**
     * Инициализирует данные, необходимые для работы сервиса.
     */
    void initializeData();

    /**
     * Сохраняет все переданные записи CDR.
     *
     * @param entities список записей CDR для сохранения
     * @param <S>      тип записи CDR
     * @return список сохраненных записей CDR
     */
    <S extends CDR> List<S> saveAllCDRs(Iterable<S> entities);

    /**
     * Возвращает список записей CDR, связанных с указанным номером абонента и ограниченных временным интервалом.
     *
     * @param msisdn      номер абонента
     * @param startOfMonth начало временного интервала
     * @param endOfMonth   конец временного интервала
     * @return список записей CDR, соответствующих критериям
     */
    @Query("SELECT c FROM CDR c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime BETWEEN :startTime AND :endTime")
    List<CDR> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
