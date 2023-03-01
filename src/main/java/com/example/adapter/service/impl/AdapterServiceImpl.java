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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdapterServiceImpl implements AdapterService {

    private final AdapterConfig adapterConfig;

    private final WebClient webClient;


    public List<FineResponse> requestFineFromSMEV(FineRequest request) {
        requestFine(request).block();
        List<FineResponse> fineResponse = getResult(request.getUuid()).block();
        sendAcknowledge(request.getUuid()).block();
        return fineResponse;
    }

    public Mono<HttpStatus> requestFine(FineRequest fineRequest) {
        return webClient.post()
                .uri(adapterConfig.getFineRequest())
                .body(BodyInserters.fromValue(fineRequest))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new SMEVException("Server is not responding")))
                .bodyToMono(HttpStatus.class);
    }

    public Mono<List<FineResponse>> getResult(UUID uuid) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(adapterConfig.getFineResult())
                        .build(uuid))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(new ParameterizedTypeReference<List<FineResponse>>() {})
                .retryWhen(Retry.fixedDelay(adapterConfig.getRetryCount(), Duration.ofSeconds(3)));
    }

    public Mono<HttpStatus> sendAcknowledge(UUID uuid) {
       return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(adapterConfig.getFineAcknowledge())
                        .build(uuid))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(HttpStatus.class);
    }
}
