package rom.cdr.service.fragment;

import lombok.RequiredArgsConstructor;
import rom.cdr.entity.Fragment;
import org.springframework.stereotype.Service;
import rom.cdr.exception.EmptyFieldException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для создания и валидации фрагментов звонков (CDR записей).
 * Обеспечивает базовую проверку целостности данных перед созданием записей.
 */
@Service
@RequiredArgsConstructor
public class FragmentEditor {

    /**
     * Создает новый фрагмент звонка с базовой валидацией.
     *
     * @param callType тип вызова ("01" - исходящий, "02" - входящий)
     * @param callerMsisdn номер вызывающего абонента
     * @param receiverMsisdn номер принимающего абонента
     * @param startTime время начала звонка
     * @param endTime время окончания звонка
     * @return новый объект Fragment
     * @throws IllegalArgumentException если время окончания раньше времени начала
     */
    public Fragment createFragment(
            String callType,
            String callerMsisdn,
            String receiverMsisdn,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        Fragment fragment = new Fragment();
        fragment.setCallType(callType);
        fragment.setCallerMsisdn(callerMsisdn);
        fragment.setReceiverMsisdn(receiverMsisdn);
        fragment.setStartTime(startTime);
        fragment.setEndTime(endTime);
        return fragment;
    }

    /**
     * Форматирует фрагмент звонка в массив строк для экспорта.
     *
     * @param fragment фрагмент для форматирования
     * @return массив строк в формате [callType, callerMsisdn, receiverMsisdn, startTime, endTime]
     */
    public String[] formatFragment(Fragment fragment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return new String[]{
                fragment.getCallType(),
                fragment.getCallerMsisdn(),
                fragment.getReceiverMsisdn(),
                fragment.getStartTime().format(formatter),
                fragment.getEndTime().format(formatter)
        };
    }

    /**
     * Проверяет фрагмент на наличие обязательных полей.
     *
     * @param fragment фрагмент для проверки
     * @throws EmptyFieldException если отсутствуют обязательные поля
     */
    public void checkFragment(Fragment fragment) throws EmptyFieldException {
        if (fragment == null) {
            throw new EmptyFieldException("Fragment is null");
        }

        if (fragment.getCallType() == null || fragment.getCallType().trim().isEmpty()) {
            throw new EmptyFieldException("callType");
        }
        if (fragment.getCallerMsisdn() == null || fragment.getCallerMsisdn().trim().isEmpty()) {
            throw new EmptyFieldException("callerMsisdn");
        }
        if (fragment.getReceiverMsisdn() == null || fragment.getReceiverMsisdn().trim().isEmpty()) {
            throw new EmptyFieldException("receiverMsisdn");
        }
        if (fragment.getStartTime() == null) {
            throw new EmptyFieldException("startTime");
        }
        if (fragment.getEndTime() == null) {
            throw new EmptyFieldException("endTime");
        }
    }
}
