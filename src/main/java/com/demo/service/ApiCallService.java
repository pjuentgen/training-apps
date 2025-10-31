package com.demo.service;

import com.demo.entity.ApiCall;
import com.demo.repository.ApiCallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ApiCallService {
    
    @Autowired
    private ApiCallRepository apiCallRepository;
    
    private final Random random = new Random();
    
    public ApiCall saveApiCall(String endpoint, String callType, String responseMessage, Long executionTimeMs) {
        ApiCall apiCall = new ApiCall(endpoint, callType, responseMessage, executionTimeMs);
        return apiCallRepository.save(apiCall);
    }
    
    public List<ApiCall> getAllApiCalls() {
        return apiCallRepository.findAllOrderByCreatedAtDesc();
    }
    
    public List<ApiCall> getApiCallsByType(String callType) {
        return apiCallRepository.findByCallType(callType);
    }
    
    public Long getCallCountByType(String callType) {
        return apiCallRepository.countByCallType(callType);
    }
    
    public void simulateSlowOperation() {
        try {
            // Simulate slow database operation (2-5 seconds)
            int delay = 2000 + random.nextInt(3000);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void simulateFastOperation() {
        try {
            // Simulate fast database operation (10-50ms)
            int delay = 10 + random.nextInt(40);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public String generateRandomResponse() {
        String[] responses = {
            "Random data generated successfully",
            "Operation completed with random result",
            "Random processing finished",
            "Generated random output",
            "Random computation complete"
        };
        return responses[random.nextInt(responses.length)];
    }
}