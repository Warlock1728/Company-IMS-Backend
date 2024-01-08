package com.bytesfarms.companyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.Payroll;

public interface PayrollRepository extends JpaRepository<Payroll, Double> {

}
