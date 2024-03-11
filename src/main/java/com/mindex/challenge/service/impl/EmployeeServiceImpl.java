package com.mindex.challenge.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    private final MongoTemplate mongoTemplate;
    
    @Autowired
    public EmployeeServiceImpl(MongoTemplate mongoTemplate){
		this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }
    
    @Override
    public ReportingStructure readEmployeeReportingStructure(String employeeId) {
        LOG.debug("Reading reporting structure for employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }
        
        int totalReporters = 0;
        if(employee.getDirectReports() != null) {
        	totalReporters += employee.getDirectReports().size();
        	totalReporters += getCountsOfReportingEmployees(employee.getDirectReports());
        }
        
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(totalReporters);
        

        return reportingStructure;
    }
    private int getCountsOfReportingEmployees(List<Employee> employees){
    	int count = 0;
    	
    	for(Employee subEmployee: employees) {
    		subEmployee = employeeRepository.findByEmployeeId(subEmployee.getEmployeeId());
    		
    		if(subEmployee.getDirectReports() != null && subEmployee.getDirectReports().size() > 0) {
    			count += subEmployee.getDirectReports().size();
    			count += getCountsOfReportingEmployees(subEmployee.getDirectReports());
    		}
    	}
    	
    	
    	return count;
    }
    
    @Override
    public Compensation createEmployeeCompensation(Compensation employeeCompensation) {
        LOG.debug("Creating compensation for employee [{}]", employeeCompensation);
                
        Query query = new Query(
        		Criteria.where("employeeId").
        		is(employeeCompensation.getEmployee().getEmployeeId()));
        
        Update update = new Update()
        		.set("salary", employeeCompensation.getSalary())
        		.set("effectiveDate", employeeCompensation.getEffectiveDate());
        
        Employee employee = mongoTemplate.findAndModify(query, update, Employee.class);
        
        employeeCompensation.setEmployee(employee);
        
        return employeeCompensation;
    }

    @Override
    public Compensation readEmployeeCompensation(String employeeId) {
        LOG.debug("Reading employee compensation with employee id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }
        
        Compensation compensation = new Compensation();
        compensation.setEmployee(employee);
        compensation.setSalary(employee.getSalary());
        compensation.setEffectiveDate(employee.getEffectiveDate());

        return compensation;
    }
   
}
