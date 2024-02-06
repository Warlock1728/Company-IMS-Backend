package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.User;

public interface PayrollService {

	List<Payroll> generateAllPayrollData(String month);

	String calculateSalary(User user, String month);

	List<Payroll> generatePayslips(User user, String month);

	byte[] generatePdf(String grossSalary, double netPay, double deductions, double bonus, String month, User userId);

}
