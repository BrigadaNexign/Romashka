package org.example.service.report;

import org.example.entity.CDR;
import org.example.service.CDR.CDRServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для генерации отчетов на основе CDR.
 * Предоставляет методы для создания отчетов в формате CSV.
 */
@Service
public class ReportGenerationService {

    private final CDRServiceImpl cdrService;

    /**
     * Конструктор с внедрением зависимости сервиса CDR.
     *
     * @param cdrService сервис для работы с CDR
     */
    @Autowired
    public ReportGenerationService(CDRServiceImpl cdrService) {
        this.cdrService = cdrService;
    }

    /**
     * Генерирует отчет CDR для указанного абонента в заданном временном интервале.
     *
     * @param msisdn    номер абонента
     * @param startDate начало временного интервала
     * @param endDate   конец временного интервала
     * @return UUID идентификатор запроса на генерацию отчета
     * @throws IOException            если произошла ошибка при записи файла
     * @throws IllegalArgumentException если не найдено записей CDR для указанного интервала
     */
    public UUID generateCDRReport(String msisdn, LocalDateTime startDate, LocalDateTime endDate)
            throws IOException, IllegalArgumentException {

        List<CDR> cdrs = cdrService.fetchCDRListByMsisdnAndTime(
                msisdn, startDate, endDate);

        if (cdrs.isEmpty()) throw new IllegalArgumentException("No CDR records found");

        UUID requestId = UUID.randomUUID();

        Path reports = Paths.get("reports");
        if (!Files.exists(reports)) {
            Files.createDirectories(reports);
        }

        String fileName = "reports/" + msisdn + "_" + requestId + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (CDR cdr : cdrs) {
                writer.write(cdrToCsvLine(cdr));
                writer.newLine();
            }
        }

        return requestId;
    }

    /**
     * Преобразует запись CDR в строку формата CSV.
     *
     * @param cdr запись CDR
     * @return строка в формате CSV
     */
    private String cdrToCsvLine(CDR cdr) {
        return String.join(",",
                cdr.getCallType(),
                cdr.getCallerMsisdn(),
                cdr.getReceiverMsisdn(),
                cdr.getStartTime().toString(),
                cdr.getEndTime().toString());
    }
}
