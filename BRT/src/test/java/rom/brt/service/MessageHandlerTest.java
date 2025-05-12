package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.brt.client.HRSClient;
import rom.brt.dto.*;
import rom.brt.dto.request.CalculationRequest;
import rom.brt.dto.response.CalculationResponse;
import rom.brt.entity.User;
import rom.brt.exception.BusinessException;
import rom.brt.exception.CsvParsingException;
import rom.brt.exception.FailedResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

    @Mock private HRSClient hrsClient;
    @Mock private UserService userService;
    @Mock private FragmentMapper fragmentMapper;
    @Mock private RequestBuilder requestBuilder;
    @Mock private ResponseHandler responseHandler;
    @Mock private CallRecordService callRecordService;

    @InjectMocks
    private MessageHandler messageHandler;

    @Test
    void handleMessage_shouldProcessMultipleFragments() throws BusinessException {
        String csvMessage = "test,csv,data";
        Fragment fragment1 = createTestFragment("01", "79001112233", "79002223344");
        Fragment fragment2 = createTestFragment("02", "79003334455", "79004445566");

        when(fragmentMapper.parseCsv(csvMessage)).thenReturn(List.of(fragment1, fragment2));
        when(userService.findUser(anyString())).thenReturn(createServicedUser(1));

        messageHandler.handleMessage(csvMessage);

        verify(fragmentMapper).parseCsv(csvMessage);
        verify(userService, times(4)).findUser(anyString());
        verify(responseHandler, times(2)).handleCalculationResponse(any(), any(), any());
    }

    @Test
    void handleMessage_shouldHandleEmptyFragmentList() throws CsvParsingException {
        String csvMessage = "empty,csv,data";
        when(fragmentMapper.parseCsv(csvMessage)).thenReturn(Collections.emptyList());

        messageHandler.handleMessage(csvMessage);

        verify(fragmentMapper).parseCsv(csvMessage);
        verifyNoInteractions(userService, requestBuilder, hrsClient, responseHandler);
    }

    @Test
    void handleMessage_shouldHandleCsvParsingException() throws CsvParsingException {
        String csvMessage = "invalid,csv,data";
        when(fragmentMapper.parseCsv(csvMessage)).thenThrow(new CsvParsingException(new IllegalArgumentException("Error")));

        messageHandler.handleMessage(csvMessage);

        verify(fragmentMapper).parseCsv(csvMessage);
        verifyNoInteractions(userService, requestBuilder, hrsClient, responseHandler);
    }

    // Тесты для processCall

//    @Test
//    void processCall_shouldSkipNonServicedCaller() throws BusinessException {
//        Fragment fragment = createTestFragment("01", "79001112233", "79002223344");
//        User nonServicedCaller = createNonServicedUser();
//
//        when(userService.findUser("79001112233")).thenReturn(nonServicedCaller);
//        assertNotNull(nonServicedCaller, "Caller should not be null");
//
//        messageHandler.processCall(fragment);
//
//        verify(userService).findUser("79001112233");
//        verify(userService, never()).createServicedSubscriberFromRecord(nonServicedCaller);
//        verifyNoInteractions(requestBuilder, hrsClient, responseHandler);
//    }

    @Test
    void processCall_shouldProcessIncomingCall() throws BusinessException {
        Fragment fragment = createTestFragment("02", "79002223333", "79002223344");
        User caller = createServicedUser(1);
        User receiver = createServicedUser(2);

        when(userService.findUser("79002223333")).thenReturn(caller);
        when(userService.findUser("79002223344")).thenReturn(receiver);
        assertNotNull(caller, "Caller should not be null");
        assertNotNull(receiver, "Receiver should not be null");

        mockSuccessfulProcessing(fragment, caller, receiver);

        messageHandler.processCall(fragment);

        verify(responseHandler).handleCalculationResponse(eq(caller), eq(fragment), any());
    }

    @Test
    void processCall_shouldHandleHrsFailure() throws BusinessException {
        // Given
        Fragment fragment = createTestFragment("01", "79001112233", "79002223344");
        User caller = createServicedUser(1);
        User receiver = createServicedUser(2);

        Subscriber callerSub = createServicedSubscriber(1, "79001112233", 1);
        Subscriber receiverSub = createServicedSubscriber(2, "79002223344", 2);

        CalculationRequest request = new CalculationRequest(
                "01", callerSub, receiverSub, 5, LocalDate.now());

        // Настройка моков
        when(userService.findUser("79001112233")).thenReturn(caller);
        when(userService.findUser("79002223344")).thenReturn(receiver);
        when(userService.createServicedSubscriberFromRecord(caller)).thenReturn(callerSub);
        when(userService.createServicedSubscriberFromRecord(receiver)).thenReturn(receiverSub);
        when(requestBuilder.build(fragment, callerSub, receiverSub)).thenReturn(request);

        // Используем willAnswer для генерации исключения
        given(hrsClient.calculateCost(request)).willAnswer(invocation -> {
            throw new FailedResponseException("code", "message");
        });

        // When/Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> messageHandler.processCall(fragment));

        // Проверяем сообщение исключения
        assertTrue(exception.getMessage().contains("message"));
        verifyNoInteractions(responseHandler);
    }

//    @Test
//    void processCall_shouldLogWhenReceiverIsForeign() throws BusinessException {
//        Fragment fragment = createTestFragment("01", "79001112233", "79002223344");
//        User caller = createServicedUser(1);
//        User foreignReceiver = createNonServicedUser();
//
//        when(userService.findUser("79001112233")).thenReturn(caller);
//        when(userService.findUser("79002223344")).thenReturn(foreignReceiver);
//        when(userService.createServicedSubscriberFromRecord(caller))
//                .thenReturn(createServicedSubscriber(1, "79001112233", 1));
//        when(userService.createForeignSubscriberFromRecord(foreignReceiver))
//                .thenReturn(Subscriber.fromForeignUser("79002223344"));
//
//        assertNotNull(caller, "Caller should not be null");
//        assertNotNull(foreignReceiver, "Receiver should not be null");
//
//        mockSuccessfulProcessing(fragment, caller, foreignReceiver);
//
//        messageHandler.processCall(fragment);
//
//        verify(responseHandler).handleCalculationResponse(eq(caller), eq(fragment), any());
//    }

    // Вспомогательные методы

    private Fragment createTestFragment(String callType, String caller, String receiver) {
        Fragment fragment = new Fragment();
        fragment.setCallType(callType);
        fragment.setCallerMsisdn(caller);
        fragment.setReceiverMsisdn(receiver);
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(5));
        return fragment;
    }

    private User createServicedUser(long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setMsisdn("790011" + userId + "1111");
        user.setTariffId(1L);
        return user;
    }

    private User createNonServicedUser() {
        User user = new User();
        user.setUserId(-1L);
        user.setMsisdn("79009998877");
        return user;
    }

    private Subscriber createServicedSubscriber(long id, String msisdn, long tariffId) {
        return Subscriber.fromServicedUser(id, msisdn, tariffId, 100, LocalDate.now());
    }

    private void mockSuccessfulProcessing(Fragment fragment, User caller, User receiver) throws BusinessException {
        Subscriber callerSub = createServicedSubscriber(caller.getUserId(), caller.getMsisdn(), caller.getTariffId());
        Subscriber receiverSub = receiver.getUserId() == -1
                ? Subscriber.fromForeignUser(receiver.getMsisdn())
                : createServicedSubscriber(receiver.getUserId(), receiver.getMsisdn(), receiver.getTariffId());

        when(userService.createServicedSubscriberFromRecord(caller)).thenReturn(callerSub);
        if (receiver.getUserId() == -1) {
            when(userService.createForeignSubscriberFromRecord(receiver)).thenReturn(receiverSub);
        } else {
            when(userService.createServicedSubscriberFromRecord(receiver)).thenReturn(receiverSub);
        }

        assertNotNull(callerSub, "Caller should not be null");
        assertNotNull(receiverSub, "Receiver should not be null");
        assertNotNull(caller, "Caller should not be null");
        assertNotNull(receiver, "Receiver should not be null");

        CalculationRequest request = new CalculationRequest(
                fragment.getCallType(), callerSub, receiverSub, 5, LocalDate.now());
        CalculationResponse response = new CalculationResponse(
                true, 10.0, "11", "Tariff", 95, LocalDate.now().plusMonths(1), null, null);

        when(requestBuilder.build(fragment, callerSub, receiverSub)).thenReturn(request);
        when(hrsClient.calculateCost(request)).thenReturn(response);
    }
}