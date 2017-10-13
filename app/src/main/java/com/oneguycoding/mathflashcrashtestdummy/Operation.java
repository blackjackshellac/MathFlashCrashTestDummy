package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;

import java.util.HashMap;

/**
 *
 * enum for Operations, also contains methods for ...
 *
 * doit - performing operation
 * rand_range - get a random number from l1 to l2 inclusive
 *
 *
 * Created by steeve on 07/09/17.
 */

enum Operation {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE;

    private static final HashMap<Operation,Character> chars;
	public static final String CHARS_LIST;

	static {
        chars = new HashMap<>();
        chars.put(PLUS, '+');
        chars.put(MINUS, '-');
        chars.put(MULTIPLY, '*');
        chars.put(DIVIDE, '/');

	    CHARS_LIST = Operation.chars_list();

    }

	private static String chars_list() {
		StringBuilder sb = new StringBuilder();
		for (Character val : chars.values()) {
			sb.append(String.format("'%s',", val.toString()));
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public long doit(LongPair nums) {
        long num1 = nums.l1;
        long num2 = nums.l2;
        long answer;

        try {
            switch (this) {
                default:
                case PLUS:
                    answer = num1 + num2;
                    break;
                case MINUS:
                    answer = num1 - num2;
                    break;
                case MULTIPLY:
                    answer = num1 * num2;
                    break;
                case DIVIDE:
                    answer = num1 / num2;
                    break;
            }
        } catch(Exception e) {
            answer = 0;
        }
        return answer;
    }

	/**
	 * Get a pair of random numbers from the given ranges lp1 and lp2.  For subtraction ensure that
	 * the answer is not negative by swapping the values. For division avoid divide by zero and
	 * calculate the top value to ensure that the answer is an integer.
	 *
	 * @param lp1 - range to select top value
	 * @param lp2 - range to select bottom value
	 * @return new range from lp1 and lp2 appropriate for the given operation
	 */
	public LongPair randomize(LongPair lp1, LongPair lp2) {
        long n1 = LongPair.rand_range(lp1);
        long n2 = LongPair.rand_range(lp2);
        switch (this) {
            default:
                break;
            case MINUS:
                if (n1 < n2) {
	                Log.d("Operation", String.format("swapping n1=%d and n2=%d", n1, n2));
	                long n = n1;
                    n1 = n2;
                    n2 = n;
                }
                break;
            case DIVIDE:
                while (true) {
                    if (n2 != 0) {
                        break;
                    }
                    n2 = LongPair.rand_range(lp2);
                }
                // n1 = 7 and n2 = 4, then do n1 = n1*n2 = 28 so that the answer is 7;
                n1 *= n2;
                break;
        }
        return new LongPair(n1, n2);
    }

	/**
	 * Get operation as single character
	 *
	 * @return return character associated with operation
	 */
	public String toChar() {
        return chars.get(this).toString();
    }
}
