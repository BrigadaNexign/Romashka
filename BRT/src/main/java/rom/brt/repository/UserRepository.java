package rom.brt.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rom.brt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMsisdn(String msisdn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.msisdn = :msisdn")
    boolean existsByMsisdnWithLock(@Param("msisdn") String msisdn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.msisdn = :msisdn")
    Optional<User> findByMsisdnWithLock(@Param("msisdn") String msisdn);


    @Query(value = "INSERT INTO users_saved (user_name, msisdn, tariff_id, balance, registration_date) " +
            "VALUES (:#{#user.userName}, :#{#user.msisdn}, :#{#user.tariffId}, " +
            ":#{#user.balance}, :#{#user.registrationDate}) " +
            "ON CONFLICT (msisdn) DO NOTHING RETURNING user_id",
            nativeQuery = true)
    Optional<Long> insertUser(@Param("user") User user);
    boolean existsByMsisdn(String msisdn);

    @Query(nativeQuery = true, value = """
        INSERT INTO users_saved 
        (user_name, msisdn, tariff_id, balance, registration_date) 
        VALUES (:userName, :msisdn, :tariffId, :balance, :registrationDate)
        ON CONFLICT (msisdn) DO UPDATE SET
            user_name = EXCLUDED.user_name,
            tariff_id = EXCLUDED.tariff_id,
            balance = EXCLUDED.balance
        RETURNING user_id
        """)
    Long upsertUser(
            @Param("userName") String userName,
            @Param("msisdn") String msisdn,
            @Param("tariffId") Long tariffId,
            @Param("balance") BigDecimal balance,
            @Param("registrationDate") LocalDateTime registrationDate);
}

