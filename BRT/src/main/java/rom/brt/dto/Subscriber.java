package rom.brt.dto;

import java.time.LocalDate;

public record Subscriber(
        Integer id,
        String msisdn,
        boolean isServiced,
        Integer tariffId,
        Integer minutes,
        LocalDate paymentDay
) {
    public Subscriber {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Invalid id. Id must be positive or none");
        }
        if (msisdn.length() != 11) {
            throw new IllegalArgumentException("Invalid msisdn. Msisdn length must be equal 11");
        }
        if (id == null && isServiced) {
            throw new IllegalStateException("Subscriber without id cannot be serviced");
        }
    }
}
