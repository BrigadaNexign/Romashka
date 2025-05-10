package rom.brt.repository;

import org.springframework.stereotype.Repository;
import rom.brt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByMsisdn(String msisdn);
    boolean existsByMsisdn(String msisdn);
}
