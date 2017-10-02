package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 *
 * A simple container class for passing pairs of longs
 *
 * Created by steeve on 07/09/17.
 */

class LongPair implements Serializable {
    final Long l1;
    final Long l2;

	/**
	 * Copy constructor
	 */
	LongPair(LongPair pair) {
		this(pair.l1, pair.l2);
	}

    LongPair(long l1, long l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    public String toString() {
        return AndroidUtil.stringFormatter("[%d,%d]", l1, l2);
    }
}
