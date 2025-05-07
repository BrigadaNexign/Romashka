package rom.hrs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {
    @Id
    @Column(name = "tariff_id")
    private Long id;

    @Column(name = "tariff_name", nullable = false, unique = true)
    private String name;

    @Column(name = "tariff_desc")
    private String description;

    @Column(name = "type_id")
    private int type;
}
