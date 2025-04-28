package rom.brt.dto;

import java.time.LocalDate;


public record CalculationResponse(
        Double cost,
        String tariffType,
        String description,
        Integer remainingMinutes,
        LocalDate nextPaymentDate
) {
    public static CalculationResponse forPackageMinutes(int remaining, String description) {
        return new CalculationResponse(null, "PACKAGE", description, remaining, null);
    }

    public static CalculationResponse forPerMinute(double cost, String description) {
        return new CalculationResponse(cost, "PER_MINUTE", description, null, null);
    }

    public static CalculationResponse forMonthlyFee(double cost, LocalDate nextPayment) {
        return new CalculationResponse(cost, "MONTHLY", "Monthly fee charged", null, nextPayment);
    }
}
