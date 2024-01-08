package com.bytesfarms.companyMain.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.repository.PayrollRepository;
import com.bytesfarms.companyMain.service.PayrollService;

@Service
public class PayrollServiceImpl implements PayrollService {

	@Autowired
	private PayrollRepository payrollRepository;

	@Override
	public List<Payroll> generateAllPayrollData() {
		return payrollRepository.findAll();
	}

}
