package rom.cdr.repository;

import org.springframework.stereotype.Repository;
import rom.cdr.entity.Fragment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с записями Fragment.
 * Предоставляет методы для выполнения операций с базой данных, связанных с Fragment.
 *
 * @see Fragment
 */
@Repository
public interface FragmentRepository extends JpaRepository<Fragment, Long> {

    /**
     * Удаляет запись Fragment по её идентификатору.
     *
     * @param CDRId идентификатор записи Fragment
     */
    void deleteById(Long CDRId);

    /**
     * Находит все записи Fragment, связанные с указанным номером абонента (как вызывающего, так и принимающего).
     *
     * @param callerMsisdn номер вызывающего абонента
     * @param receiverMsisdn номер принимающего абонента
     * @return список записей Fragment
     */
    List<Fragment> findByCallerMsisdnOrReceiverMsisdn(String callerMsisdn, String receiverMsisdn);

    /**
     * Сохраняет все переданные записи Fragment.
     *
     * @param entities список записей Fragment для сохранения
     * @param <S> тип записи Fragment
     * @return список сохраненных записей Fragment
     */
    @Override
    <S extends Fragment> List<S> saveAll(Iterable<S> entities);

    /**
     * Находит все записи Fragment, связанные с указанным номером абонента (как вызывающего, так и принимающего),
     * и ограниченные временным интервалом.
     *
     * @param msisdn номер абонента
     * @param startTime начало временного интервала
     * @param endTime конец временного интервала
     * @return список записей Fragment, соответствующих критериям
     */
    @Query("SELECT c FROM Fragment c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime BETWEEN :startTime AND :endTime")
    List<Fragment> findByCallerMsisdnOrReceiverMsisdnAndStartTimeBetween(
            @Param("msisdn") String msisdn,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Fragment c WHERE " +
            "((c.callerMsisdn = :callerMsisdn OR c.receiverMsisdn = :receiverMsisdn) " +
            "OR (c.callerMsisdn = :receiverMsisdn OR c.receiverMsisdn = :callerMsisdn)) " +
            "AND ((c.startTime BETWEEN :startTime AND :endTime) OR " +
            "(c.endTime BETWEEN :startTime AND :endTime) OR " +
            "(c.startTime <= :startTime AND c.endTime >= :endTime))")
    boolean existsConflictingCalls(
            @Param("callerMsisdn") String callerMsisdn,
            @Param("receiverMsisdn") String receiverMsisdn,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
