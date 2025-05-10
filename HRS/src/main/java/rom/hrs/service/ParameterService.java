package rom.hrs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.hrs.dto.CallPriceDto;
import rom.hrs.dto.TariffParamResponse;
import rom.hrs.entity.Parameter;
import rom.hrs.repository.ParameterRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;

    public Parameter findByName(String name) throws Exception {
        Optional<Parameter> parameter = parameterRepository.findByName(name);
        if (parameter.isEmpty()) throw new Exception("Parameter not found"); //TODO: exception
        return parameter.get();
    }

    public Parameter findByParamId(Integer id) throws Exception {
        Optional<Parameter> parameter = parameterRepository.findById(id);
        if (parameter.isEmpty()) throw new Exception("Parameter not found"); //TODO: exception
        return parameter.get();
    }


    public List<TariffParamResponse> findListOfParamResponseById(Long id) {
        return parameterRepository.findById(id).stream().map(parameter -> TariffParamResponse.builder()
                        .name(parameter.getName())
                        .description(parameter.getDescription())
                        .units(parameter.getUnits())
                        .build())
                .collect(Collectors.toList());
    }
}
