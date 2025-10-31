package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.entity.ApiCall;
import com.demo.service.ApiCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    @Autowired
    private ApiCallService apiCallService;
    
    private final Random random = new Random();
    
    @GetMapping("/normal")
    public ResponseEntity<ApiResponse> normalCall() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate normal database operation
            Thread.sleep(100 + random.nextInt(200)); // 100-300ms
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Normal API call executed successfully";
            
            // Save to database
            apiCallService.saveApiCall("/api/normal", "NORMAL", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "NORMAL", "/api/normal", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long executionTime = System.currentTimeMillis() - startTime;
            ApiResponse response = new ApiResponse("Normal call interrupted", "NORMAL", "/api/normal", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/error")
    public ResponseEntity<ApiResponse> errorCall() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate some processing before error
            Thread.sleep(50 + random.nextInt(100));
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Simulated error occurred during processing";
            
            // Save to database even for errors
            apiCallService.saveApiCall("/api/error", "ERROR", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "ERROR", "/api/error", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long executionTime = System.currentTimeMillis() - startTime;
            ApiResponse response = new ApiResponse("Error call interrupted", "ERROR", "/api/error", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/slow")
    public ResponseEntity<ApiResponse> slowCall() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate slow database operation
            apiCallService.simulateSlowOperation();
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Slow API call completed after " + executionTime + "ms";
            
            // Save to database
            apiCallService.saveApiCall("/api/slow", "SLOW", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "SLOW", "/api/slow", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            ApiResponse response = new ApiResponse("Slow call failed: " + e.getMessage(), "SLOW", "/api/slow", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/fast")
    public ResponseEntity<ApiResponse> fastCall() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate fast database operation
            apiCallService.simulateFastOperation();
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Fast API call completed in " + executionTime + "ms";
            
            // Save to database
            apiCallService.saveApiCall("/api/fast", "FAST", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "FAST", "/api/fast", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            ApiResponse response = new ApiResponse("Fast call failed: " + e.getMessage(), "FAST", "/api/fast", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/random")
    public ResponseEntity<ApiResponse> randomCall() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Random behavior - could be fast, slow, or error
            int behavior = random.nextInt(3);
            
            switch (behavior) {
                case 0: // Fast
                    apiCallService.simulateFastOperation();
                    break;
                case 1: // Slow
                    Thread.sleep(500 + random.nextInt(1000)); // 0.5-1.5 seconds
                    break;
                case 2: // Sometimes error
                    if (random.nextBoolean()) {
                        throw new RuntimeException("Random error occurred");
                    }
                    Thread.sleep(100 + random.nextInt(300));
                    break;
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = apiCallService.generateRandomResponse() + " (behavior: " + behavior + ")";
            
            // Save to database
            apiCallService.saveApiCall("/api/random", "RANDOM", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "RANDOM", "/api/random", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Random call failed: " + e.getMessage();
            
            // Save error to database
            apiCallService.saveApiCall("/api/random", "RANDOM", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "RANDOM", "/api/random", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getStats() {
        long startTime = System.currentTimeMillis();
        
        try {
            List<ApiCall> allCalls = apiCallService.getAllApiCalls();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Save stats call to database
            apiCallService.saveApiCall("/api/stats", "STATS", "Retrieved " + allCalls.size() + " records", executionTime);
            
            return ResponseEntity.ok(java.util.Map.of(
                "totalCalls", allCalls.size(),
                "normalCalls", apiCallService.getCallCountByType("NORMAL"),
                "errorCalls", apiCallService.getCallCountByType("ERROR"),
                "slowCalls", apiCallService.getCallCountByType("SLOW"),
                "fastCalls", apiCallService.getCallCountByType("FAST"),
                "randomCalls", apiCallService.getCallCountByType("RANDOM"),
                "statsCalls", apiCallService.getCallCountByType("STATS"),
                "recentCalls", allCalls.stream().limit(10).toList(),
                "executionTimeMs", executionTime
            ));
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            ApiResponse response = new ApiResponse("Failed to retrieve stats: " + e.getMessage(), "STATS", "/api/stats", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}