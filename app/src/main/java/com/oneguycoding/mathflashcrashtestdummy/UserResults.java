package com.oneguycoding.mathflashcrashtestdummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by steeve on 13/09/17.
 */

public class UserResults {
	int nCorrect;
	int nWrong;
	Map<Operation, Stack<LongPair>> retryMap;

	UserResults() {
		nCorrect = 0;
		nWrong = 0;
		retryMap = new HashMap<Operation, Stack<LongPair>>();
	}

	private void setRetry(Operation op, LongPair nums) {
		Stack<LongPair> retryStack = retryMap.get(op);
		if (retryStack == null) {
			retryStack = new Stack<LongPair>();
			retryMap.put(op, retryStack);
		}
		retryStack.push(nums);
	}

	public void correct() {
		nCorrect += 1;
	}

	public void wrong(Operation op, LongPair nums) {
		nWrong += 1;
		setRetry(op, nums);
	}

	public boolean hasNext(Operation op) {
		return false;
	}

	public LongPair next(Operation op) {
		Stack<LongPair> retryStack = retryMap.get(op);
		return retryStack == null ? null : retryStack.pop();
	}

	public int getnCorrect() {
		return nCorrect;
	}

	public void reset() {
		nWrong = 0;
		nCorrect = 0;
		for (Operation op : retryMap.keySet()) {
			Stack<LongPair> stack = retryMap.get(op);
			stack.empty();
		}
	}
}
