package com.demo.repository;

import com.demo.entity.ApiCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {
    
    List<ApiCall> findByCallType(String callType);
    
    @Query("SELECT a FROM ApiCall a ORDER BY a.createdAt DESC")
    List<ApiCall> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(a) FROM ApiCall a WHERE a.callType = ?1")
    Long countByCallType(String callType);
}