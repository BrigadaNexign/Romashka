package org.example.controller;

import org.example.dto.UDR;
import org.example.service.UDR.UDRGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.YearMonth;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class UDRControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UDRGenerationService udrService;

    @InjectMocks
    private UDRController udrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(udrController).build();
    }

    @Test
    void getUDRForSubscriber_UDR_WithMonth() throws Exception {
        String msisdn = "79992221122";
        YearMonth month = YearMonth.of(2025, 2);
        UDR udr = new UDR();
        udr.setMsisdn(msisdn);

        when(udrService.generateUDRForSubscriber(msisdn, month)).thenReturn(udr);

        mockMvc.perform(
                get("/udr/{msisdn}", msisdn)
                        .param("month", month.toString())
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value(msisdn));

        verify(udrService, times(1)).generateUDRForSubscriber(msisdn, month);
    }

    @Test
    void getUDRForSubscriber_UDR_NoMonth() throws Exception {
        String msisdn = "79992221122";
        UDR udr = new UDR();
        udr.setMsisdn(msisdn);

        when(udrService.generateUDRForSubscriber(msisdn)).thenReturn(udr);

        mockMvc.perform(
                get("/udr/{msisdn}", msisdn).accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value(msisdn));

        verify(udrService, times(1)).generateUDRForSubscriber(msisdn);
    }

    @Test
    void getUDRForAllSubscribers_UDRMap() throws Exception {
        YearMonth month = YearMonth.of(2025, 2);
        UDR udr = new UDR();
        udr.setMsisdn("79992221122");

        Map<String, UDR> udrMap = Collections.singletonMap("79992221122", udr);

        when(udrService.generateUDRForAllSubscribers(month)).thenReturn(udrMap);

        mockMvc.perform(get("/udr/monthly")
                        .param("month", month.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.79992221122.msisdn").value("79992221122"));

        verify(udrService, times(1)).generateUDRForAllSubscribers(month);
    }
}
