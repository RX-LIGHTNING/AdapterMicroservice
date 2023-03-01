package com.example.adapter.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

// TODO: 10.02.2023 Validation 
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FineRequest {

    private UUID uuid = UUID.randomUUID();
    private String vehicleCertificate;
    private String taxPayerID;

}
