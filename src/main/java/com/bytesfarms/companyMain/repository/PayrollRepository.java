package com.bytesfarms.companyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.User;

public interface PayrollRepository extends JpaRepository<Payroll, Double> {

	List<Payroll> findByUserAndMonth(User user, String month);

	List<Payroll> findByUser(User user);

}
