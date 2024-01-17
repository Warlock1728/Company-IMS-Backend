package com.bytesfarms.companyMain.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bytesfarms.companyMain.entity.Policy;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.entity.UserProfile;
import com.bytesfarms.companyMain.repository.PolicyRepository;
import com.bytesfarms.companyMain.service.PolicyService;

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
	public String createPolicyPdf(Long id) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Optional<Policy> policyOptional = policyRepository.findById(id);

			if (policyOptional.isPresent()) {
				Policy policy = policyOptional.get();
				String content = policy.getContent();
				String name = policy.getTitle();

				// content = content.trim();

				if (content.isEmpty()) {
					log.warn("Policy content is empty for ID: {}", id);
					return null;
				}

				InputStream htmlInputStream = getClass().getResourceAsStream("/PolicyPDF.html");
				String htmlContent = IOUtils.toString(htmlInputStream, StandardCharsets.UTF_8);

				htmlContent = processThymeleafTemplate(htmlContent, content, name);

				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocumentFromString(htmlContent);
				renderer.layout();
				renderer.createPDF(baos);

				log.info("Generating payroll pdf : ");

				return baos.toString();
			} else {
				log.info("Policy is not present ! ");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private String processThymeleafTemplate(String htmlContent, String content, String name) {
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(new StringTemplateResolver());

		Context context = new Context();
		context.setVariable("POLICYNAME", name);
		context.setVariable("CONTENT", content);

		String processedHtml = templateEngine.process(htmlContent, context);

		// log.info("BRO CHAL NI RAHA YAR" + processedHtml.toString());
		return processedHtml.toString();
	}

	@Override
	public String deletePolicy(Long id) {
		Optional<Policy> policyOptional = policyRepository.findById(id);

		if (policyOptional.isPresent()) {
			Policy policy = policyOptional.get();

			policyRepository.deleteById(id);

			log.info("Deleted Policy: {}", policy.getTitle());

			return policy.getTitle() + " has been deleted";
		} else {

			log.warn("Policy not found for ID: {}", id);

			return "Policy not found for ID: " + id;
		}
	}

}
