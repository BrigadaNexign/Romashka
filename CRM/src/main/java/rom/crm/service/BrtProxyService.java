package rom.crm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rom.crm.controller.BrtClient;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.TariffResponse;
import rom.crm.dto.response.UserResponse;
import rom.crm.entity.TariffType;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BrtProxyService {
    private static final Logger logger = LoggerFactory.getLogger(BrtProxyService.class);
    private final BrtClient brtClient;

    public UserResponse getUserByMsisdn(String msisdn) {
        return brtClient.getUserByMsisdn(msisdn).getBody();
    }

    public void updateBalance(String msisdn, BalanceUpdate request) {
        brtClient.topUpBalance(msisdn, request);
    }

    public void createUserWithTariff(UserUpdateRequest request, TariffResponse tariffResponse) {
        if (tariffResponse.type().equals(TariffType.INTERVAL.getId()) ||
                tariffResponse.type().equals(TariffType.COMBINED.getId())
        ) {
            brtClient.createUser(
                    new UserUpdateRequest(
                            request.name(),
                            request.msisdn(),
                            request.tariffId(),
                            request.balance(),
                            request.minutes() == 0 ? tariffResponse.intervalDays() : request.minutes(),
                            LocalDate.now().plusDays(tariffResponse.intervalDays())
                    )
            );
        } else {
            brtClient.createUser(request);
        }
    }

    public void createUser(UserUpdateRequest request) {
        brtClient.createUser(request);
    }

    public void changeUserTariff(String msisdn, ChangeTariffRequest request) {
        brtClient.changeUserTariff(msisdn, request);
    }
}