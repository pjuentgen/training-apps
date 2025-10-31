package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.entity.ApiCall;
import com.demo.service.ApiCallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    
    @Autowired
    private ApiCallService apiCallService;
    
    private final Random random = new Random();
    
    /**
     * Sets up comprehensive MDC attributes for logging and OTLP export
     */
    private void setupMDC(String operation, String apiType, String endpoint, HttpServletRequest request) {
        // Clear existing MDC to start fresh
        MDC.clear();
        
        String requestId = UUID.randomUUID().toString();
        String timestamp = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        
        // Request context attributes
        MDC.put("request.id", requestId);
        MDC.put("request.method", request != null ? request.getMethod() : "GET");
        MDC.put("request.uri", endpoint);
        MDC.put("request.query_string", request != null ? (request.getQueryString() != null ? request.getQueryString() : "") : "");
        MDC.put("request.remote_addr", request != null ? request.getRemoteAddr() : "unknown");
        MDC.put("request.user_agent", request != null ? (request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "unknown") : "unknown");
        MDC.put("request.content_type", request != null ? (request.getContentType() != null ? request.getContentType() : "application/json") : "application/json");
        
        // API operation attributes
        MDC.put("api.endpoint", endpoint);
        MDC.put("api.type", apiType);
        MDC.put("api.operation", operation);
        MDC.put("api.version", "v1");
        
        // Service context attributes
        MDC.put("service.name", "spring-boot-demo");
        MDC.put("service.version", "1.0.0");
        MDC.put("service.environment", "development");
        MDC.put("service.component", "rest-controller");
        MDC.put("service.layer", "presentation");
        
        // Timing attributes
        MDC.put("operation.start_time", timestamp);
        MDC.put("operation.start_time_ms", String.valueOf(System.currentTimeMillis()));
        
        // Business context attributes
        MDC.put("business.domain", "demo-api");
        MDC.put("business.transaction_type", "api_call");
        
        // Infrastructure attributes
        MDC.put("host.name", System.getProperty("user.name"));
        MDC.put("process.pid", String.valueOf(ProcessHandle.current().pid()));
        MDC.put("thread.name", Thread.currentThread().getName());
        
        logger.debug("MDC setup completed for operation: {}", operation);
    }
    
    /**
     * Adds execution metrics and completion status to MDC
     */
    private void addExecutionMetrics(long startTime, String status, String statusCode, Object result) {
        long executionTime = System.currentTimeMillis() - startTime;
        String endTimestamp = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        
        // Execution metrics
        MDC.put("operation.duration_ms", String.valueOf(executionTime));
        MDC.put("operation.end_time", endTimestamp);
        MDC.put("operation.status", status);
        
        // Response attributes
        MDC.put("response.status", status);
        MDC.put("response.status_code", statusCode);
        MDC.put("response.size_bytes", result != null ? String.valueOf(result.toString().length()) : "0");
        
        // Performance classification
        if (executionTime < 100) {
            MDC.put("performance.category", "fast");
        } else if (executionTime < 1000) {
            MDC.put("performance.category", "normal");
        } else if (executionTime < 5000) {
            MDC.put("performance.category", "slow");
        } else {
            MDC.put("performance.category", "very_slow");
        }
        
        // SLA compliance (assuming 2s SLA)
        MDC.put("sla.compliant", String.valueOf(executionTime < 2000));
        MDC.put("sla.threshold_ms", "2000");
    }
    
    /**
     * Adds error context to MDC
     */
    private void addErrorContext(Exception e, String errorCategory) {
        MDC.put("error.type", e.getClass().getSimpleName());
        MDC.put("error.message", e.getMessage());
        MDC.put("error.category", errorCategory);
        MDC.put("error.stack_trace", e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "");
        MDC.put("error.occurred", "true");
        
        // Determine error severity
        if (e instanceof InterruptedException) {
            MDC.put("error.severity", "medium");
        } else if (e instanceof RuntimeException) {
            MDC.put("error.severity", "high");
        } else {
            MDC.put("error.severity", "low");
        }
    }
    
    @GetMapping("/normal")
    public ResponseEntity<ApiResponse> normalCall(HttpServletRequest request) {
        setupMDC("normal_call", "NORMAL", "/api/normal", request);
        long startTime = System.currentTimeMillis();
        
        // Add business-specific attributes
        MDC.put("business.operation_type", "data_retrieval");
        MDC.put("business.complexity", "low");
        MDC.put("business.expected_duration_ms", "200");
        
        logger.info("Normal API call started");
        
        try {
            // Simulate normal database operation
            int delay = 100 + random.nextInt(200); // 100-300ms
            MDC.put("operation.simulated_delay_ms", String.valueOf(delay));
            Thread.sleep(delay);
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Normal API call executed successfully";
            
            // Save to database
            apiCallService.saveApiCall("/api/normal", "NORMAL", message, executionTime);
            
            ApiResponse response = new ApiResponse(message, "NORMAL", "/api/normal", executionTime);
            addExecutionMetrics(startTime, "success", "200", response);
            
            logger.info("Normal API call completed successfully in {}ms", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long executionTime = System.currentTimeMillis() - startTime;
            
            addErrorContext(e, "interruption");
            addExecutionMetrics(startTime, "error", "500", null);
            
            logger.error("Normal API call interrupted after {}ms", executionTime, e);
            ApiResponse response = new ApiResponse("Normal call interrupted", "NORMAL", "/api/normal", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            addErrorContext(e, "unexpected");
            addExecutionMetrics(startTime, "error", "500", null);
            
            logger.error("Unexpected error in normal API call after {}ms", executionTime, e);
            ApiResponse response = new ApiResponse("Unexpected error occurred", "NORMAL", "/api/normal", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
    
    @GetMapping("/error")
    public ResponseEntity<ApiResponse> errorCall(HttpServletRequest request) {
        setupMDC("error_call", "ERROR", "/api/error", request);
        long startTime = System.currentTimeMillis();
        
        // Add business-specific attributes
        MDC.put("business.operation_type", "error_simulation");
        MDC.put("business.complexity", "high");
        MDC.put("business.expected_outcome", "error");
        MDC.put("business.test_scenario", "failure_path");
        
        logger.info("Error API call started (simulated error scenario)");
        
        try {
            // Simulate some processing before error
            int delay = 50 + random.nextInt(100);
            MDC.put("operation.simulated_delay_ms", String.valueOf(delay));
            Thread.sleep(delay);
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Simulated error occurred during processing";
            
            // Add simulated error context
            MDC.put("error.simulated", "true");
            MDC.put("error.type", "SimulatedBusinessError");
            MDC.put("error.message", message);
            MDC.put("error.category", "business_logic");
            MDC.put("error.severity", "medium");
            
            // Save to database even for errors
            apiCallService.saveApiCall("/api/error", "ERROR", message, executionTime);
            
            addExecutionMetrics(startTime, "expected_error", "500", null);
            
            logger.warn("Simulated error API call completed in {}ms", executionTime);
            ApiResponse response = new ApiResponse(message, "ERROR", "/api/error", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long executionTime = System.currentTimeMillis() - startTime;
            
            addErrorContext(e, "interruption");
            addExecutionMetrics(startTime, "unexpected_error", "500", null);
            
            logger.error("Error API call interrupted after {}ms", executionTime, e);
            ApiResponse response = new ApiResponse("Error call interrupted", "ERROR", "/api/error", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            addErrorContext(e, "unexpected");
            addExecutionMetrics(startTime, "unexpected_error", "500", null);
            
            logger.error("Unexpected error in error API call after {}ms", executionTime, e);
            ApiResponse response = new ApiResponse("Unexpected error occurred", "ERROR", "/api/error", executionTime, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
    
    @GetMapping("/slow")
    public ResponseEntity<ApiResponse> slowCall() {
        logger.info("Slow API call started");
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate slow database operation
            apiCallService.simulateSlowOperation();
            
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Slow API call completed after " + executionTime + "ms";
            
            // Save to database
            apiCallService.saveApiCall("/api/slow", "SLOW", message, executionTime);
            
            if (executionTime > 1000) {
                logger.warn("Slow API call took {}ms - performance issue detected", executionTime);
            } else {
                logger.info("Slow API call completed in {}ms", executionTime);
            }
            
            ApiResponse response = new ApiResponse(message, "SLOW", "/api/slow", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Slow API call failed after {}ms: {}", executionTime, e.getMessage(), e);
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
        logger.info("Random API call started");
        long startTime = System.currentTimeMillis();
        
        try {
            // Random behavior - could be fast, slow, or error
            int behavior = random.nextInt(3);
            logger.debug("Random behavior selected: {}", behavior);
            
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
            
            logger.info("Random API call completed with behavior {} in {}ms", behavior, executionTime);
            ApiResponse response = new ApiResponse(message, "RANDOM", "/api/random", executionTime);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            String message = "Random call failed: " + e.getMessage();
            
            // Save error to database
            apiCallService.saveApiCall("/api/random", "RANDOM", message, executionTime);
            
            logger.error("Random API call failed after {}ms: {}", executionTime, e.getMessage(), e);
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