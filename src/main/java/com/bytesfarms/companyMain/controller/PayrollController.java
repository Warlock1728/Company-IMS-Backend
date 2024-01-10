package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.helper.PayrollRequest;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.PayrollService;
import com.bytesfarms.companyMain.util.PdfUtil;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

	@Autowired
	private PayrollService payrollService;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/allData")
	public List<Payroll> getAllPayrollData() {
		return payrollService.generateAllPayrollData();
	}

	@PostMapping("/create")
	public String calculateSalary(@RequestParam Long userId, @RequestParam String month) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

		return payrollService.calculateSalary(user, month);
	}

	@GetMapping("/generatePayslip")
	public ResponseEntity<List<Payroll>> generatePayslip(@RequestParam Long userId,
			@RequestParam(required = false) String month) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

		List<Payroll> payslip = payrollService.generatePayslips(user, month);
		return ResponseEntity.ok(payslip);
	}

	@PostMapping("/generatePdf")
	public ResponseEntity<byte[]> generatePdf(@RequestBody PayrollRequest payrollRequest) {
		byte[] pdfContent = payrollService.generatePdf(payrollRequest.getGrossSalary(), payrollRequest.getNetPay(),
				payrollRequest.getDeductions(), payrollRequest.getBonus(), payrollRequest.getMonth());
		return PdfUtil.createResponse(pdfContent, "payroll.pdf");
	}

}