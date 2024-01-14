package com.bytesfarms.companyMain.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bytesfarms.companyMain.entity.Policy;
import com.bytesfarms.companyMain.repository.PolicyRepository;
import com.bytesfarms.companyMain.service.PolicyService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PolicyServiceImpl implements PolicyService {

	private static final Logger log = LoggerFactory.getLogger(PolicyServiceImpl.class);

	@Autowired
	private PolicyRepository policyRepository;

	@Override
	public List<Policy> getPolicyById(Long id) {
		if (id == 0) {
			return policyRepository.findAll();
		}

		Optional<Policy> policyOptional = policyRepository.findById(id);
		return policyOptional.map(Collections::singletonList).orElse(Collections.emptyList());
	}

	@Override
	public Policy savePolicy(Policy policy) {
		policy.setUploadDate(LocalDateTime.now());
		return policyRepository.save(policy);
	}

	@Override
	public String updatePolicyContent(Long id, Policy policy) {

		Policy existingPolicy = policyRepository.findById(id).orElse(null);

		if (existingPolicy != null) {
			if (policy.getContent() != null) {
				existingPolicy.setContent(policy.getContent());
			}
			if (policy.getTitle() != null) {
				existingPolicy.setTitle(policy.getTitle());

			}
			policyRepository.save(existingPolicy);
			return "Policy " + existingPolicy.getTitle() + " has been updated";

		}
		return "Policy  hasn't been found";

	}

	@Override
	public byte[] createPolicyPdf(Long id) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Optional<Policy> policyOptional = policyRepository.findById(id);

			if (policyOptional.isPresent()) {
				Policy policy = policyOptional.get();
				String content = policy.getContent();

				Document document = new Document();
				PdfWriter.getInstance(document, baos);
				document.open();

				document.add(new Paragraph(content));

				document.close();

				log.info("Generated PDF for Policy: {}", policy.getTitle());

				return baos.toByteArray();
			} else {
				log.warn("Policy not found for ID: {}", id);
				return null;
			}
		} catch (Exception e) {
			log.error("Error generating PDF for Policy ID: {}", id, e);
			return null;
		}
	}

	private String processPolicyTemplate(String template, String content) {
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(new StringTemplateResolver());

		Context context = new Context();
		context.setVariable("policyContent", content);

		return templateEngine.process(template, context);
	}

}
