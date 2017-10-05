package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Container to store user data
 *
 * Created by steeve on 12/09/17.
 */

class UserData implements Serializable {
	private String name;
	private String email;
	final UserResults results;
	final OperationsClass ops;

	UserData() {
		this("","");
	}

	UserData(String name, String email) {
		this.name = name;
		this.email = email;
		results = new UserResults(0);
		ops = new OperationsClass();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String getEmail() {
		return email.trim();
	}

	void setEmail(String email) {
		this.email = email.trim();
	}
}
