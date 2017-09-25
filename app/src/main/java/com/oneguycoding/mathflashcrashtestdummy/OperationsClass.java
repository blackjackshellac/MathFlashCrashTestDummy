package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by steeve on 11/09/17.
 */

public class OperationsClass implements Serializable {
	private LinkedHashMap<Operation, NumberOperation> opNumbers;
	private boolean allowMultiple;
	private int curIndex = 0;

	OperationsClass() {
		allowMultiple = false;

		Operation op = Operation.PLUS;
		opNumbers = new LinkedHashMap<>();
		opNumbers.put(op, new NumberOperation(op));
	}

	public void add(Operation op, NumberOperation nop) {
		if (!allowMultiple) {
			opNumbers.clear();
		}
		if (nop == null) {
			nop = new NumberOperation(op);
		}
		opNumbers.put(op, nop);
	}

	public Operation getFirst() {
		Iterator<Map.Entry<Operation, NumberOperation>> it = opNumbers.entrySet().iterator();
		return it.hasNext() ? it.next().getKey() : Operation.PLUS;
	}

	public boolean isSet(Operation op) {
		return opNumbers.containsKey(op);
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean b) {
		allowMultiple = b;
		if (!allowMultiple) {
			Set<Operation> ops = getOps();
			if (ops.size() > 1) {
				// remove all but first op
				Iterator<Operation> it = ops.iterator();
				boolean firstTime = true;
				while (it.hasNext()) {
					Operation op = it.next();
					if (!firstTime) {
						opNumbers.remove(op);
					}
					firstTime = false;

				}
			}

		}
	}

	public Set<Operation> getOps() {
		return opNumbers.keySet();
	}

	public void setOperation(Operation op, boolean checked) {
		if (checked) {
			add(op, null);
		} else {
			if (isSet(op)) {
				// don't remove last op
				if (getOps().size() > 1) {
					opNumbers.remove(op);
				}
			}
		}
	}

	private Operation getNth(int n) {
		Set<Operation> ops = getOps();
		Iterator<Operation> it = ops.iterator();
		int i = 0;
		while (it.hasNext()) {
			Operation op = it.next();
			if (i == n) {
				return op;
			}
			i += 1;
		}
		return null;
	}

	private int incCurIndex() {
		int i = curIndex;
		curIndex += 1;
		if (curIndex > opNumbers.size()) {
			curIndex = 0;
		}
		return i;
	}

	public NumberOperation getNextOp() {
		int i = 0;
		if (opNumbers.isEmpty()) {
			Log.d("Operations", "opList shouldn't be empty");
			this.add(Operation.PLUS, null);
		}
		if (allowMultiple) {
			i = incCurIndex();
		} else {
			if (opNumbers.size() > 1) {
				Log.d("Operations", "opNumbers should only have one entry if allowMultiple is set");
				throw new RuntimeException("Too many entries in opNumbers");
			}
		}
		if (i > opNumbers.size()) {
			throw new RuntimeException("Next op index out of bounds");
		}
		Operation op = getNth(i);
		return opNumbers.get(op);
	}

	public NumberOperation getOp(Operation op) {
		return opNumbers.get(op);
	}

	/**
	 * Update top range for all ops in opNumbers map
	 *
	 * @param min update min or ignore if null
	 * @param max update max or ignore if null
	 */
	public void updateTop(Long min, Long max) {
		Iterator<Map.Entry<Operation, NumberOperation>> it = opNumbers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Operation, NumberOperation> entry = it.next();
			NumberOperation nop = entry.getValue();
			nop.updateTop(min, max);
		}
	}

	/**
	 * Update bottom range for all ops in opNumbers map
	 *
	 * @param min update min or ignore if null
	 * @param max update max or ignore if null
	 */
	public void updateBottom(Long min, Long max) {
		Iterator<Map.Entry<Operation, NumberOperation>> it = opNumbers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Operation, NumberOperation> entry = it.next();
			NumberOperation nop = entry.getValue();
			nop.updateBottom(min, max);
		}
	}

}
