package com.tcube.api.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ForgetPassword {

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private String to;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	private String login_str;

	public String getLogin_str() {
		return login_str;
	}

	public void setLogin_str(String login_str) {
		this.login_str = login_str;
	}

	@Override
	public String toString() {
		return "Model- EmailRequest{" + "to = '" + to + '}';
	}

}
