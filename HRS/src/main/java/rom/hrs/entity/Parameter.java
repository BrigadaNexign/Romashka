package rom.hrs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "params")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    @Id
    @Column(name = "param_id")
    private Long id;

    @Column(name = "param_name", nullable = false, unique = true)
    private String name;

    @Column(name = "param_desc")
    private String description;

    @Column(name = "units", length = 50)
    private String units;

}
