package com.ewt45.patchapp;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.ewt45.patchapp.thread.Action.MSG_NULL;

import android.app.ProgressDialog;

import com.ewt45.patchapp.thread.Action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActionPool {
    ExecutorService singleThreadExecutor;
    ExecutorService futureThreadExecutor; //实在不知道怎么办了。再来一个线程池处理返回消息吧
    DoneCallback mCallback;
    LinkedList<Callable<String>> mTaskList = new LinkedList<>();

    boolean mVacant = true;

    public ActionPool(DoneCallback callback) {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        futureThreadExecutor = Executors.newSingleThreadExecutor();
        mCallback = callback;
    }

    /**
     * 新界面用的
     */
    public ActionPool(){
        this(null);
    }


    /**
     * fragment添加一个任务
     */
    @Deprecated
    public void submit(Action command) {
        /*
        将任务添加到线程池，并获得一个future
        将future加入到列表
        如何监听future
            在构造函数里新开一个线程，while一直循环。检查列表，如果为空，睡1秒再检查。如果不为空，就取出一个future，获取返回值，此时会一直等着，
            获取返回值之后调用callback，然后一次循环结束
        需要加synchronize的：list，函数？
         */
        int msgId = command.getStartMessage();
        if (msgId != MSG_NULL) {
            mCallback.setMessageStart(msgId);
        }

        Future<Integer> future = singleThreadExecutor.submit(command);

        futureThreadExecutor.execute(() -> {
            try {
                future.get();
                if (msgId == R.string.actmsg_signaldone) {
                    mCallback.restoreViewVis();
                }
                if (msgId != MSG_NULL) {
                    mCallback.setMessageFinish(msgId);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                mCallback.setMessageFail(msgId, e);
            }
        });
//        mTaskList.add(command);
//        //如果当前没有任务运行则运行一个任务
//        checkVacantToRun();

    }

    /**
     * 新界面用的
     */
    public void submitGroup(DoneCallback callback, Action... actions) {
        new Thread(() -> {
            for (Action command : actions) {
                int msgId = command.getStartMessage();
                if (msgId != MSG_NULL) callback.setMessageStart(msgId);
                try {
                    singleThreadExecutor.submit(command).get();
                    if (msgId != MSG_NULL) callback.setMessageFinish(msgId);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    callback.setMessageFail(msgId, e);
                }
            }
            callback.restoreViewVis();
        }, "线程：执行action").start();
    }

//    /**
//     * 检查当前是否空闲状态
//     */
//    private void checkVacantToRun() {
//        if (mVacant) {
//            //如果是空闲，获取队首任务并执行，然后监听
//            Future<String> future = singleThreadExecutor.submit(mTaskList.removeFirst());
//            //状态改为忙碌
//            mVacant = false;
//            //定时任务查询是否完成,完成时调用callBack更新textview显示,状态改为空闲，去掉定时器
//            Timer timer = new Timer();
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    if (future.isDone() || future.isCancelled()) {
//                        try {
//                            String message = future.get();
//                            if(message.equals(SignalDone.SIGNAL_DONE)){
//                                mCallback.restoreViewVis();
//                                Log.d("", "run: 消息是SIGNAL_DONE");
//                            }
//                            else{
//                                Log.d("", "run: 消息不是SIGNAL_DONE");
//                                mCallback.sendMessage(message);
//                            }
//
//                        } catch (ExecutionException | InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mVacant = true;
//                        timer.cancel();
//                    }
//                }
//            };
//            timer.schedule(task, 0, 500);
//
//        }
//
//    }


    public interface DoneCallback {
        //actionpool里调用sendmessage传字符串，接口定义在fragment里
//        public void sendMessage(int resId);

        /**
         * SignalDone的时候调用，用于恢复视图各选项enable状态
         */
        public void restoreViewVis();

        public void setMessageStart(int resId);

        public void setMessageFinish(int resId);

        public void setMessageFail(int resId, Exception e);
    }

    public abstract static class ProgressDialogCallback implements DoneCallback{
        ProgressDialog mDialog;
        String mText="";
        boolean noError=true;
        public ProgressDialogCallback(ProgressDialog dialog){
            mDialog = dialog;
        }

        @Override
        public void restoreViewVis() {
            //如果任务执行成功，自动关闭dialog，执行回调，
            //如果有任务执行失败，则提示查看日志。点确定再关闭窗口
            AndroidUtils.uiThread(()->{
                mDialog.setButton(BUTTON_POSITIVE, mDialog.getContext().getString(android.R.string.yes), (dialog, which) -> {
                    mDialog.dismiss();
                    onAllActionFinished(noError);
                });

                if(noError)
                    mDialog.getButton(BUTTON_POSITIVE).performClick();
            });

        }

        public abstract void onAllActionFinished(boolean noError);

        @Override
        public void setMessageStart(int resId) {
            //不在ui线程执行的话，会不会有问题？
            if(resId!=0){
                mText+=mDialog.getContext().getString(resId)+"  正在执行\n";
                AndroidUtils.uiThread(()-> mDialog.setMessage(mText));
            }
        }

        @Override
        public void setMessageFinish(int resId) {
            if(resId!=0){
                mText+=mDialog.getContext().getString(resId)+"  执行成功\n";
                AndroidUtils.uiThread(()-> mDialog.setMessage(mText));
            }
        }

        @Override
        public void setMessageFail(int resId, Exception e) {
            noError=false;
            if(resId!=0){
                mText+=mDialog.getContext().getString(resId)+"  执行失败\n";
                try (StringWriter strWriter = new StringWriter();
                     PrintWriter writer = new PrintWriter(strWriter)){
                    e.printStackTrace(writer);
                    strWriter.flush();
                    mText+=strWriter.toString()+'\n';
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                AndroidUtils.uiThread(()-> mDialog.setMessage(mText));
                e.printStackTrace();
            }
        }
    }
}
