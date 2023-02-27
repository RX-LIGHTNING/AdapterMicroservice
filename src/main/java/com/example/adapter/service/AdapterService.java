package com.example.adapter.service;

import com.example.adapter.dao.FineRequest;
import com.example.adapter.dao.FineResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdapterService {
    List<FineResponse> requestFineFromSMEV(FineRequest fineRequest);

}
