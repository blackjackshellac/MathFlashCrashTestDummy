package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * A container class to hold and Operation and the top and bottom range for the arithmetic number operation
 *
 * Created by steeve on 07/09/17.
 */

class NumberOperation implements Serializable {
    private static final LongPair TOP_RANGE;
    private static final LongPair BOTTOM_RANGE;

	static {
		TOP_RANGE = new LongPair(0, 12);
		BOTTOM_RANGE = new LongPair(0, 12);
	}

    final Operation op;

    private LongPair topRange;      // 0 - 12
    private LongPair bottomRange;   // 0 - 12

    private LongPair numbers;
    private long answer;

    NumberOperation(Operation op) {
        this(op, TOP_RANGE, BOTTOM_RANGE);
    }

    private NumberOperation(Operation op, LongPair topRange, LongPair bottomRange) {
        this(op, topRange.l1, topRange.l2, bottomRange.l1, bottomRange.l2);
    }

    private NumberOperation(Operation op, long num1min, long num1max, long num2min, long num2max) {
        this.op = op;
        this.topRange = new LongPair(num1min, num1max);
        this.bottomRange = new LongPair(num2min, num2max);
    }

	/**
	 * Get a random number pair between topRange and bottomRange (inclusive) and calcuation the result for the operation
	 * @return returns the number pair
	 */
	LongPair randomize() {
        numbers = op.randomize(topRange, bottomRange);
        answer = op.doit(numbers);
        return numbers;
    }

    LongPair getNumbers() {
        return numbers;
    }

    LongPair setNumbers(Long n1, Long n2) {
	    if (n1 == null) {
		    n1 = numbers.l1;
	    }
	    if (n2 == null) {
		    n2 = numbers.l2;
	    }
	    numbers = new LongPair(n1, n2);
	    answer = op.doit(numbers);
	    return numbers;
    }

    boolean isAnswer(long answer) {
        return answer == this.answer;
    }

    LongPair getTopRange() {
        return topRange;
    }

    LongPair getBottomRange() {
        return bottomRange;
    }

    void updateTop(Long min, Long max) {
	    if (min == null && max == null) {
		    topRange = new LongPair(TOP_RANGE);
	    } else {
		    if (min == null) {
			    min = topRange.l1;
		    }
		    if (max == null) {
			    max = topRange.l2;
		    }
		    topRange = new LongPair(min, max);
	    }
    }

    void updateBottom(Long min, Long max) {
	    if (min == null && max == null) {
		    bottomRange = new LongPair(BOTTOM_RANGE);
	    } else {
		    if (min == null) {
			    min = bottomRange.l1;
		    }
		    if (max == null) {
			    max = bottomRange.l2;
		    }
		    bottomRange = new LongPair(min, max);
	    }
    }

	@SuppressWarnings("unused")
	public LongPair sameTestNumbers() {
		return setNumbers(5L, 9L);
	}
}
