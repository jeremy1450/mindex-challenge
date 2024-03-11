package com.mindex.challenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
    
    

    @GetMapping("/employee/{id}/reportingStructure")
    public ReportingStructure readEmployeeReportingStructure(@PathVariable String id) {
        LOG.debug("Received employee reporting structure read request for employee id [{}]", id);

        return employeeService.readEmployeeReportingStructure(id);
    }
    
    
    @PostMapping("/employee/{id}/compensation")
    public Compensation createEmployeeCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received employee compensation create request for [{}]", compensation);

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        compensation.setEmployee(employee);
        
        return employeeService.createEmployeeCompensation(compensation);
    }
    @GetMapping("/employee/{id}/compensation")
    public Compensation readEmployeeCompensation(@PathVariable String id) {
        LOG.debug("Received employee compensation read request for employee id [{}]", id);

        return employeeService.readEmployeeCompensation(id);
    }
}
