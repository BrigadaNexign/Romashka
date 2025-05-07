package rom.brt.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Fragment {
    /**
     * Тип вызова:
     * - "01" — исходящий вызов,
     * - "02" — входящий вызов.
     */
    @CsvBindByName(column = "call_type", required = true)
    private String callType;

    @CsvBindByName(column = "caller_msisdn", required = true)
    private String callerMsisdn;

    @CsvBindByName(column = "receiver_msisdn", required = true)
    private String receiverMsisdn;

    @CsvBindByName(column = "start_time", required = true)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @CsvBindByName(column = "end_time", required = true)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    public void validate() {
        if (!"01".equals(callType) && !"02".equals(callType)) {
            throw new IllegalArgumentException("Invalid call type: " + callType);
        }

        if (callerMsisdn == null || callerMsisdn.trim().isEmpty()) {
            throw new IllegalArgumentException("Caller MSISDN is empty");
        }

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time is after end time");
        }
    }
}