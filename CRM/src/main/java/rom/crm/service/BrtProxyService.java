package rom.crm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.crm.controller.BrtClient;
import rom.crm.dto.request.BalanceUpdate;
import rom.crm.dto.request.UserUpdateRequest;
import rom.crm.dto.response.UserResponse;

@Service
@RequiredArgsConstructor
public class BrtProxyService {
    private final BrtClient brtClient;

    public UserResponse getUserByMsisdn(String msisdn, String jwt) {
        return brtClient.getUserByMsisdn(msisdn)
                .getBody();
    }

    public void updateBalance(String msisdn, BalanceUpdate request, String jwt) {
        brtClient.updateBalance(msisdn, request, "Bearer " + jwt);
    }

    public void createUser(UserUpdateRequest request, String jwt) {
        brtClient.createUser(request);
    }
}