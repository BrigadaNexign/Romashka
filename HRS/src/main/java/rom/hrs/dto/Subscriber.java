package rom.hrs.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {
    private Integer id;
    private String msisdn;
    private boolean isServiced;
    private Integer tariffId;
    private Integer minutes;
    private LocalDate paymentDay;
}