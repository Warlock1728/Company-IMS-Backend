package com.bytesfarms.companyMain.helper;

import lombok.Data;

@Data
public class PayrollRequest {
	private String grossSalary;
	private double netPay;
	private double deductions;
	private double bonus;
	private String month;

}
