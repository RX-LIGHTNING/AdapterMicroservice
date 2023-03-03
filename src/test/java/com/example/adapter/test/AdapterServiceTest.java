package com.example.adapter.test;

import com.example.adapter.config.AdapterConfig;
import com.example.adapter.dao.FineRequest;
import com.example.adapter.dao.FineResponse;
import com.example.adapter.service.impl.AdapterServiceImpl;
import com.example.adapter.utils.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;


@SpringBootTest
@AutoConfigureMockMvc
class AdapterServiceTest {
    @Autowired
    private AdapterServiceImpl adapterService;

    @Autowired
    private AdapterConfig adapterConfig;

    @Autowired
    private static MockWebServer mockBackEnd;

    private UUID uuid = UUID.randomUUID();

    @PostConstruct
    void setUp() throws IOException {
        //starting mock server
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        //setting base url of mock web server, as a base url into yaml configuration file
        adapterConfig.setBaseUrl(String.valueOf(mockBackEnd.url("")));
    }

    @AfterAll
    static void tearDown() throws IOException {
        //closing resources
        mockBackEnd.shutdown();
    }

    @Test
    void testFineRequest() throws InterruptedException {
        FineRequest fineRequest = new FineRequest();
        fineRequest.setTaxPayerID("1234567890");
        fineRequest.setUuid(uuid);

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200));

        HttpUrl fineRequestUrl = mockBackEnd.url("/request");
        adapterConfig.setFineRequest(String.valueOf(fineRequestUrl));
        adapterService.requestFine(fineRequest).block();

        RecordedRequest request = mockBackEnd.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/request", request.getPath());
    }

    @Test
    void testFineResult() throws InterruptedException, JsonProcessingException {

        //creating testing data
        List<FineResponse> resultList = new ArrayList<>();

        FineResponse fineResponse = new FineResponse();
        fineResponse.setFineAmount(BigDecimal.valueOf(1));
        fineResponse.setArticle("123");
        fineResponse.setTaxPayerID("123456");

        resultList.add(fineResponse);

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(CONTENT_TYPE, "application/json")
                .setBody(Mapper.mapToJson(resultList)));

        //creating mock path
        HttpUrl fineResultUrl = mockBackEnd.url("/result/" + uuid);
        adapterConfig.setFineResult(String.valueOf(fineResultUrl));
        adapterService.getResult(uuid).block();

        //asserting results
        RecordedRequest request = mockBackEnd.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/result/" + uuid, request.getPath());
    }

    @Test
    void testFineAcknowledge() throws InterruptedException {

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200));

        HttpUrl fineRequestUrl = mockBackEnd.url("/acknowledge/" + uuid);
        adapterConfig.setFineAcknowledge(String.valueOf(fineRequestUrl));
        adapterService.sendAcknowledge(uuid).block();

        RecordedRequest request = mockBackEnd.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals("/acknowledge/" + uuid, request.getPath());

    }
}
