package rom.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TariffType {
    INTERVAL(1, "interval", "Интервальный тариф"),
    PER_MINUTE(2, "per_minute", "Поминутный тариф"),
    COMBINED(3, "combined", "Комбинированный тариф");

    private final Integer id;
    private final String name;
    private final String description;
}

