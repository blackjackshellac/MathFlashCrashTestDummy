package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * Data holder for operations selector. This value is stored in each UserData.
 *
 * Created by steeve on 11/09/17.
 */

class OperationsClass implements Serializable {
	public final LinkedHashMap<Operation, NumberOperation> opNumbers;
	private boolean allowMultiple;
	private int curIndex = 0;

	OperationsClass() {
		allowMultiple = false;

		//Operation op = Operation.PLUS;
		opNumbers = new LinkedHashMap<>();
		//NumberOperation nop = new NumberOperation(op);
		//opNumbers.put(op, nop);
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

/*
	public Operation getFirst() {
		Iterator<Map.Entry<Operation, NumberOperation>> it = opNumbers.entrySet().iterator();
		return it.hasNext() ? it.next().getKey() : Operation.PLUS;
	}
*/

	boolean isSet(Operation op) {
		return opNumbers.containsKey(op);
	}

	boolean isAllowMultiple() {
		return allowMultiple;
	}

	void setAllowMultiple(boolean b) {
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

	private Set<Operation> getOps() {
		return opNumbers.keySet();
	}

	void setOperation(Operation op, boolean checked) {
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

	/**
	 * Get the operation with the specified index from opNumbers LinkedHashMap
	 *
	 * @param idx - the index of the operation
	 *
	 * @return the operation with the given index, or opNumbers.get(0
	 */
	private Operation get_idx(int idx) {
		if (idx < 0 || idx >= opNumbers.size()) {
			idx = 0;
		}
		int i=0;
		Set<Operation> keys = opNumbers.keySet();
		for (Operation op : keys) {
			if (i == idx) {
				return op;
			}
			i++;
		}
		throw new RuntimeException("opNumbers is empty");
	}

	private int incCurIndex() {
		int i = curIndex;
		curIndex += 1;
		if (curIndex >= opNumbers.size()) {
			curIndex = 0;
		}
		return i;
	}

	NumberOperation getNextOp() {
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
		Operation op = get_idx(i);
		return opNumbers.get(op);
	}

	NumberOperation getOp(Operation op) {
		return opNumbers.get(op);
	}

	/**
	 * Update top range for all ops in opNumbers map
	 *
	 * @param min update min or ignore if null
	 * @param max update max or ignore if null
	 */
	void updateTop(Long min, Long max) {
		Set<Map.Entry<Operation, NumberOperation>> entries = opNumbers.entrySet();
		for (Map.Entry<Operation, NumberOperation> entry : entries) {
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
	void updateBottom(Long min, Long max) {
		Set<Map.Entry<Operation, NumberOperation>> entries = opNumbers.entrySet();
		for (Map.Entry<Operation, NumberOperation> entry : entries) {
			NumberOperation nop = entry.getValue();
			nop.updateBottom(min, max);
		}
	}

}
