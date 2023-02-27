package com.example.adapter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@RestControllerAdvice
@Slf4j
public class AdapterExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e){
        log.error("Ooops, something went wrong!"+ e.getClass());
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Object> handleWebClientxception(WebClientRequestException e){
        log.error("Web Client error");
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SMEVException.class)
    public ResponseEntity<Object> handleSMEVException(SMEVException e){
        log.error("Something went wrong with SMEV");
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
