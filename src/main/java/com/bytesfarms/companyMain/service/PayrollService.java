package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.User;

public interface PayrollService {

	List<Payroll> generateAllPayrollData();

}
