package rom.brt.dto;

import java.time.LocalDate;

/**
 * Представляет абонента.
 * Может быть как обслуживаемым (с тарифом), так и внешним (без тарифа).
 */
public record Subscriber(
        Long id,
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

    /**
     * Создает обслуживаемого абонента.
     */
    public static Subscriber fromServicedUser(long id, String msisdn, long tariffId, int minutes, LocalDate paymentDay) {
        return new Subscriber(id, msisdn, true, tariffId, minutes, paymentDay);
    }

    /**
     * Создает внешнего абонента (не обслуживаемого нашей системой).
     */
    public static Subscriber fromForeignUser(String msisdn) {
        return new Subscriber(null, msisdn, false, null, null, null);
    }
}
