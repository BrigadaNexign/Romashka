package rom.brt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс, представляющий сущность Fragment.
 * Fragment содержит информацию о звонке, включая тип вызова, номера абонентов и временные метки.
 */
@Data
@AllArgsConstructor
public class Fragment {
    /**
     * Тип вызова:
     * - "01" — исходящий вызов,
     * - "02" — входящий вызов.
     */
    private String callType;

    private String callerMsisdn;

    private String receiverMsisdn;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public static Fragment fromString(String fragmentString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String[] parts = fragmentString.split(", ");
        try {
            return new Fragment(
                    parts[0],
                    parts[1],
                    parts[2],
                    LocalDateTime.parse(parts[3], formatter),
                    LocalDateTime.parse(parts[4], formatter)
            );
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
