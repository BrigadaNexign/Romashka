package org.example.service.fragment;

import org.example.entity.Fragment;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с Fragment.
 * Предоставляет методы для сохранения, поиска и удаления записей Fragment.
 */
public interface FragmentService {

    /**
     * Сохраняет запись Fragment в базе данных.
     *
     * @param fragment запись Fragment для сохранения
     * @return сохраненная запись Fragment
     */
    Fragment saveCDR(Fragment fragment);

    /**
     * Возвращает список всех записей Fragment.
     *
     * @return список всех записей Fragment
     */
    List<Fragment> fetchCDRList();

    /**
     * Удаляет запись Fragment по её идентификатору.
     *
     * @param CDRId идентификатор записи Fragment
     */
    void deleteCDRByID(Long CDRId);

    /**
     * Возвращает список записей Fragment, связанных с указанными номерами абонентов.
     *
     * @param callerMsisdn   номер вызывающего абонента
     * @param receiverMsisdn номер принимающего абонента
     * @return список записей Fragment
     */
    List<Fragment> fetchCDRListByMsisdn(String callerMsisdn, String receiverMsisdn);

    /**
     * Инициализирует данные, необходимые для работы сервиса.
     */
    void initializeData();

    /**
     * Сохраняет все переданные записи Fragment.
     *
     * @param entities список записей Fragment для сохранения
     * @param <S>      тип записи Fragment
     * @return список сохраненных записей Fragment
     */
    <S extends Fragment> List<S> saveAllCDRs(Iterable<S> entities);

    /**
     * Возвращает список записей Fragment, связанных с указанным номером абонента и ограниченных временным интервалом.
     *
     * @param msisdn      номер абонента
     * @param startOfMonth начало временного интервала
     * @param endOfMonth   конец временного интервала
     * @return список записей Fragment, соответствующих критериям
     */
    @Query("SELECT c FROM Fragment c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime BETWEEN :startTime AND :endTime")
    List<Fragment> fetchCDRListByMsisdnAndTime(String msisdn, LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
