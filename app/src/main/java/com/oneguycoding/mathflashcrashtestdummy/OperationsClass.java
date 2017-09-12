package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by steeve on 11/09/17.
 */

public class OperationsClass implements Serializable {
	private ArrayList<Operation> opList;
	private boolean allowMultiple;
	private int curIndex = 0;

	OperationsClass() {
		allowMultiple = false;
		opList = new ArrayList<Operation>();
		opList.add(Operation.PLUS);
	}

	public void add(Operation op) {
		if (!allowMultiple) {
			opList.clear();
		}
		opList.add(op);
	}

	public Operation getFirst() {
		return opList.isEmpty() ? Operation.PLUS : opList.get(0);
	}

	public boolean isSet(Operation op) {
		return opList.contains(op);
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean b) {
		allowMultiple = b;
	}

	public ArrayList<Operation> getOps() {
		return opList;
	}

	public void setOperation(Operation op, boolean checked) {
		if (checked) {
			add(op);
		} else {
			if (isSet(op)) {
				opList.remove(op);
			}
		}
	}

	public Operation getNextOp() {
		int i = 0;
		if (opList.isEmpty()) {
			Log.d("Operations", "opList shouldn't be empty");
			this.add(Operation.PLUS);
		}
		if (allowMultiple) {
			i = curIndex;
			curIndex += 1;
			if (curIndex >= opList.size()) {
				curIndex = 0;
			}
		}
		if (i > opList.size()) {
			throw new RuntimeException("Next op index out of bounds");
		}
		return opList.get(i);
	}
}
