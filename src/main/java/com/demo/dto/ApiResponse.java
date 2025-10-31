package com.demo.dto;

import java.time.LocalDateTime;

public class ApiResponse {
    private String message;
    private String callType;
    private String endpoint;
    private Long executionTimeMs;
    private LocalDateTime timestamp;
    private boolean success;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    
    public ApiResponse(String message, String callType, String endpoint, Long executionTimeMs) {
        this();
        this.message = message;
        this.callType = callType;
        this.endpoint = endpoint;
        this.executionTimeMs = executionTimeMs;
    }
    
    public ApiResponse(String message, String callType, String endpoint, Long executionTimeMs, boolean success) {
        this(message, callType, endpoint, executionTimeMs);
        this.success = success;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCallType() {
        return callType;
    }
    
    public void setCallType(String callType) {
        this.callType = callType;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}