package rom.cdr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rom.cdr.service.record.RecordGenerator;

import java.time.LocalDateTime;

/**
 * Контроллер для генерации CDR (Call Detail Records) - записей о звонках.
 * Позволяет генерировать тестовые данные за различные периоды времени.
 */
@RestController
@RequestMapping("/api/cdr")
public class CdrController {
    @Autowired
    private RecordGenerator recordGenerator;
    private LocalDateTime currentDate = LocalDateTime.now().minusYears(1);;

    /**
     * Генерирует CDR записи за указанное количество лет.
     *
     * @param years количество лет для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/year/{years}")
    public String generateForYears(@PathVariable int years) {
        LocalDateTime nextDate = currentDate.plusYears(years);
        recordGenerator.generateForPeriod(
                currentDate,
                nextDate
        );
        currentDate = nextDate;
        return "Started generation for 1 year";
    }

    /**
     * Генерирует CDR записи за указанное количество месяцев.
     *
     * @param months количество месяцев для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/month/{months}")
    public String generateForMonths(@PathVariable int months) {
        LocalDateTime nextDate = currentDate.plusMonths(months);
        recordGenerator.generateForPeriod(
                currentDate,
                nextDate
        );
        currentDate = nextDate;
        return "Started generation for " + months + " month(s)";
    }

    /**
     * Генерирует CDR записи за указанное количество недель.
     *
     * @param weeks количество недель для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/week/{weeks}")
    public String generateForWeeks(@PathVariable int weeks) {
        LocalDateTime nextDate = currentDate.plusWeeks(weeks);
        recordGenerator.generateForPeriod(
                currentDate,
                nextDate
        );
        currentDate = nextDate;
        return "Started generation for " + weeks + " week(s)";
    }

    /**
     * Генерирует CDR записи за указанное количество дней.
     *
     * @param days количество дней для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/day/{days}")
    public String generateForDays(@PathVariable int days) {
        LocalDateTime nextDate = currentDate.plusDays(days);
        recordGenerator.generateForPeriod(
                currentDate,
                nextDate
        );
        currentDate = nextDate;
        return "Started generation for " + days + " day(s)";
    }
}