package rom.hrs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Entity
@Table(name = "type", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TariffType {

    @Id
    @Column(name = "type_id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Getter
    public enum TypeEnum {
        INTERVAL(1, "interval", "Интервальный тариф"),
        PER_MINUTE(2, "per_minute", "Поминутный тариф"),
        COMBINED(3, "combined", "Комбинированный тариф");

        private final Integer id;
        private final String name;
        private final String description;

        TypeEnum(Integer id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public TariffType toEntity() {
            return new TariffType(id, name, description);
        }

        public static TypeEnum fromId(Integer id) {
            return Arrays.stream(values())
                    .filter(type -> type.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown type id: " + id));
        }

        public static TypeEnum fromName(String name) {
            return Arrays.stream(values())
                    .filter(type -> type.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown type name: " + name));
        }
    }
}
