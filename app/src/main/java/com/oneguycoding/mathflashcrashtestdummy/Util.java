package com.oneguycoding.mathflashcrashtestdummy;

/**
 * Class for java utils
 *
 * Created by steeve on 13/10/17.
 */

@SuppressWarnings("unused")
class Util {
	@SuppressWarnings("unused")
	static String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
}
