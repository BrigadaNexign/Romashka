package rom.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rom.brt.client.HRSClient;
import rom.brt.dto.*;
import rom.brt.entity.User;
import rom.brt.exceptions.BusinessException;
import rom.brt.exceptions.CsvParsingException;
import rom.brt.exceptions.FailedResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

    @Mock
    private HRSClient hrsClient;

    @Mock
    private UserService userService;

    @Mock
    private FragmentMapper fragmentMapper;

    @Mock
    private RequestBuilder requestBuilder;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private MessageHandler messageHandler;

    @Test
    void handleMessage_shouldLogErrorWhenExceptionOccurs() throws CsvParsingException {
        String csvMessage = "invalid,csv,data";
        when(fragmentMapper.parseCsv(csvMessage)).thenThrow(new RuntimeException("Parsing error"));

        messageHandler.handleMessage(csvMessage);

        verify(fragmentMapper).parseCsv(csvMessage);
        verifyNoMoreInteractions(userService, requestBuilder, hrsClient, responseHandler);
    }

    @Test
    void processCall_shouldSkipNonServicedCaller() throws BusinessException {
        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");

        User nonServicedCaller = new User();
        nonServicedCaller.setUserId(-1);

        when(userService.findUser("79001112233")).thenReturn(nonServicedCaller);

        messageHandler.processCall(fragment);

        verify(userService).findUser("79001112233");
    }

    @Test
    void processCall_shouldProcessServicedCallerAndServicedReceiver() throws BusinessException {
        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");
        fragment.setCallType("01");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(5));

        User callerRecord = new User();
        callerRecord.setUserId(1);
        callerRecord.setMsisdn("79001112233");
        callerRecord.setTariffId(1);

        User receiverRecord = new User();
        receiverRecord.setUserId(2);
        receiverRecord.setMsisdn("79002223344");
        receiverRecord.setTariffId(2);

        Subscriber caller = Subscriber.fromServicedUser(1, "79001112233", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromServicedUser(2, "79002223344", 2, 200, LocalDate.now());

        CalculationRequest request = new CalculationRequest(
                "01", caller, receiver, 5, LocalDate.now());
        CalculationResponse response = new CalculationResponse(
                true, 10.0, "11", "Standard tariff", 95, LocalDate.now().plusMonths(1), null, null);

        when(userService.findUser("79001112233")).thenReturn(callerRecord);
        when(userService.findUser("79002223344")).thenReturn(receiverRecord);
        when(userService.createServicedSubscriberFromRecord(callerRecord)).thenReturn(caller);
        when(userService.createServicedSubscriberFromRecord(receiverRecord)).thenReturn(receiver);
        when(requestBuilder.build(fragment, caller, receiver)).thenReturn(request);
        when(hrsClient.calculateCost(request)).thenReturn(response);

        messageHandler.processCall(fragment);

        verify(userService).findUser("79001112233");
        verify(userService).findUser("79002223344");
        verify(requestBuilder).build(fragment, caller, receiver);
        verify(hrsClient).calculateCost(request);
        verify(responseHandler).handleCalculationResponse(callerRecord, fragment, response);
    }

    @Test
    void processCall_shouldProcessServicedCallerAndForeignReceiver() throws BusinessException {
        Fragment fragment = new Fragment();
        fragment.setCallerMsisdn("79001112233");
        fragment.setReceiverMsisdn("79002223344");
        fragment.setCallType("01");
        fragment.setStartTime(LocalDateTime.now());
        fragment.setEndTime(LocalDateTime.now().plusMinutes(5));

        User callerRecord = new User();
        callerRecord.setUserId(1);
        callerRecord.setMsisdn("79001112233");
        callerRecord.setTariffId(1);

        User foreignReceiverRecord = new User();
        foreignReceiverRecord.setUserId(-1);
        foreignReceiverRecord.setMsisdn("79002223344");

        Subscriber caller = Subscriber.fromServicedUser(1, "79001112233", 1, 100, LocalDate.now());
        Subscriber receiver = Subscriber.fromForeignUser("79002223344");

        CalculationRequest request = new CalculationRequest(
                "01", caller, receiver, 5, LocalDate.now());
        CalculationResponse response = new CalculationResponse(
                true, 15.0, "11", "Standard tariff", 95, LocalDate.now().plusMonths(1), null, null);

        when(userService.findUser("79001112233")).thenReturn(callerRecord);
        when(userService.findUser("79002223344")).thenReturn(foreignReceiverRecord);
        when(userService.createServicedSubscriberFromRecord(callerRecord)).thenReturn(caller);
        when(userService.createForeignSubscriberFromRecord(foreignReceiverRecord)).thenReturn(receiver);
        when(requestBuilder.build(fragment, caller, receiver)).thenReturn(request);
        when(hrsClient.calculateCost(request)).thenReturn(response);

        messageHandler.processCall(fragment);

        verify(userService).findUser("79001112233");
        verify(userService).findUser("79002223344");
        verify(requestBuilder).build(fragment, caller, receiver);
        verify(hrsClient).calculateCost(request);
        verify(responseHandler).handleCalculationResponse(callerRecord, fragment, response);
    }
}