package com.oneguycoding.mathflashcrashtestdummy;

import java.io.Serializable;

/**
 * Created by steeve on 07/09/17.
 */

public class LongPair implements Serializable {
    public final Long l1;
    public final Long l2;

    LongPair(long l1, long l2) {
        this.l1 = l1;
        this.l2 = l2;
    }
}
