package rom.hrs.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.hrs.entity.TariffType;
import rom.hrs.repository.TariffTypeRepository;

@Service
@RequiredArgsConstructor
public class TariffTypeService {
    private final TariffTypeRepository typeRepository;

    public String getTypeNameById(Long id) {
        return typeRepository.findById(id)
                .map(TariffType::getName)
                .orElseThrow(() -> new EntityNotFoundException("Type not found with id: " + id));
    }
}
