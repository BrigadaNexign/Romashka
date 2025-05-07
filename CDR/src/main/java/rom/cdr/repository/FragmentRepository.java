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

    void deleteById(Long CDRId);

    @Override
    <S extends Fragment> List<S> saveAll(Iterable<S> entities);

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
