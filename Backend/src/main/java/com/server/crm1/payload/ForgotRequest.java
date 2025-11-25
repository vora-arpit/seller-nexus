package com.server.crm1.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ForgotRequest {
    @NotBlank
	@Email
	private String email;

	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
