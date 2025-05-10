package rom.crm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rom.crm.controller.HrsClient;
import rom.crm.dto.request.ChangeTariffRequest;
import rom.crm.dto.response.TariffResponse;

@Service
@RequiredArgsConstructor
public class HrsProxyService {
    private final HrsClient hrsClient;

    public TariffResponse getTariff(Long tariffId, String jwt) {
        return hrsClient.getTariff(tariffId)
                .getBody();
    }

    public void changeTariff(String msisdn, ChangeTariffRequest request, String jwt) {
        hrsClient.changeTariff(request);
    }
}