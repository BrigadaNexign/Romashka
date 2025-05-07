package rom.hrs.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.repository.TariffRepository;
import rom.hrs.repository.TariffTypeRepository;

@Service
@RequiredArgsConstructor
public class TariffService {
    private static final Logger logger = LoggerFactory.getLogger(TariffService.class);

    @Autowired
    private TariffRepository tariffRepository;
    @Autowired
    private TariffTypeRepository tariffTypeRepository;

    public Tariff findTariffById(long tariffId) throws NoTariffFoundException {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new NoTariffFoundException(tariffId));
    }
}
