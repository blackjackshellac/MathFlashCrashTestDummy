package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Created by steeve on 07/09/17.
 */

public class NumberOperation implements Serializable {
    private static final LongPair NUM1 = new LongPair(0, 12);
    private static final LongPair NUM2 = new LongPair(0, 12);

    public final Operation op;
    public final LongPair lp1; // 0 - 12
    public final LongPair lp2;

    private LongPair nums;
    private long ans;

    NumberOperation(Operation op) {
        this(op, NUM1, NUM2);
    }

    NumberOperation(Operation op, LongPair lp1, LongPair lp2) {
        this(op, lp1.l1, lp1.l2, lp2.l1, lp2.l2);
    }

    NumberOperation(Operation op, long num1min, long num1max, long num2min, long num2max) {
        this.op = op;
        this.lp1 = new LongPair(num1min, num1max);
        this.lp2 = new LongPair(num2min, num2max);
    }

	/**
	 * Get a random number pair between lp1 and lp2 (inclusive) and calcuation the result for the operation
	 * @return returns the number pair
	 */
	public LongPair randomize() {
        nums = op.randomize(lp1, lp2);
        ans = op.doit(nums);
        return nums;
    }

    public LongPair nums() {
        return nums;
    }

    boolean isAnswer(long answer) {
        return answer == ans;
    }
}
