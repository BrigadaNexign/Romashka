package rom.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rom.hrs.dto.TariffParamResponse;
import rom.hrs.entity.Parameter;
import rom.hrs.entity.TariffParameter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByName(String name);
}

