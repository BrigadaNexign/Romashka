package rom.hrs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rom.hrs.dto.ChangeTariffRequest;
import rom.hrs.dto.CreateTariffRequest;
import rom.hrs.dto.TariffResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.service.TariffService;

@RestController
@RequestMapping("/tariff")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @GetMapping("/{tariffId}")
    @PreAuthorize("hasAuthority('SCOPE_tariff.read')")
    public ResponseEntity<TariffResponse> getTariff(@PathVariable Long tariffId) {
        return ResponseEntity.ok(tariffService.getTariff(tariffId));
    }

    @GetMapping("/by-msisdn/{msisdn}")
    @PreAuthorize("hasAuthority('SCOPE_tariff.read')")
    public ResponseEntity<TariffResponse> getTariffByMsisdn(@PathVariable String msisdn) {
        return ResponseEntity.ok(tariffService.getTariffByMsisdn(msisdn));
    }

    @GetMapping("/by-id/{tariffId}")
    @PreAuthorize("hasAuthority('SCOPE_tariff.read')")
    public ResponseEntity<Tariff> findTariffById(@PathVariable long tariffId) throws NoTariffFoundException {
        return ResponseEntity.ok(tariffService.findTariffById(tariffId));
    }

    @PostMapping("/change/{msisdn}")
    @PreAuthorize("hasAuthority('SCOPE_tariff.update')")
    public ResponseEntity<Void> changeTariff(
            @PathVariable String msisdn,
            @RequestBody @Valid ChangeTariffRequest request) {
        tariffService.changeTariff(msisdn, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_tariff.create')")
    public ResponseEntity<Void> createTariff(@RequestBody @Valid CreateTariffRequest request) {
        tariffService.createTariff(request);
        return ResponseEntity.status(201).build();
    }
}