package com.oneguycoding.mathflashcrashtestdummy;

import java.util.HashMap;

/**
 * Record LongPair values by count
 *
 * Created by steeve on 12/10/17.
 */

class LongPairRecorder {
	private final HashMap<Operation, HashMap<LongPair,Integer>> recorder;

	LongPairRecorder() {
		recorder = new HashMap<>();
		// create a recorder map for all values
		for (Operation op: Operation.values()) {
			recorder.put(op, new HashMap<LongPair, Integer>());
		}
	}

	private HashMap<LongPair, Integer> getRecord(Operation op) {
		HashMap<LongPair, Integer> record = recorder.get(op);
		assert record != null;
		return record;
	}

	boolean isRecorded(Operation op, LongPair longPair) {
		HashMap<LongPair, Integer> record = getRecord(op);
		return record.containsKey(longPair);
	}

	/**
	 * Clear map for given operation
	 *
	 * @param op - specified operation
	 */
	private void clear(Operation op) {
		HashMap<LongPair, Integer> record = getRecord(op);
		record.clear();
	}

	/**
	 * Clear maps for all operations
	 */
	void clear() {
		for (Operation op : Operation.values()) {
			clear(op);
		}
	}

	void record(Operation op, LongPair numberPair) {
		HashMap<LongPair, Integer> record = getRecord(op);
		Integer count = record.containsKey(numberPair) ? record.get(numberPair) : 0;
		getRecord(op).put(numberPair, count+1);
	}
}
