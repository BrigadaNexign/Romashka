package rom.hrs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.hrs.dto.*;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.service.TariffService;

import java.util.List;

/**
 * Контроллер для управления тарифами.
 */
@RestController
@RequestMapping("${services.hrs.api.mappings.tariff.base}")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @GetMapping
    public ResponseEntity<List<TariffResponse>> getAllTariffs(
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(tariffService.getAllTariffs(sortBy));
    }

    @GetMapping("${services.hrs.api.mappings.tariff.by-msisdn}")
    public ResponseEntity<TariffResponse> getTariffByMsisdn(@PathVariable String msisdn) {
        return ResponseEntity.ok(tariffService.getTariffByMsisdn(msisdn));
    }

    @GetMapping("${services.hrs.api.mappings.tariff.by-id}")
    public ResponseEntity<TariffResponse> findTariffById(@PathVariable long tariffId) {
        try {
            return ResponseEntity.ok(tariffService.findTariffResponseById(tariffId));
        } catch (NoTariffFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("${services.hrs.api.mappings.tariff.create}")
    public ResponseEntity<TariffResponse> createTariff(@RequestBody @Valid CreateTariffRequest request) {
        return ResponseEntity.ok(tariffService.createTariff(request));
    }

    @PostMapping("${services.hrs.api.mappings.tariff.delete}")
    public ResponseEntity<Void> deleteTariff(@PathVariable long tariffId) {
        tariffService.deleteTariff(tariffId);
        return ResponseEntity.status(200).build();
    }
}