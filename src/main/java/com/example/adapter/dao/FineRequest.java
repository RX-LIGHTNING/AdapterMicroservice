package com.example.adapter.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FineRequest {

    private UUID uuid = UUID.randomUUID();
    @Pattern(regexp = "^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2}\\d{2,3}$",
            message = "Incorrect data")
    private String vehicleCertificate;
    @Pattern(regexp = "^[\\d+]{10}$", message = "Incorrect data")
    private String taxPayerID;

}
