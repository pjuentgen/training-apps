package com.demo;

import com.demo.entity.ApiCall;
import com.demo.repository.ApiCallRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SpringBootDemoApplicationTests {

    @Autowired
    private ApiCallRepository apiCallRepository;

    @Test
    void contextLoads() {
        assertNotNull(apiCallRepository);
    }

    @Test
    void testApiCallEntity() {
        ApiCall apiCall = new ApiCall("/test", "TEST", "Test message", 100L);
        
        assertNotNull(apiCall.getCreatedAt());
        assertEquals("/test", apiCall.getEndpoint());
        assertEquals("TEST", apiCall.getCallType());
        assertEquals("Test message", apiCall.getResponseMessage());
        assertEquals(100L, apiCall.getExecutionTimeMs());
    }

    @Test
    void testDatabaseSave() {
        ApiCall apiCall = new ApiCall("/test", "TEST", "Test message", 100L);
        ApiCall saved = apiCallRepository.save(apiCall);
        
        assertNotNull(saved.getId());
        assertEquals("/test", saved.getEndpoint());
        
        long count = apiCallRepository.count();
        assertTrue(count > 0);
    }
}