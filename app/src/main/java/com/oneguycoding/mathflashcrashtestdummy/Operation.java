package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;
import android.util.StringBuilderPrinter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by steeve on 07/09/17.
 */

public enum Operation {
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

    public static Operation rand_op() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

    public long rand_range(LongPair lp) {
	    //return lp.l1 + (long)(Math.random() * lp.l2);
        return ThreadLocalRandom.current().nextLong(lp.l1, lp.l2 + 1);
    }

    public LongPair randomize(LongPair lp1, LongPair lp2) {
        long n1 = rand_range(lp1);
        long n2 = rand_range(lp2);
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
                    n2 = rand_range(lp2);
                }
                // n1 = 7 and n2 = 4, then do n1 = n1*n2;
                n1 *= n2;
                break;
        }
        return new LongPair(n1, n2);
    }

    private static String chars_list() {
        StringBuilder sb = new StringBuilder();
        for (Character val : chars.values()) {
            sb.append(String.format("'%s',", val.toString()));
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public String toChar() {
        return chars.get(this).toString();
    }
}
