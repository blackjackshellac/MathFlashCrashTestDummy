package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by steeve on 12/09/17.
 */

class OperationData implements Serializable {
	public final HashMap<Operation, NumberOperation> ops;

	OperationData() {
		ops = new HashMap<Operation,NumberOperation>(4);
		for (Operation op : Operation.values()) {
			ops.put(op, new NumberOperation(op));
		}
	}

	public NumberOperation getOp(Operation op) {
		return (ops.containsKey(op)) ? ops.get(op) : null;
	}
}
