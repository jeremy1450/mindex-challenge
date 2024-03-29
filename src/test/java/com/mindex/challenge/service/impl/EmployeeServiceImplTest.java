package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeIdReportingStructureUrl;
    private String employeeIdCompensationUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeIdReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportingStructure";
        employeeIdCompensationUrl = "http://localhost:" + port + "/employee/{id}/compensation";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
                
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
    
    @Test
    public void testEmployeeReportingStructure() {
       
    	String targetEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f"; //in the static json file (employee_database.json)
    	
    	 // Read checks
        ReportingStructure readReportingStructure = restTemplate
        		.getForEntity(employeeIdReportingStructureUrl, ReportingStructure.class, targetEmployeeId)
        		.getBody();
       
        assertNotNull(readReportingStructure);
        assertNotNull(readReportingStructure.getEmployee());
        assertNotNull(readReportingStructure.getNumberOfReports());

        assertEquals(targetEmployeeId, readReportingStructure.getEmployee().getEmployeeId());
        assertEquals(Integer.valueOf(4), readReportingStructure.getNumberOfReports());
        
        
    }
    
    @Test
    public void testEmployeeCompensation() {
       
    	String targetEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f"; //in the static json file (employee_database.json)
    	String effectiveDate = "3-21-2024";
    	Double salary = 120000.0;
    	
    	 Compensation insertCompensation = new Compensation();
    	 insertCompensation.setEffectiveDate(effectiveDate);
    	 insertCompensation.setSalary(salary);
    	 
    	 // Create checks
    	 Compensation createdCompensation = restTemplate
        		 .postForEntity(employeeIdCompensationUrl, insertCompensation, Compensation.class, targetEmployeeId)
        		 .getBody();

    	 assertNotNull(createdCompensation.getEmployee());
         assertEquals(targetEmployeeId, createdCompensation.getEmployee().getEmployeeId());

    	 // Read checks
        Compensation readCompensation = restTemplate
        		.getForEntity(employeeIdCompensationUrl, Compensation.class, targetEmployeeId)
        		.getBody();
       
        assertNotNull(readCompensation);
        assertNotNull(readCompensation.getEmployee());

        assertEquals(targetEmployeeId, readCompensation.getEmployee().getEmployeeId());
        assertEquals(effectiveDate, readCompensation.getEffectiveDate());
        assertEquals(salary, readCompensation.getSalary());
        
        
    }
    
}
