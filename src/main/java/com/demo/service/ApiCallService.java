package com.demo.service;

import com.demo.entity.ApiCall;
import com.demo.repository.ApiCallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ApiCallService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiCallService.class);
    
    @Autowired
    private ApiCallRepository apiCallRepository;
    
    private final Random random = new Random();
    
    public ApiCall saveApiCall(String endpoint, String callType, String responseMessage, Long executionTimeMs) {
        long dbStartTime = System.currentTimeMillis();
        
        // Add comprehensive database operation attributes to MDC
        MDC.put("db.operation", "insert");
        MDC.put("db.operation.type", "create");
        MDC.put("db.table", "api_calls");
        MDC.put("db.system", "h2");
        MDC.put("db.connection_string", "jdbc:h2:mem:testdb");
        MDC.put("db.user", "sa");
        
        // Add business data attributes
        MDC.put("api.call.endpoint", endpoint);
        MDC.put("api.call.type", callType);
        MDC.put("api.call.execution_time_ms", String.valueOf(executionTimeMs));
        MDC.put("api.call.response_message_length", String.valueOf(responseMessage.length()));
        
        // Add service layer context
        MDC.put("service.layer", "business");
        MDC.put("service.component", "api-call-service");
        MDC.put("service.method", "saveApiCall");
        
        logger.debug("Saving API call: endpoint={}, type={}, executionTime={}ms", endpoint, callType, executionTimeMs);
        
        try {
            ApiCall apiCall = new ApiCall(endpoint, callType, responseMessage, executionTimeMs);
            ApiCall savedCall = apiCallRepository.save(apiCall);
            
            long dbExecutionTime = System.currentTimeMillis() - dbStartTime;
            
            // Add success attributes
            MDC.put("db.record.id", String.valueOf(savedCall.getId()));
            MDC.put("db.record.created_at", savedCall.getCreatedAt().toString());
            MDC.put("db.operation.duration_ms", String.valueOf(dbExecutionTime));
            MDC.put("db.operation.status", "success");
            MDC.put("db.record.size_estimate", String.valueOf(calculateRecordSize(savedCall)));
            
            logger.debug("API call saved successfully with ID: {} in {}ms", savedCall.getId(), dbExecutionTime);
            
            return savedCall;
            
        } catch (Exception e) {
            long dbExecutionTime = System.currentTimeMillis() - dbStartTime;
            
            // Add error attributes
            MDC.put("db.operation.duration_ms", String.valueOf(dbExecutionTime));
            MDC.put("db.operation.status", "error");
            MDC.put("db.error.type", e.getClass().getSimpleName());
            MDC.put("db.error.message", e.getMessage());
            
            logger.error("Failed to save API call to database after {}ms", dbExecutionTime, e);
            throw e;
            
        } finally {
            // Clear database-specific MDC attributes
            MDC.remove("db.operation");
            MDC.remove("db.operation.type");
            MDC.remove("db.table");
            MDC.remove("db.system");
            MDC.remove("db.connection_string");
            MDC.remove("db.user");
            MDC.remove("api.call.endpoint");
            MDC.remove("api.call.type");
            MDC.remove("api.call.execution_time_ms");
            MDC.remove("api.call.response_message_length");
            MDC.remove("db.record.id");
            MDC.remove("db.record.created_at");
            MDC.remove("db.operation.duration_ms");
            MDC.remove("db.operation.status");
            MDC.remove("db.record.size_estimate");
            MDC.remove("db.error.type");
            MDC.remove("db.error.message");
        }
    }
    
    /**
     * Estimates the size of a database record in bytes
     */
    private int calculateRecordSize(ApiCall apiCall) {
        return (apiCall.getEndpoint() != null ? apiCall.getEndpoint().length() : 0) +
               (apiCall.getCallType() != null ? apiCall.getCallType().length() : 0) +
               (apiCall.getResponseMessage() != null ? apiCall.getResponseMessage().length() : 0) +
               32; // Approximate overhead for ID, timestamp, etc.
    }
    
    public List<ApiCall> getAllApiCalls() {
        long queryStartTime = System.currentTimeMillis();
        
        // Add query context to MDC
        MDC.put("db.operation", "select");
        MDC.put("db.operation.type", "query_all");
        MDC.put("db.table", "api_calls");
        MDC.put("db.query.type", "findAll");
        MDC.put("service.method", "getAllApiCalls");
        
        try {
            List<ApiCall> results = apiCallRepository.findAllOrderByCreatedAtDesc();
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add query results to MDC
            MDC.put("db.query.result_count", String.valueOf(results.size()));
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "success");
            MDC.put("db.query.estimated_size_bytes", String.valueOf(results.size() * 200)); // Rough estimate
            
            logger.debug("Retrieved {} API calls in {}ms", results.size(), queryExecutionTime);
            
            return results;
            
        } catch (Exception e) {
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add error attributes
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "error");
            MDC.put("db.error.type", e.getClass().getSimpleName());
            MDC.put("db.error.message", e.getMessage());
            
            logger.error("Failed to retrieve API calls after {}ms", queryExecutionTime, e);
            throw e;
            
        } finally {
            // Clean up query MDC attributes
            MDC.remove("db.operation");
            MDC.remove("db.operation.type");
            MDC.remove("db.table");
            MDC.remove("db.query.type");
            MDC.remove("db.query.result_count");
            MDC.remove("db.operation.duration_ms");
            MDC.remove("db.operation.status");
            MDC.remove("db.query.estimated_size_bytes");
            MDC.remove("db.error.type");
            MDC.remove("db.error.message");
        }
    }

    public List<ApiCall> getApiCallsByType(String callType) {
        long queryStartTime = System.currentTimeMillis();
        
        // Add query context to MDC
        MDC.put("db.operation", "select");
        MDC.put("db.operation.type", "query_by_type");
        MDC.put("db.table", "api_calls");
        MDC.put("db.query.type", "findByCallType");
        MDC.put("db.query.filter.call_type", callType);
        MDC.put("service.method", "getApiCallsByType");
        
        try {
            List<ApiCall> results = apiCallRepository.findByCallType(callType);
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add query results to MDC
            MDC.put("db.query.result_count", String.valueOf(results.size()));
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "success");
            
            logger.debug("Retrieved {} API calls of type '{}' in {}ms", results.size(), callType, queryExecutionTime);
            
            return results;
            
        } catch (Exception e) {
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add error attributes
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "error");
            MDC.put("db.error.type", e.getClass().getSimpleName());
            MDC.put("db.error.message", e.getMessage());
            
            logger.error("Failed to retrieve API calls by type '{}' after {}ms", callType, queryExecutionTime, e);
            throw e;
            
        } finally {
            // Clean up query MDC attributes
            MDC.remove("db.operation");
            MDC.remove("db.operation.type");
            MDC.remove("db.table");
            MDC.remove("db.query.type");
            MDC.remove("db.query.filter.call_type");
            MDC.remove("db.query.result_count");
            MDC.remove("db.operation.duration_ms");
            MDC.remove("db.operation.status");
            MDC.remove("db.error.type");
            MDC.remove("db.error.message");
        }
    }

    public Long getCallCountByType(String callType) {
        long queryStartTime = System.currentTimeMillis();
        
        // Add query context to MDC
        MDC.put("db.operation", "count");
        MDC.put("db.operation.type", "count_by_type");
        MDC.put("db.table", "api_calls");
        MDC.put("db.query.type", "countByCallType");
        MDC.put("db.query.filter.call_type", callType);
        MDC.put("service.method", "getCallCountByType");
        
        try {
            Long count = apiCallRepository.countByCallType(callType);
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add query results to MDC
            MDC.put("db.query.result_count", String.valueOf(count));
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "success");
            
            logger.debug("Counted {} API calls of type '{}' in {}ms", count, callType, queryExecutionTime);
            
            return count;
            
        } catch (Exception e) {
            long queryExecutionTime = System.currentTimeMillis() - queryStartTime;
            
            // Add error attributes
            MDC.put("db.operation.duration_ms", String.valueOf(queryExecutionTime));
            MDC.put("db.operation.status", "error");
            MDC.put("db.error.type", e.getClass().getSimpleName());
            MDC.put("db.error.message", e.getMessage());
            
            logger.error("Failed to count API calls by type '{}' after {}ms", callType, queryExecutionTime, e);
            throw e;
            
        } finally {
            // Clean up query MDC attributes
            MDC.remove("db.operation");
            MDC.remove("db.operation.type");
            MDC.remove("db.table");
            MDC.remove("db.query.type");
            MDC.remove("db.query.filter.call_type");
            MDC.remove("db.query.result_count");
            MDC.remove("db.operation.duration_ms");
            MDC.remove("db.operation.status");
            MDC.remove("db.error.type");
            MDC.remove("db.error.message");
        }
    }
    
    public void simulateSlowOperation() {
        // Add simulation context to MDC
        MDC.put("simulation.type", "slow_operation");
        MDC.put("simulation.component", "database");
        
        try {
            // Simulate slow database operation (2-5 seconds)
            int delay = 2000 + random.nextInt(3000);
            
            MDC.put("simulation.delay_ms", String.valueOf(delay));
            MDC.put("simulation.delay_category", "slow");
            MDC.put("simulation.expected_range", "2000-5000ms");
            
            logger.debug("Starting slow operation simulation with {}ms delay", delay);
            Thread.sleep(delay);
            
            MDC.put("simulation.status", "completed");
            logger.debug("Slow operation simulation completed");
            
        } catch (InterruptedException e) {
            MDC.put("simulation.status", "interrupted");
            MDC.put("simulation.error", e.getMessage());
            logger.warn("Slow operation simulation was interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            // Clean up simulation MDC attributes
            MDC.remove("simulation.type");
            MDC.remove("simulation.component");
            MDC.remove("simulation.delay_ms");
            MDC.remove("simulation.delay_category");
            MDC.remove("simulation.expected_range");
            MDC.remove("simulation.status");
            MDC.remove("simulation.error");
        }
    }

    public void simulateFastOperation() {
        // Add simulation context to MDC
        MDC.put("simulation.type", "fast_operation");
        MDC.put("simulation.component", "cache");
        
        try {
            // Simulate fast database operation (10-50ms)
            int delay = 10 + random.nextInt(40);
            
            MDC.put("simulation.delay_ms", String.valueOf(delay));
            MDC.put("simulation.delay_category", "fast");
            MDC.put("simulation.expected_range", "10-50ms");
            
            logger.debug("Starting fast operation simulation with {}ms delay", delay);
            Thread.sleep(delay);
            
            MDC.put("simulation.status", "completed");
            logger.debug("Fast operation simulation completed");
            
        } catch (InterruptedException e) {
            MDC.put("simulation.status", "interrupted");
            MDC.put("simulation.error", e.getMessage());
            logger.warn("Fast operation simulation was interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            // Clean up simulation MDC attributes
            MDC.remove("simulation.type");
            MDC.remove("simulation.component");
            MDC.remove("simulation.delay_ms");
            MDC.remove("simulation.delay_category");
            MDC.remove("simulation.expected_range");
            MDC.remove("simulation.status");
            MDC.remove("simulation.error");
        }
    }    public String generateRandomResponse() {
        // Add response generation context to MDC
        MDC.put("response.generation.type", "random");
        MDC.put("response.generation.component", "response-generator");
        
        try {
            String[] responses = {
                "Random data generated successfully",
                "Operation completed with random result",
                "Random processing finished",
                "Generated random output",
                "Random computation complete"
            };
            
            int selectedIndex = random.nextInt(responses.length);
            String selectedResponse = responses[selectedIndex];
            
            // Add response details to MDC
            MDC.put("response.generation.selected_index", String.valueOf(selectedIndex));
            MDC.put("response.generation.total_options", String.valueOf(responses.length));
            MDC.put("response.generation.response_length", String.valueOf(selectedResponse.length()));
            MDC.put("response.generation.status", "success");
            
            logger.debug("Generated random response: index={}, length={}", selectedIndex, selectedResponse.length());
            
            return selectedResponse;
            
        } finally {
            // Clean up response generation MDC attributes
            MDC.remove("response.generation.type");
            MDC.remove("response.generation.component");
            MDC.remove("response.generation.selected_index");
            MDC.remove("response.generation.total_options");
            MDC.remove("response.generation.response_length");
            MDC.remove("response.generation.status");
        }
    }
}