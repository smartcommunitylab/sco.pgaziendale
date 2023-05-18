package it.smartcommunitylab.pgazienda.dto;

import java.util.List;

import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;

public class ProfileDTO {

	private Company company;
	private List<UserDTO> mobilityManagers;
	private String name, surname, companyEmail;
	private CompanyLocation location;
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public List<UserDTO> getMobilityManagers() {
		return mobilityManagers;
	}
	public void setMobilityManagers(List<UserDTO> mobilityManagers) {
		this.mobilityManagers = mobilityManagers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getCompanyEmail() {
		return companyEmail;
	}
	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}
	public CompanyLocation getLocation() {
		return location;
	}
	public void setLocation(CompanyLocation location) {
		this.location = location;
	}
	
}
