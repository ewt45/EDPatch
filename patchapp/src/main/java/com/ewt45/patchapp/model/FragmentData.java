package com.ewt45.patchapp.model;

import android.text.SpannableStringBuilder;

import com.ewt45.patchapp.ActionPool;
import com.ewt45.patchapp.thread.Func;

import java.util.ArrayList;
import java.util.List;

public class FragmentData {
    /**
     * 尚未适配
     */
    public int currentStepIndex = 0;
    public ActionPool mActionPool = new ActionPool(); //用于替代单线程池，并处理返回结果显示
    public boolean isShowingLog = false; //如果正在显示log，则屏蔽返回键，防止切换fragment导致fab变化
    public SpannableStringBuilder logText = new SpannableStringBuilder(); //用于记录日志，以供日志视图显示。包含报错
}
