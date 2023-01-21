package com.ewt45.patchapp.thread;

import com.ewt45.patchapp.R;

public class SignalDone implements Action {
//    public static int SIGNAL_DONE = 54321;

    @Override
    public Integer call() throws Exception {
        return R.string.actmsg_signaldone;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_signaldone;
    }
}
