package com.biznopay.authservice.bdd;


import com.biznopay.authservice.domain.vo.ApiResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ScenarioContext {

    private final Map<String, FilePart> fileParts = new HashMap<>();
    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper objectMapper;
    private ResponseEntity<ApiResponse> response;
    private RestTemplate restTemplate;
    private StringRedisTemplate redisTemplate;
    private JdbcTemplate jdbcTemplate;
    private Object requestData;

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

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }

    public Map<String, FilePart> getFileParts() {
        return fileParts;
    }

    public void addFilePart(String field, String filename, String contentType, byte[] bytes) {
        fileParts.put(field, new FilePart(filename, contentType, bytes));
    }

    public void clearFileParts() {
        fileParts.clear();
        requestData = null;
    }

    public record FilePart(String filename, String contentType, byte[] bytes) {
        public ByteArrayResource toResource() {
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
        }
    }
}