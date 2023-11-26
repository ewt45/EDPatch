package com.ewt45.patchapp.thread;

import com.ewt45.patchapp.R;

@Deprecated
public class SignalDone implements Action {
//    public static int SIGNAL_DONE = 54321;

    @Override
    public Integer call() throws Exception {
//        if(true)
//            throw new RuntimeException("测试异常");

        return R.string.actmsg_signaldone;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_signaldone;
    }
}
