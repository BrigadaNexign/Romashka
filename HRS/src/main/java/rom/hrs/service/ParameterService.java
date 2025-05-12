package rom.hrs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.hrs.entity.Parameter;
import rom.hrs.repository.ParameterRepository;

import java.util.Optional;

/**
 * Сервис для работы с параметрами.
 */
@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;

    public Parameter findByParamId(Long id) throws Exception {
        Optional<Parameter> parameter = parameterRepository.findById(id);
        if (parameter.isEmpty()) throw new Exception("Parameter not found"); //TODO: exception
        return parameter.get();
    }
}
