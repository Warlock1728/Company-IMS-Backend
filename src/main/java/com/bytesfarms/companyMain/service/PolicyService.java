package com.bytesfarms.companyMain.service;

import java.io.Reader;
import java.util.List;

import com.bytesfarms.companyMain.entity.Policy;

public interface PolicyService {



	List<Policy> getPolicyById(Long id);

	Policy savePolicy(Policy policy);

	String createPolicyPdf(Long id);

	String updatePolicyContent(Long id, Policy policy);

	String deletePolicy(Long id);

}
