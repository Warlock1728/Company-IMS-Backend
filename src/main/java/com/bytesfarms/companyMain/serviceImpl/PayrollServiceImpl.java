package com.bytesfarms.companyMain.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.entity.UserProfile;
import com.bytesfarms.companyMain.repository.PayrollRepository;
import com.bytesfarms.companyMain.repository.TimeSheetRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.PayrollService;
import com.bytesfarms.companyMain.util.IMSConstants;

import org.apache.commons.io.IOUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.transaction.Transactional;

@Service
public class PayrollServiceImpl implements PayrollService {

	private static final Logger log = LoggerFactory.getLogger(PayrollServiceImpl.class);

	@Autowired
	private PayrollRepository payrollRepository;

	@Autowired
	IMSConstants imsConstants;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	EmailSender emailSender;

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

			payroll.setLeaveDays(totalLeavesTaken);
			payroll.setHalfDays(totalHalfDaysTaken);

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
	public void SendEmailForPayroll() throws AddressException, MessagingException {
		List<User> usersWithRole2And3 = userRepository.findByRoleIdIn(Arrays.asList(2L, 3L));

		for (User user : usersWithRole2And3) {
			sendPayrollEmail(user);
		}

		log.info("Mails have been sent to everyone regarding Payroll. Thank You");
	}

	private void sendPayrollEmail(User user) throws AddressException, MessagingException {

		HashMap<String, String> map = new HashMap<>();
		String link = "http://localhost:3000/user-dashboard";

		map.put("DASHBOARD", link);
		map.put("userName", user.getUsername());
		

		String subject = "Leave Application Notification";
		String emailTemplate = loadHtmlTemplate("/Payroll-Available.html");

		emailSender.sendEmail(IMSConstants.RECEIPIENT, emailTemplate, subject, map);
	}

	private String loadHtmlTemplate(String templatePath) {
		try (InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
				Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			return scanner.useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new RuntimeException("Error loading HTML template", e);
		}
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
	public byte[] generatePdf(String grossSalary, double netPay, double deductions, double bonus, String month,
			User userId) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			// Load HTML directly
			InputStream htmlInputStream = getClass().getResourceAsStream("/payslip.html");
			String htmlContent = IOUtils.toString(htmlInputStream, StandardCharsets.UTF_8);

			// Load Signatures
			InputStream signatureInputStream = getClass().getResourceAsStream("/CEOsignatures.png");
			byte[] signatureBytes = IOUtils.toByteArray(signatureInputStream);
			String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);

			// Load Logo of Bytesfarms
			InputStream LogoInputStream = getClass().getResourceAsStream("/LOGO.png");
			byte[] logoBytes = IOUtils.toByteArray(LogoInputStream);
			String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);

			User user = userRepository.findById(userId.getId()).orElse(null);

			if (user != null) {
				// Fetch profile details associated with the user
				UserProfile profile = user.getProfile();

				if (profile != null) {
					// Process Thymeleaf template with user and profile details
					htmlContent = processThymeleafTemplate(htmlContent, grossSalary, netPay, deductions, bonus, month,
							user.getUsername(), profile.getDesignation(), signatureBase64, logoBase64);
				}
			}

			// Process Thymeleaf template
			// htmlContent = processThymeleafTemplate(htmlContent, grossSalary, netPay,
			// deductions, bonus, month);

//			log.info("HTML CONTENT : " + htmlContent);

			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(baos);

			log.info("Generating payroll pdf : ");

			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String processThymeleafTemplate(String template, String grossSalary, double netPay, double deductions,
			double bonus, String month, String employeeName, String designation, String signatureBase64,
			String logoBase64) {

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(new StringTemplateResolver());

		Context context = new Context();
		context.setVariable("grossSalary", grossSalary);
		context.setVariable("netPay", netPay);
		context.setVariable("deductions", deductions);
		context.setVariable("bonus", bonus);
		context.setVariable("month", month);
		context.setVariable("Payslip", "PASSED");

		context.setVariable("employeeName", employeeName);
		context.setVariable("designation", designation);
		context.setVariable("year", LocalDateTime.now().getYear());

		context.setVariable("signatureImageSource", "data:image/png;base64," + signatureBase64);

		context.setVariable("LogoImageSource", "data:image/png;base64," + logoBase64);

		String processedHtml = templateEngine.process(template, context);

		// log.info("BRO CHAL NI RAHA YAR" + processedHtml.toString());
		return processedHtml.toString();
	}

}
