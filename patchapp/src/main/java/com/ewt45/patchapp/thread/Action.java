package com.ewt45.patchapp.thread;

import java.util.concurrent.Callable;

public interface Action extends Callable<Integer> {
    public static int MSG_NULL = 0;
    public int getStartMessage();
}
