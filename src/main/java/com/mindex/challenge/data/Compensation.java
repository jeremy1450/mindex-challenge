package com.mindex.challenge.data;

public class Compensation {

	public Compensation() {
		
	}
	
	private Employee employee;
	private Double salary;
	private String effectiveDate; //what object? String? Long (epoch)?
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
}
