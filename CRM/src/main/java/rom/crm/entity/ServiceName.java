package rom.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceName {
    BRT("BRT"),
    HRS("HRS"),
    CRM("CRM");
    private final String code;
}
