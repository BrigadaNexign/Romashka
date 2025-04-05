package org.example.controller;

import org.example.service.report.ReportGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Контроллер для обработки запросов, связанных с генерацией отчетов.
 * Предоставляет REST API для создания отчетов на основе CDR.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportGenerationService reportService;

    /**
     * Конструктор с внедрением зависимости сервиса генерации отчетов.
     *
     * @param reportService сервис для генерации отчетов
     */
    @Autowired
    public ReportController(ReportGenerationService reportService) {
        this.reportService = reportService;
    }

    /**
     * Генерирует отчет на основе CDR для указанного абонента в заданном временном интервале.
     *
     * @param msisdn    номер абонента
     * @param startDate начало временного интервала
     * @param endDate   конец временного интервала
     * @return ответ с идентификатором запроса или сообщением об ошибке
     */
    @GetMapping("/generate")
    public ResponseEntity<String> generateReport(
            @RequestParam String msisdn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            UUID requestId = reportService.generateCDRReport(msisdn, startDate, endDate);
            return ResponseEntity.ok("Request ID: " + requestId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
