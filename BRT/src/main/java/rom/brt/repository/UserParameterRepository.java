package rom.brt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.brt.entity.UserParams;

@Repository
public interface UserParameterRepository extends JpaRepository<UserParams, Long> {
}
