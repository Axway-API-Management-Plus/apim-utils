package com.axway.apim.utils.model;

public class Subscriber {
	String appName;
	String name;
	String loginName;
	String email;

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "Subscriber [name=" + name + ", loginName=" + loginName + ", email=" + email + "]";
	}
}
