package rom.hrs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.hrs.dto.TariffParamResponse;
import rom.hrs.repository.TariffParameterRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с параметрами тарифов.
 */
@Service
@RequiredArgsConstructor
public class TariffParamService {
    private final TariffParameterRepository tariffParameterRepository;
    private final ParameterService parameterService;

    public List<TariffParamResponse> findTariffResponseByTariffId(Long tariffId) {
        return tariffParameterRepository.findByTariff_Id(tariffId).stream()
                .map(tariffParameter -> {
                    try {
                        return TariffParamResponse.builder()
                                .name(parameterService.findByParamId(tariffParameter.getId().getParamId()).getName())
                                .description(parameterService.findByParamId(tariffParameter.getId().getParamId()).getDescription())
                                .value(tariffParameter.getValue().doubleValue())
                                .units(parameterService.findByParamId(tariffParameter.getId().getParamId()).getUnits())
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
