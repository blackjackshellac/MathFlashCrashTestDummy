package com.oneguycoding.mathflashcrashtestdummy;

import android.util.SparseIntArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by steeve on 13/09/17.
 *
 */

public class UserResults implements Serializable {
	public static final int DEFAULT_NUM = 50;
	private int num;
	private int nCorrect;
	private int nWrong;
	private Map<Operation, Stack<LongPair>> retryMap;

	private static final SparseIntArray limitMap;
	private static final SparseIntArray MAX_PERCENTAGE;

	static {
		limitMap = new SparseIntArray();
		limitMap.put(0, 0);
		limitMap.put(1, 0);
		limitMap.put(10, 0);
		limitMap.put(11, 0);

		MAX_PERCENTAGE = new SparseIntArray();
		MAX_PERCENTAGE.put(0, 2);
		MAX_PERCENTAGE.put(1, 2);
		MAX_PERCENTAGE.put(10, 2);
		MAX_PERCENTAGE.put(11, 2);
	}

	/**
	 * Create new UserResults object
	 *
	 * @param num number of operations to test, default is DEFAULT_NUM
	 */
	UserResults(int num) {
		setNum(num);
		resetCounters();
		retryMap = new HashMap<Operation, Stack<LongPair>>();
	}

	private void resetCounters() {
		nCorrect = 0;
		nWrong = 0;
		for (int ikey=0; ikey < 4; ikey++) {
			limitMap.put(limitMap.keyAt(ikey), 0);
		}
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

	private void setNum(int num) {
		this.num = num <=0 ? DEFAULT_NUM : num;
	}

	/**
	 * Reset results with the given number of trials, and the retryMap for all ops
	 *
	 * @param num - if num > 0 reset its value, otherwise keep the current value
	 */
	public void reset(int num) {
		if (num > 0) {
			setNum(num);
		}
		resetCounters();
		for (Operation op : retryMap.keySet()) {
			Stack<LongPair> stack = retryMap.get(op);
			stack.empty();
		}
	}

	public int getNum() {
		return num;
	}

	public float getPercentage() {
		int na = getNumAnswered();
		return ((float)nCorrect / na) * 100;
	}

	public int getNumAnswered() {
		return nCorrect+nWrong;
	}

	public int getRemaining() {
		return num-getNumAnswered();
	}

	public boolean testDone() {
		return getNumAnswered() >= num;
	}

	private boolean isLimited(int n, int maxPercentage) {
		float percent = (float) (n*100.0/num);
		return percent >= maxPercentage;
	}

	private boolean limitValue(int n, LongPair numberPair) {
		if (numberPair.l1 == n || numberPair.l2 == n) {
			int max = MAX_PERCENTAGE.get(n);
			int val = limitMap.get(n);
			if (isLimited(val, max)) {
				return true;
			}
			limitMap.put(n, val+1);
		}
		return false;
	}

	public boolean limitZerosAndOnes(LongPair numberPair) {
		for (int i=0; i <= 1; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}

	public boolean limitElevensAndTens(LongPair numberPair) {
		for (int i=10; i <= 11; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}
}
