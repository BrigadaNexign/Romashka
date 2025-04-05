package org.example.controller;


import org.example.dto.UDR;
import org.example.service.UDR.UDRGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Map;

/**
 * Контроллер для обработки запросов, связанных с UDR.
 * Предоставляет REST API для получения отчетов о звонках абонентов.
 */
@RestController
@RequestMapping("/udr")
public class UDRController {
    private final UDRGenerationService udrService;

    /**
     * Конструктор с внедрением зависимости сервиса генерации UDR.
     *
     * @param udrService сервис для генерации UDR
     */
    @Autowired
    public UDRController(UDRGenerationService udrService) {
        this.udrService = udrService;
    }

    /**
     * Возвращает UDR для указанного абонента.
     * Если указан месяц, возвращает отчет за этот месяц. Иначе возвращает отчет за всё время.
     *
     * @param msisdn номер абонента
     * @param month  месяц в формате YYYY-MM (опционально)
     * @return объект UDR с деталями звонков абонента
     */
    @GetMapping("/{msisdn}")
    public UDR getUDRForSubscriber(
            @PathVariable String msisdn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth month) {
        if (month != null) {
            return udrService.generateUDRForSubscriber(msisdn, month);
        } else {
            return udrService.generateUDRForSubscriber(msisdn);
        }
    }

    /**
     * Возвращает UDR для всех абонентов за указанный месяц.
     *
     * @param month месяц в формате YYYY-MM
     * @return карта, где ключ — номер абонента, а значение — объект UDR
     */
    @GetMapping("/monthly")
    public Map<String, UDR> getUDRForAllSubscribers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth month) {
        return udrService.generateUDRForAllSubscribers(month);
    }
}
