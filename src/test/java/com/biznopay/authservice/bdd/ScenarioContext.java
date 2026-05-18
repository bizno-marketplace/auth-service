package com.biznopay.authservice.bdd;


import com.biznopay.authservice.domain.vo.ApiResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Component
@ScenarioScope
public class ScenarioContext {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<ApiResponse> response;
    private RestTemplate restTemplate;
    private StringRedisTemplate redisTemplate;
    private JdbcTemplate jdbcTemplate;

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

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String url(String path) {
        return "http://localhost:" + port + path;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}