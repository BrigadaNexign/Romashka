package org.example.repository;

import org.example.entity.CDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с записями CDR.
 * Предоставляет методы для выполнения операций с базой данных, связанных с CDR.
 *
 * @see org.example.entity.CDR
 */
public interface CDRRepository extends JpaRepository<CDR, Long> {

    /**
     * Удаляет запись CDR по её идентификатору.
     *
     * @param CDRId идентификатор записи CDR
     */
    void deleteById(Long CDRId);

    /**
     * Находит все записи CDR, связанные с указанным номером абонента (как вызывающего, так и принимающего).
     *
     * @param callerMsisdn номер вызывающего абонента
     * @param receiverMsisdn номер принимающего абонента
     * @return список записей CDR
     */
    List<CDR> findByCallerMsisdnOrReceiverMsisdn(String callerMsisdn, String receiverMsisdn);

    /**
     * Сохраняет все переданные записи CDR.
     *
     * @param entities список записей CDR для сохранения
     * @param <S> тип записи CDR
     * @return список сохраненных записей CDR
     */
    @Override
    <S extends CDR> List<S> saveAll(Iterable<S> entities);

    /**
     * Находит все записи CDR, связанные с указанным номером абонента (как вызывающего, так и принимающего),
     * и ограниченные временным интервалом.
     *
     * @param msisdn номер абонента
     * @param startTime начало временного интервала
     * @param endTime конец временного интервала
     * @return список записей CDR, соответствующих критериям
     */
    @Query("SELECT c FROM CDR c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime BETWEEN :startTime AND :endTime")
    List<CDR> findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
            @Param("msisdn") String msisdn,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
