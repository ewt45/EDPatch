package com.ewt45.patchapp.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ProgressBar;

import com.ewt45.patchapp.ActionPool;
import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.thread.Action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ActionProgressDialog extends ProgressDialog implements ActionPool.DoneCallback {
    private static final String TAG = "ActionProgressDialog";
    private final AllFinishCallback mCallback;
    //    ProgressDialog mDialog;
    SpannableStringBuilder mText;
    boolean noError = true;


    public ActionProgressDialog(Context context, AllFinishCallback callback) {
        super(context);

        mCallback = callback;
        mText = new SpannableStringBuilder("正在执行操作，请勿切换界面，以免出现问题。");
//        mText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, mText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MyApplication.data.logText.append("\n\n").append(mText.toString());
        setMessage(mText);
        setIndeterminate(true);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setButton(BUTTON_POSITIVE, getContext().getString(android.R.string.yes), (dialog, which) -> {
            dismiss();
            if (mCallback != null)
                mCallback.onAllActionFinished(noError);
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //构造函数里直接getButton还是null 用onShowListener的话又太慢，还没false就先true了。所以放到onCreate里了
        getButton(BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void show() {
        super.show();


    }

    public static void startActionsWithDialog(Context context, AllFinishCallback callback, Action... actions) {
        ActionProgressDialog dialog = new ActionProgressDialog(context, callback);
        MyApplication.data.mActionPool.submitGroup(dialog, actions);
        dialog.show();
//        return dialog;
    }

    @Override
    public void restoreViewVis() {
        //如果任务执行成功，自动关闭dialog，执行回调，
        //如果有任务执行失败，则提示查看日志。点确定再关闭窗口

        AndroidUtils.uiThread(() -> {
            Log.d(TAG, "restoreViewVis: 全部action结束，此时应该显示确定按钮");
            getButton(BUTTON_POSITIVE).setEnabled(true);
            setCancelable(true);
            setCanceledOnTouchOutside(true);

            //解决新图标不显示
            ProgressBar progress = findViewById(android.R.id.progress);
            if(progress!=null){
                Rect bounds = progress.getIndeterminateDrawable().getBounds();
                setIndeterminateDrawable(getContext().getDrawable(noError ? R.drawable.ic_check_circle : R.drawable.ic_error));
                progress.getIndeterminateDrawable().setBounds(bounds);
            }
            //
//            setProgressDrawable(getContext().getDrawable(noError ? R.drawable.ic_check_circle : R.drawable.ic_error));
//            setIcon(getContext().getDrawable(noError ? R.drawable.ic_check_circle : R.drawable.ic_error));

            if (noError){
                getButton(BUTTON_POSITIVE).performClick();
            } else {
                mText.append('\n').append("部分操作执行失败，具体报错请向下滑动标题栏查看日志。");
                setMessage(mText);
            }
        });

    }

    @Override
    public void setMessageStart(int resId) {
        //不在ui线程执行的话，会不会有问题？
        if (resId != 0) {
//            String actionStrStart = '\n' + getContext().getString(resId) + "  正在执行";
//            mText.append(actionStrStart, new ForegroundColorSpan(Color.BLACK), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //✔️ ❌ ⏳
            String actionStrStart = "\n⏳ " + getContext().getString(resId);
            mText.append(actionStrStart);
            MyApplication.data.logText.append(actionStrStart);
            AndroidUtils.uiThread(() -> setMessage(mText));
        }
    }

    @Override
    public void setMessageFinish(int resId) {
        if (resId != 0) {
            String actionStrStart = "\n⏳ " + getContext().getString(resId);
            String actionStrFinish = "\n✔️ "+getContext().getString(resId);
            if(mText.toString().endsWith(actionStrStart))
                mText.delete(mText.length() - actionStrStart.length(), mText.length());
//            String actionStrFinish = '\n' + getContext().getString(resId) + "  执行成功";
//            mText.append(actionStrFinish, new ForegroundColorSpan(Color.GREEN), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mText.append(actionStrFinish);
            MyApplication.data.logText.append(actionStrFinish);
            AndroidUtils.uiThread(() -> setMessage(mText));
        }
    }

    @Override
    public void setMessageFail(int resId, Exception e) {
        noError = false;
        if (resId != 0) {
            String actionStrStart = "\n⏳ " + getContext().getString(resId);
            String actionStrFailed = "\n❌ " + getContext().getString(resId);
            if(mText.toString().endsWith(actionStrStart))
                mText.delete(mText.length() - actionStrStart.length(), mText.length());
            mText.append(actionStrFailed);
            AndroidUtils.uiThread(() -> setMessage(mText));
            e.printStackTrace();

            StringBuilder details = new StringBuilder();
            try (StringWriter strWriter = new StringWriter();
                 PrintWriter writer = new PrintWriter(strWriter)) {
                e.printStackTrace(writer);
                strWriter.flush();
                details.append('\n').append(strWriter);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            MyApplication.data.logText.append(actionStrFailed).append('\n').append(details.toString());

        }
    }


    public interface AllFinishCallback {
        void onAllActionFinished(boolean noError);
    }
}
