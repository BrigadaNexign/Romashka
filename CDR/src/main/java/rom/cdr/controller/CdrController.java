package rom.cdr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rom.cdr.service.record.RecordGenerator;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cdr")
public class CdrController {
    @Autowired
    private RecordGenerator recordGenerator;

    @GetMapping("/generate/year/{years}")
    public String generateForYears(@PathVariable int years) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusYears(years),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + years + " год(а/лет) запущена";
    }

    @GetMapping("/generate/month/{months}")
    public String generateForMonths(@PathVariable int months) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusMonths(months),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + months + " месяц(ев) запущена";
    }

    @GetMapping("/generate/week/{weeks}")
    public String generateForWeeks(@PathVariable int weeks) {
        recordGenerator.generateForPeriod(
                LocalDateTime.now().minusWeeks(weeks),
                LocalDateTime.now()
        );
        return "Генерация CDR за " + weeks + " недель(ю/и) запущена";
    }

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