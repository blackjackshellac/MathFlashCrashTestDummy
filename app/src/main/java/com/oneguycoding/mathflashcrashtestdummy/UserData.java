package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Created by steeve on 12/09/17.
 */

public class UserData implements Serializable {
	private String name;
	private String email;
	public final OperationData operationData;

	UserData() {
		this("","");
	}

	UserData(String name, String email) {
		this.name = name;
		this.email = email;
		operationData = new OperationData();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
