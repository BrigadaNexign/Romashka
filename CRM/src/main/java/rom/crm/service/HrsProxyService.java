package rom.crm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import rom.crm.controller.HrsClient;
import rom.crm.dto.request.CreateTariffRequest;
import rom.crm.dto.response.TariffResponse;
import rom.crm.exception.ExternalServiceException;
import rom.crm.exception.TariffNotFoundException;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HrsProxyService {
    private static final Logger logger = LoggerFactory.getLogger(HrsProxyService.class);
    private final HrsClient hrsClient;

    public List<TariffResponse> getAllTariffs(String sortBy) throws ExternalServiceException {
        try {
            ResponseEntity<List<TariffResponse>> response = hrsClient.getAllTariffs(sortBy);
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (FeignException e) {
            logger.error("Failed to get tariffs from HRS. Sort param: {}. Error: {}", sortBy, e.getMessage());
            throw new ExternalServiceException(e.getMessage());
        }
    }

    public TariffResponse getTariffById(long tariffId) throws TariffNotFoundException, ExternalServiceException {
        try {
            ResponseEntity<TariffResponse> response = hrsClient.getTariffById(tariffId);
            if (response.getBody() == null) {
                throw new TariffNotFoundException("Tariff not found with id: " + tariffId);
            }
            return response.getBody();
        } catch (FeignException.NotFound e) {
            throw new TariffNotFoundException("Tariff not found with id: " + tariffId);
        } catch (FeignException e) {
            logger.error("Failed to get tariff by id: {}. Error: {}", tariffId, e.getMessage());
            throw new ExternalServiceException(e.getMessage());
        }
    }

    public void createTariff(CreateTariffRequest request) throws ExternalServiceException {
        try {
            ResponseEntity<Void> response = hrsClient.createTariff(request);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Failed to create tariff. Status: " + response.getStatusCode());
            }
        } catch (FeignException e) {
            logger.error("Failed to create tariff. Error: {}", e.getMessage());
            throw new ExternalServiceException(e.getMessage());
        }
    }

    public void deleteTariff(long tariffId) throws ExternalServiceException, TariffNotFoundException {
        try {
            ResponseEntity<Void> response = hrsClient.deleteTariff(tariffId);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Failed to delete tariff. Status: " + response.getStatusCode());
            }
        } catch (FeignException.NotFound e) {
            throw new TariffNotFoundException("Tariff not found with id: " + tariffId);
        } catch (FeignException e) {
            logger.error("Failed to delete tariff with id: {}. Error: {}", tariffId, e.getMessage());
            throw new ExternalServiceException(e.getMessage());
        }
    }
}