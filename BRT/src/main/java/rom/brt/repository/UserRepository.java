package rom.brt.repository;

import org.springframework.stereotype.Repository;
import rom.brt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByMsisdn(String msisdn);
}
