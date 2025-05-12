package rom.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import rom.crm.client.BrtClient;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.CallPriceResponse;
import rom.crm.dto.response.TariffParamResponse;
import rom.crm.dto.response.TariffResponse;
import rom.crm.dto.response.UserResponse;
import rom.crm.entity.TariffType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrtProxyServiceTest {
    @Mock
    private BrtClient brtClient;

    @InjectMocks
    private BrtProxyService brtProxyService;

    private UserUpdateRequest userUpdateRequest;
    private TariffResponse intervalTariff;
    private BalanceUpdate balanceUpdate;
    private ChangeTariffRequest changeTariffRequest;

    @BeforeEach
    void setUp() {
        userUpdateRequest = new UserUpdateRequest(
                "Name",
                "79991234567",
                1L,
                100.0,
                0,
                null
        );

        intervalTariff = new TariffResponse(
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

        intervalTariff = new TariffResponse(
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

        balanceUpdate = new BalanceUpdate("79991234567",50.0);
        changeTariffRequest = new ChangeTariffRequest(2L);
    }

    @Test
    void getUserByMsisdn_returnUserResponse() {
        String msisdn = "79991234567";
        UserResponse expectedResponse = new UserResponse(
                1L,
                "Name",
                msisdn,
                1L,
                100.0,
                30,
                LocalDateTime.now().minusDays(60),
                LocalDate.now().plusDays(7)
        );
        when(brtClient.getUserByMsisdn(msisdn)).thenReturn(ResponseEntity.ok(expectedResponse));

        UserResponse result = brtProxyService.getUserByMsisdn(msisdn);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(brtClient).getUserByMsisdn(msisdn);
    }

    @Test
    void updateBalance_callBrtClient() {
        String msisdn = "79991234567";

        brtProxyService.updateBalance(msisdn, balanceUpdate);

        verify(brtClient).topUpBalance(msisdn, balanceUpdate);
    }

    @Test
    void createUserWithTariff_setIntervalDays_intervalTariff() {
        LocalDate expectedDate = LocalDate.now().plusDays(intervalTariff.intervalDays());

        brtProxyService.createUserWithTariff(userUpdateRequest, intervalTariff);

        verify(brtClient).createUser(argThat(request ->
                request.name().equals(userUpdateRequest.name()) &&
                        request.msisdn().equals(userUpdateRequest.msisdn()) &&
                        request.tariffId().equals(userUpdateRequest.tariffId()) &&
                        request.balance().equals(userUpdateRequest.balance()) &&
                        request.minutes().equals(intervalTariff.intervalDays()) &&
                        request.paymentDay().equals(expectedDate)
        ));
    }

    @Test
    void createUserWithTariff_setIntervalDays_combinedTariff() {
        TariffResponse combinedTariff = new TariffResponse(
                3L,
                "Interval Tariff",
                "Description",
                15,
                50.0,
                TariffType.COMBINED.getId(),
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

        LocalDate expectedDate = LocalDate.now().plusDays(combinedTariff.intervalDays());

        brtProxyService.createUserWithTariff(userUpdateRequest, combinedTariff);

        verify(brtClient).createUser(argThat(request ->
                request.minutes().equals(combinedTariff.intervalDays()) &&
                        request.paymentDay().equals(expectedDate)
        ));
    }

    @Test
    void createUserWithTariff_preserveMinutes() {
        UserUpdateRequest requestWithMinutes = new UserUpdateRequest(
                "Name",
                "79991234567",
                1L,
                100.0,
                15,
                null
        );

        brtProxyService.createUserWithTariff(requestWithMinutes, intervalTariff);

        verify(brtClient).createUser(argThat(request ->
                request.minutes() == 15
        ));
    }

    @Test
    void createUser_callBrtClientUpdate() {
        brtProxyService.createUser(userUpdateRequest);

        verify(brtClient).createUser(userUpdateRequest);
    }

    @Test
    void changeUserTariff_ShouldCallBrtClientTariff() {
        String msisdn = "79991234567";

        brtProxyService.changeUserTariff(msisdn, changeTariffRequest);

        verify(brtClient).changeUserTariff(msisdn, changeTariffRequest);
    }
}