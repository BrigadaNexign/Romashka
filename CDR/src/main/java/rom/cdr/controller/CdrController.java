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

    /**
     * Генерирует CDR записи за указанное количество лет.
     *
     * @param years количество лет для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/year/{years}")
    public String generateForYears(@PathVariable int years) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusYears(years),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + years + " год(а/лет) запущена";
    }

    /**
     * Генерирует CDR записи за указанное количество месяцев.
     *
     * @param months количество месяцев для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/month/{months}")
    public String generateForMonths(@PathVariable int months) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusMonths(months),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + months + " месяц(ев) запущена";
    }

    /**
     * Генерирует CDR записи за указанное количество недель.
     *
     * @param weeks количество недель для генерации данных
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/week/{weeks}")
    public String generateForWeeks(@PathVariable int weeks) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusWeeks(weeks),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + weeks + " недель(ю/и) запущена";
    }

    /**
     * Генерирует CDR записи за пользовательский период времени.
     *
     * @param start начальная дата периода (формат: yyyy-MM-dd'T'HH:mm:ss)
     * @param end конечная дата периода (формат: yyyy-MM-dd'T'HH:mm:ss)
     * @return сообщение о статусе операции
     */
    @GetMapping("/generate/custom")
    public String generateCustom(
            @RequestParam String start,
            @RequestParam String end
    ) {
        recordGenerator.generateForPeriod(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
        return "Генерация CDR за указанный период запущена";
    }
}