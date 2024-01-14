package com.bytesfarms.companyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
}