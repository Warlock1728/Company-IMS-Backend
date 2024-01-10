package com.bytesfarms.companyMain.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.PayrollRepository;
import com.bytesfarms.companyMain.repository.TimeSheetRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.PayrollService;
import com.bytesfarms.companyMain.util.IMSConstants;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.transaction.Transactional;

@Service
public class PayrollServiceImpl implements PayrollService {

	private static final Logger log = LoggerFactory.getLogger(TESTServiceImpl.class);

	@Autowired
	private PayrollRepository payrollRepository;

	@Autowired
	IMSConstants imsConstants;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	TimeSheetRepository timeSheetRepository;

	@Autowired
	UserRepository userRepository;

	private static final String SECRET_KEY = IMSConstants.SECRET_KEY;

	@Override
	public List<Payroll> generateAllPayrollData() {
		return payrollRepository.findAll();
	}

	@Override
	public String calculateSalary(User user, String month) {

		String fixedSalaryString = decrypt(user.getFixedSalary());

		if (fixedSalaryString != null) {

			double fixedSalary = Double.parseDouble(fixedSalaryString);

			List<TimeSheet> timeSheets = timeSheetRepository.findByUserIdAndMonth(user.getId(), month);

			int totalLeavesTaken = (int) timeSheets.stream().filter(TimeSheet::isLeaveDay).count();
			int totalHalfDaysTaken = (int) timeSheets.stream().filter(TimeSheet::isHalfDay).count();

			log.info("This is total leaves taken by : " + user.getUsername() + "-- " + totalLeavesTaken);

			log.info("This is total Half Days taken by : " + user.getUsername() + "-- " + totalHalfDaysTaken);

			double deductionPerDay = fixedSalary / 22;
			double deductionForHalfDays = totalHalfDaysTaken * (deductionPerDay / 2);
			double totalDeductions = (totalLeavesTaken + totalHalfDaysTaken) * deductionPerDay;

			totalDeductions -= deductionForHalfDays;

			double netPay = fixedSalary - totalDeductions;

			Payroll payroll = new Payroll();

			payroll.setGrossSalary(fixedSalaryString);

			payroll.setNetPay(netPay);
			payroll.setDeductions(totalDeductions);
			payroll.setBonus(3000);
			payroll.setUser(user);
			payroll.setMonth(month.toString());

			payrollRepository.save(payroll);

			return "Payroll Created for: " + user.getUsername() + " for the month: " + month;
		} else {
			return "Please ask admin to put his Gross salary first";
		}

	}

	private String decrypt(String fixedSalary) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] derivedKey = deriveKey(SECRET_KEY);
			SecretKey secretKey = new SecretKeySpec(derivedKey, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decodedBytes = Base64.getDecoder().decode(fixedSalary);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private byte[] deriveKey(String secret) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Scheduled(cron = "0 0 0 L * ?") // Execute at midnight on the last day of every month
	@Transactional
	public void SendEmailForPayroll() {
		List<User> usersWithRole2And3 = userRepository.findByRoleIdIn(Arrays.asList(2L, 3L));

		for (User user : usersWithRole2And3) {
			sendPayrollEmail(user);
		}

		log.info("Mails have been sent to everyone regarding Payroll. Thank You");
	}

	private void sendPayrollEmail(User user) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Your Payroll is Available !");
		message.setText("Hello " + user.getUsername()
				+ ", your Payroll is now available for this month. Please visit your dashboard to find the same. Thanks.");

		javaMailSender.send(message);
	}

	@Override
	public List<Payroll> generatePayslips(User user, String month) {
		if (month != null) {

			return payrollRepository.findByUserAndMonth(user, month);
		} else {

			return payrollRepository.findByUser(user);
		}
	}

	@Override
	public byte[] generatePdf(String grossSalary, double netPay, double deductions, double bonus, String month) {
		Document document = new Document();
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			PdfWriter.getInstance(document, baos);
			document.open();
			document.add(new Paragraph("Gross Salary: " + grossSalary));
			document.add(new Paragraph("Net Pay: " + netPay));
			document.add(new Paragraph("Deductions: " + deductions));
			document.add(new Paragraph("Bonus: " + bonus));
			document.add(new Paragraph("Month: " + month));
			document.close();
			
			log.info("Generating payroll pdf : ");
			
			return baos.toByteArray();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
