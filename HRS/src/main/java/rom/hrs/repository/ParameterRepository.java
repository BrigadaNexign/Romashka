package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.entity.Parameter;

import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByName(String name);
}

