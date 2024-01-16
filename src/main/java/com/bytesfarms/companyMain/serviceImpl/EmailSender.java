package com.bytesfarms.companyMain.serviceImpl;



import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mortbay.log.Log;
import org.springframework.stereotype.Component;

import com.bytesfarms.companyMain.util.IMSConstants;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

 
@Component
public class EmailSender {
	
 
	
 
	public void sendEmail(String recipient, String template, String subject, HashMap<String, String> map)
			throws AddressException, MessagingException {
		Properties props = new Properties();
		 
		props.put(IMSConstants.SMTP_HOST, 	IMSConstants.SMTP_MAIL_HOST);
		props.put(IMSConstants.SMTP_PORT, IMSConstants.SMTP_MAIL_PORT);
		props.put(IMSConstants.SMTP_AUTH, IMSConstants.TRUE);
		props.put(IMSConstants.SMTP_STARTTLS_ENABLE, IMSConstants.TRUE);
		//props.put("mail.smtp.ssl.trust", IMSConstants.SMTP_MAIL_HOST); 
		
		Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
			protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
				return new jakarta.mail.PasswordAuthentication(IMSConstants.MAIL_USERNAME, IMSConstants.MAIL_PASSWORD);
			}
		});
		
 
		VelocityContext context = new VelocityContext();
		for (String key : map.keySet()) {
			context.put(key, map.get(key));
		}
 
		String renderedBody = renderer(template, context);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(IMSConstants.MAIL_USERNAME));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");
		
		
		//Log.info("This is final template content : : "+ renderedBody);
		Transport.send(message);
	}

	private String renderer(String template, VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();
 
		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", template);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
 
		return null;
	}
 
}
