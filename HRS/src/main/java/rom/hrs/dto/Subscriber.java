package rom.hrs.dto;

import java.time.LocalDate;

public record Subscriber(
        Integer id,
        String msisdn,
        boolean isServiced,
        Long tariffId,
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

    public static Subscriber fromServicedUser(int id, String msisdn, long tariffId, int minutes, LocalDate paymentDay) {
        return new Subscriber(id, msisdn, true, tariffId, minutes, paymentDay);
    }

    public static Subscriber fromForeignUser(String msisdn) {
        return new Subscriber(null, msisdn, false, null, null, null);
    }
}