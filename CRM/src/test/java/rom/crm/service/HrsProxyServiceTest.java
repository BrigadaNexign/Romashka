package rom.crm.service;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rom.crm.client.HrsClient;
import rom.crm.dto.request.CallPriceDto;
import rom.crm.dto.request.CreateTariffRequest;
import rom.crm.dto.request.TariffParamDto;
import rom.crm.dto.response.CallPriceResponse;
import rom.crm.dto.response.TariffParamResponse;
import rom.crm.dto.response.TariffResponse;
import rom.crm.entity.TariffType;
import rom.crm.exception.ExternalServiceException;
import rom.crm.exception.TariffNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrsProxyServiceTest {

    @Mock
    private HrsClient hrsClient;

    @InjectMocks
    private HrsProxyService hrsProxyService;

    private TariffResponse tariffResponse1;
    private TariffResponse tariffResponse2;
    private CreateTariffRequest createTariffRequest;

    @BeforeEach
    void setUp() {
        tariffResponse1 = new TariffResponse(
                1L,
                "Interval Tariff",
                "Description",
                14,
                50.0,
                TariffType.INTERVAL.getId(),
                List.of(
                        new CallPriceResponse(1, 10.0),
                        new CallPriceResponse(2, 10.0),
                        new CallPriceResponse(3, 10.0),
                        new CallPriceResponse(4, 10.0)
                ),
                List.of(new TariffParamResponse(
                        "minutes",
                        "description",
                        100.0,
                        "min"
                ))
        );

        tariffResponse2 = new TariffResponse(
                2L,
                "Interval Tariff",
                "Description",
                30,
                100.0,
                TariffType.INTERVAL.getId(),
                List.of(
                        new CallPriceResponse(1, 10.0),
                        new CallPriceResponse(2, 10.0),
                        new CallPriceResponse(3, 10.0),
                        new CallPriceResponse(4, 10.0)
                ),
                List.of(new TariffParamResponse(
                        "minutes",
                        "description",
                        100.0,
                        "min"
                ))
        );
        createTariffRequest = new CreateTariffRequest(
                "New Tariff",
                "Description",
                60,
                300.0,
                List.of(
                        new CallPriceDto(1, 10.0),
                        new CallPriceDto(2, 10.0),
                        new CallPriceDto(3, 10.0),
                        new CallPriceDto(4, 10.0)
                ),
                List.of(
                        new TariffParamDto(
                                "minutes",
                                "description",
                                60.0,
                                "mins"
                        )
                )
        );
    }

    @Test
    void getAllTariffs_returnListOfTariffs() throws ExternalServiceException {
        List<TariffResponse> expectedTariffs = Arrays.asList(tariffResponse1, tariffResponse2);
        when(hrsClient.getAllTariffs(anyString()))
                .thenReturn(ResponseEntity.ok(expectedTariffs));

        List<TariffResponse> result = hrsProxyService.getAllTariffs("name");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedTariffs, result);
        verify(hrsClient).getAllTariffs("name");
    }

    @Test
    void getAllTariffs_returnEmptyList() throws ExternalServiceException {
        when(hrsClient.getAllTariffs(anyString()))
                .thenReturn(ResponseEntity.ok(null));

        List<TariffResponse> result = hrsProxyService.getAllTariffs("name");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTariffs_throwExternalServiceException() {
        when(hrsClient.getAllTariffs(anyString()))
                .thenThrow(FeignException.class);

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.getAllTariffs("name");
        });
    }

    @Test
    void getTariffById_returnTariff() throws TariffNotFoundException, ExternalServiceException {
        when(hrsClient.getTariffById(anyLong()))
                .thenReturn(ResponseEntity.ok(tariffResponse1));

        TariffResponse result = hrsProxyService.getTariffById(1L);

        assertNotNull(result);
        assertEquals(tariffResponse1, result);
        verify(hrsClient).getTariffById(1L);
    }

    @Test
    void getTariffById_throwTariffNotFound() {
        when(hrsClient.getTariffById(anyLong()))
                .thenThrow(FeignException.NotFound.class);

        assertThrows(TariffNotFoundException.class, () -> {
            hrsProxyService.getTariffById(1L);
        });
    }

    @Test
    void getTariffById_throwExternalServiceException() {
        when(hrsClient.getTariffById(anyLong()))
                .thenThrow(FeignException.InternalServerError.class);

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.getTariffById(1L);
        });
    }

    @Test
    void getTariffById_throwTariffNotFoundExceptionNull() {
        when(hrsClient.getTariffById(anyLong()))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(TariffNotFoundException.class, () -> {
            hrsProxyService.getTariffById(1L);
        });
    }

    @Test
    void createTariff_createTariff() throws ExternalServiceException {
        when(hrsClient.createTariff(any(CreateTariffRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        hrsProxyService.createTariff(createTariffRequest);

        verify(hrsClient).createTariff(createTariffRequest);
    }

    @Test
    void createTariff_throwExternalServiceException() {
        when(hrsClient.createTariff(any(CreateTariffRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.createTariff(createTariffRequest);
        });
    }

    @Test
    void createTariff_throwExternalServiceException_whenException() {
        when(hrsClient.createTariff(any(CreateTariffRequest.class)))
                .thenThrow(FeignException.class);

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.createTariff(createTariffRequest);
        });
    }

    @Test
    void deleteTariff_deleteTariff() throws ExternalServiceException, TariffNotFoundException {
        when(hrsClient.deleteTariff(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        hrsProxyService.deleteTariff(1L);

        verify(hrsClient).deleteTariff(1L);
    }

    @Test
    void deleteTariff_throwTariffNotFoundException() {
        when(hrsClient.deleteTariff(anyLong()))
                .thenThrow(FeignException.NotFound.class);

        assertThrows(TariffNotFoundException.class, () -> {
            hrsProxyService.deleteTariff(1L);
        });
    }

    @Test
    void deleteTariff_throwExternalServiceException() {
        when(hrsClient.deleteTariff(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.deleteTariff(1L);
        });
    }

    @Test
    void deleteTariff_throwExternalServiceException_feignException() {
        when(hrsClient.deleteTariff(anyLong()))
                .thenThrow(FeignException.InternalServerError.class);

        assertThrows(ExternalServiceException.class, () -> {
            hrsProxyService.deleteTariff(1L);
        });
    }
}