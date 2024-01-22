package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.entity.Policy;
import com.bytesfarms.companyMain.service.PolicyService;

@RestController
@RequestMapping("/policy")
public class PolicyController {

	@Autowired
	private PolicyService policyService;

	@GetMapping("/get")
	public List<Policy> getPolicyById(@RequestParam Long id) {
		return policyService.getPolicyById(id);
	}

	// Create A new policy
	@PostMapping("/create/new")
	public Policy savePolicy(@RequestBody Policy policy) {
		return policyService.savePolicy(policy);
	}

	// Update an existing policy	 content
	@PutMapping("/update")
	public String updatePolicyContent(@RequestParam Long id, @RequestBody Policy policy) {
		return policyService.updatePolicyContent(id, policy);
	}

	// Create the pdf against content
	@PostMapping("/create/pdf")
	public String updatePolicyPdf(@RequestParam Long id) {
		return policyService.createPolicyPdf(id);
	}
	
	
	//Delete a policy
	@DeleteMapping("/delete")
	public String deletePolicy(@RequestParam Long id) {
		return policyService.deletePolicy(id);
	}
	
	
}
