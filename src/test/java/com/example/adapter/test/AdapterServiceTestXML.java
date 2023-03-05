package com.example.adapter.test;

import com.example.adapter.config.AdapterConfig;
import com.example.adapter.dao.FineRequest;
import com.example.adapter.dao.FineResponse;
import com.example.adapter.service.impl.AdapterServiceImpl;
import com.example.adapter.utils.Mapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ExtendWith(MockitoExtension.class)
public class AdapterServiceTestXML {

    @Autowired
    private static MockWebServer mockBackEnd;

    @Autowired
    private MockMvc mockMvc;

    private UUID uuid = UUID.randomUUID();

    private List<FineResponse> resultList = new ArrayList<>();

    @PostConstruct
    void setUp() throws IOException {
        //starting mock server
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(InetAddress.getByName("localhost"),8081);

        //setting up testing resources
        FineResponse fineResponse = new FineResponse();
        fineResponse.setFineAmount(BigDecimal.valueOf(1));
        fineResponse.setArticle("123");
        fineResponse.setTaxPayerID("123456");

        resultList.add(fineResponse);

        //setting up an example responses
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200));

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(CONTENT_TYPE, "application/json")
                .setBody(Mapper.mapToJson(resultList)));

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200));
    }

    @AfterAll
    static void tearDown() throws IOException {
        //closing resources
        mockBackEnd.shutdown();
    }

    @Test
    void testXMLIntegration() throws Exception {

        log.error(String.valueOf(mockBackEnd.url("")));

        FineRequest fineRequest = new FineRequest();
        fineRequest.setTaxPayerID("1234567890");

        mockMvc.perform(get("/api/v1/fine/request")
                        .contentType(APPLICATION_XML)
                        .accept(TEXT_XML)
                        .content(Mapper.mapToXML(fineRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/xml;charset=UTF-8"));
    }

}
