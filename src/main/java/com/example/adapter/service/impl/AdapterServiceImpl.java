package com.example.adapter.service.impl;

import com.example.adapter.config.AdapterConfig;
import com.example.adapter.dao.FineRequest;
import com.example.adapter.dao.FineResponse;
import com.example.adapter.exception.SMEVException;
import com.example.adapter.service.AdapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdapterServiceImpl implements AdapterService {

    private final AdapterConfig adapterConfig;

    private final WebClient webClient;


    public List<FineResponse> requestFineFromSMEV(FineRequest fineRequest) {
            FineRequest request = requestFine(fineRequest).block();
            List<FineResponse> fineResponse = getResult(request).block();
            sendAcknowledge(fineResponse).block();
            return fineResponse;
    }

    public Mono<FineRequest> requestFine(FineRequest fineRequest) {
        return webClient.post()
                .uri(adapterConfig.getFineRequest())
                .body(BodyInserters.fromValue(fineRequest))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new SMEVException("Server is not responding")))
                .bodyToMono(FineRequest.class);
    }

    public Mono<List<FineResponse>> getResult(FineRequest fineRequest) {
        return webClient.post()
                .uri(adapterConfig.getFineResult())
                .body(BodyInserters.fromValue(fineRequest))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(new ParameterizedTypeReference<List<FineResponse>>() {})
                .retryWhen(Retry.fixedDelay(adapterConfig.getRetryCount(), Duration.ofSeconds(3)));
    }

    public Mono<ResponseEntity> sendAcknowledge(List<FineResponse> fineResponse) {
        return webClient.post()
                .uri(adapterConfig.getFineAcknowledge())
                .body(BodyInserters.fromValue(fineResponse))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(ResponseEntity.class);
    }
}
