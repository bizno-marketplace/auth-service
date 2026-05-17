package com.biznopay.authservice.bdd;


import com.biznopay.authservice.domain.vo.ApiResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ScenarioScope
public class ScenarioContext {

    private ResponseEntity<ApiResponse> response;
    private RestTemplate restTemplate;

    public ResponseEntity<ApiResponse> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<ApiResponse> response) {
        this.response = response;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}