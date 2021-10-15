package com.axway.apim.utils.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {
	
	private String id;
	private String name;
	private String description;
	private String email;
	private String phone;
	private String createdBy;
	private List<String> managedBy;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public List<String> getManagedBy() {
		return managedBy;
	}
	public void setManagedBy(List<String> managedBy) {
		this.managedBy = managedBy;
	}
	@Override
	public String toString() {
		return "Application: "+name+" ("+id+")";
	}
}
