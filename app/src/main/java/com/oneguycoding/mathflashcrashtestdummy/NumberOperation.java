package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Created by steeve on 07/09/17.
 */

public class NumberOperation implements Serializable {
    private static final LongPair TOP_RANGE = new LongPair(0, 12);
    private static final LongPair BOTTOM_RANGE = new LongPair(0, 12);

    public final Operation op;

    private LongPair topRange; // 0 - 12
    private LongPair bottomRange; // 0 - 12

    private LongPair nums;
    private long ans;

    NumberOperation(Operation op) {
        this(op, TOP_RANGE, BOTTOM_RANGE);
    }

    NumberOperation(Operation op, LongPair topRange, LongPair bottomRange) {
        this(op, topRange.l1, topRange.l2, bottomRange.l1, bottomRange.l2);
    }

    NumberOperation(Operation op, long num1min, long num1max, long num2min, long num2max) {
        this.op = op;
        this.topRange = new LongPair(num1min, num1max);
        this.bottomRange = new LongPair(num2min, num2max);
    }

	/**
	 * Get a random number pair between topRange and bottomRange (inclusive) and calcuation the result for the operation
	 * @return returns the number pair
	 */
	public LongPair randomize() {
        nums = op.randomize(topRange, bottomRange);
        ans = op.doit(nums);
        return nums;
    }

    public LongPair nums() {
        return nums;
    }

    boolean isAnswer(long answer) {
        return answer == ans;
    }

    public LongPair getTopRange() {
        return topRange;
    }

    public void setTopRange(LongPair topRange) {
        this.topRange = topRange;
    }

    public LongPair getBottomRange() {
        return bottomRange;
    }

    public void setBottomRange(LongPair botRange) {
        this.bottomRange = botRange;
    }

    public void updateTop(Long min, Long max) {
	    if (min == null && max == null) {
		    // no change
		    return;
	    }
	    if (min == null) {
		    min = topRange.l1;
	    }
	    if (max == null) {
		    max = topRange.l2;
	    }
	    topRange = new LongPair(min, max);
    }

    public void updateBottom(Long min, Long max) {
	    if (min == null && max == null) {
		    // no change
		    return;
	    }
	    if (min == null) {
		    min = bottomRange.l1;
	    }
	    if (max == null) {
		    max = bottomRange.l2;
	    }
	    bottomRange = new LongPair(min, max);
    }
}
