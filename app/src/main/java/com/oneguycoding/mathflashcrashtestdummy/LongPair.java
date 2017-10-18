package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * A simple container class for passing pairs of longs
 *
 * Created by steeve on 07/09/17.
 */

class LongPair implements Serializable {
	private static final Random RANDOMIZER;
	private static final LongPair DEFAULT_RANGE;
	private static final int MAX_VALUE; // int is a 32bit value
	private static final int MAX_SHIFT;

	static {
		MAX_VALUE = 0xffff; // 0-65535
		MAX_SHIFT = 10;
		RANDOMIZER = new Random(System.currentTimeMillis());
		DEFAULT_RANGE = new LongPair(0, 12);
	}

    final Long l1;
    final Long l2;

	/**
	 * Copy constructor
	 */
	LongPair(LongPair pair) {
		this(pair.l1, pair.l2);
	}

	/**
	 * Main constructor
	 *
	 * @param l1 - first long number
	 * @param l2 - second long number
	 */
    LongPair(long l1, long l2) {
	    if (l1 > MAX_VALUE) {
		    throw new IllegalArgumentException("Maximum value for l1 is "+MAX_VALUE);
	    }
        this.l1 = l1;
	    if (l2 > MAX_VALUE) {
		    throw new IllegalArgumentException("Maximum value for l2 is "+MAX_VALUE);
	    }
        this.l2 = l2;
    }

	/**
	 * Testing randomly generated LongPair values
	 */
	LongPair() {
	    this(rand_range(DEFAULT_RANGE), rand_range(DEFAULT_RANGE));
    }

	/**
	 * compute a unique hashcode for LongPair
	 * @return unique hashcode
	 */
	@Override
	public int hashCode() {
		//noinspection UnnecessaryLocalVariable
		int code = (int)((l1 << MAX_SHIFT) + l2);
		return code;
	}

	/**
	 * check that this value is equal to the other (LongPair) object
	 *
	 * @param other
	 * @return true if this and other are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		return (l1 == ((LongPair)other).l1 && l2 == ((LongPair)other).l2);
	}

	@Override
	public String toString() {
        return AndroidUtil.stringFormatter("[%d,%d]", l1, l2);
    }

	/**
	 * Compare l1 with l2.
	 *
	 * @param item - LongPair to compare with this
	 *
	 * @return the value 0 if this LongPair is equal to the argument item; a value < 0 if
	 * this LongPair is numerically less than the argument item; and a value > 0 if this
	 * LongPair is numerically greater than the argument item (signed comparison).
	 */
	public int compareTo(LongPair item) {
		if (item == null) {
			return 1;
		}
		if (item.l1 == l1) {
			return l2.compareTo(item.l2);
		}
		return l1.compareTo(item.l1);
	}

	/**
	 * Get a RANDOMIZER integer in the DEFAULT_RANGE lp.l1 to lp.l2, inclusive
	 *
	 * @param lp - DEFAULT_RANGE to select RANDOMIZER number from
	 *
	 * @return RANDOMIZER number
	 */
	public static long rand_range(LongPair lp) {
		//return lp.l1 + (long)(Math.RANDOMIZER() * lp.l2);
		// can't seem to seed ThreadLocalRandom
		//return ThreadLocalRandom.current().nextLong(lp.l1, lp.l2 + 1);
		long lower_bound = lp.l1;
		long upper_bound = lp.l2+1;
		long number = lower_bound+((long)(RANDOMIZER.nextDouble()*(upper_bound-lower_bound)));
		if (BuildConfig.DEBUG) {
			if ((number < lp.l1 || number > lp.l2)) {
				Log.d("RANDOM", AndroidUtil.stringFormatter("Number is out of DEFAULT_RANGE: %s [%s,%s]", number, lp.l1, lp.l2));
				if (number < lp.l1) {
					number = lp.l1;
				} else if (number > lp.l2) {
					number = lp.l2;
				}
			}
		}
		return number;
	}




}
