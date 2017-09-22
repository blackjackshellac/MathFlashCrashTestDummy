package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Created by steeve on 12/09/17.
 */

class UserData implements Serializable {
	private String name;
	private String email;
	final OperationData operationData;
	final UserResults userResults;
	final OperationsClass ops;

	UserData() {
		this("","");
	}

	UserData(String name, String email) {
		this.name = name;
		this.email = email;
		operationData = new OperationData();
		userResults = new UserResults(0);
		ops = new OperationsClass();
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
