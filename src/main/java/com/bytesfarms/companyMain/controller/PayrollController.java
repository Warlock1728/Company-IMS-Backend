package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.entity.Payroll;
import com.bytesfarms.companyMain.service.PayrollService;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/allData")
    public List<Payroll> getAllPayrollData() {
        return payrollService.generateAllPayrollData();
    }
}