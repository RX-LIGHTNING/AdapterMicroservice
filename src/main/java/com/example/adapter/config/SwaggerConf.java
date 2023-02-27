package com.example.adapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConf {

    @Bean
    public OpenAPI swaggerInfo(@Value("${pom.version}") String pom){
        return new OpenAPI().info(new Info()
                .version(pom)
                .title("Adapter Microservice")
                .description("Microservice to get fine from GISMP")
                .contact(new Contact().name("TYXRX")));
    }

}
