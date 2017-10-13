package com.oneguycoding.mathflashcrashtestdummy;

/**
 * Created by steeve on 13/10/17.
 */

public class Util {
	public static String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
}
