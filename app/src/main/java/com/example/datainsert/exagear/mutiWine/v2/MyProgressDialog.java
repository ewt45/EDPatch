package com.example.datainsert.exagear.mutiWine.v2;

import android.app.ProgressDialog;
import android.widget.Button;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.QH;

/**
 * 用于长时间后台操作（下载或解压）时使用对话框阻止用户进行其他操作。（并显示进度）
 * <p/>
 * 使用：
 * <br/>
 * - 新建：MyProgressDialog dialog = new MyProgressDialog().init("解压中", true);
 * <br/>
 * - 显示：dialog.show();
 * <br/>
 * - 完成,可以关闭：dialog.done();
 */
public class MyProgressDialog extends ProgressDialog {
    Callback defaultCallback = new Callback() {
        @Override
        public void onFinish(String message) {
            done(message);
        }

        @Override
        public void updateProgress(int value) {
            setProgress(value);
        }
    };

    public MyProgressDialog() {
        super(QH.getCurrentActivity());
    }

    /**
     * 显示文字和直线进度条。indeterminate为true时无进度预览
     */
    public static ProgressDialog show(String message, boolean indeterminate) {
        ProgressDialog progressDialog = new ProgressDialog(QH.getCurrentActivity());
        progressDialog.setMessage(message);
        progressDialog.setButton(BUTTON_POSITIVE, AndroidHelpers.getString(android.R.string.yes), (dialog, which) -> dialog.dismiss());
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.show();
        progressDialog.getButton(BUTTON_POSITIVE).setEnabled(false);//要在show之后设置不然按钮是null
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            UiThread.post(() -> {
                progressDialog.setMessage("解压完成/失败");
//                progressDialog.setIndeterminateDrawable(null);//完成后关闭
                progressDialog.setIndeterminate(false);
                progressDialog.setProgress(100);
                progressDialog.getButton(BUTTON_POSITIVE).setEnabled(true);
            });

        }).start();
        return progressDialog;
    }

    /**
     * 显示文字和直线进度条。indeterminate为true时无进度预览
     * <p>
     * 可能会被调用多次
     */
    public MyProgressDialog init(String message, boolean indeterminate) {
        setMessage(message);
        setButton(BUTTON_POSITIVE, AndroidHelpers.getString(android.R.string.yes), (dialog, which) -> dialog.dismiss());
        //可能会被调用多次，第二次往后需要在这里设置enable
        Button btn = getButton(BUTTON_POSITIVE);
        if (btn != null)
            btn.setEnabled(false);

        setCancelable(false);
        setProgressStyle(STYLE_HORIZONTAL);
        setProgress(0);
        setIndeterminate(indeterminate);
        return this;
    }

    @Override
    public void show() {
        super.show();
        //任务执行过程中，确定按钮禁用
        getButton(BUTTON_POSITIVE).setEnabled(false);
    }

    /**
     * 任务执行完毕，可以关闭对话框
     */
    public void done(String message) {
        if (message != null)
            setMessage(message);
        setIndeterminate(false);
        setProgress(getMax());
        getButton(BUTTON_POSITIVE).setEnabled(true);

    }
    /**
     * 同done，但是显示进度为0
     */
    public void fail(String message){
        if (message != null)
            setMessage(message);
        setIndeterminate(false);
        setProgress(0);
        getButton(BUTTON_POSITIVE).setEnabled(true);
    }

    /**
     * 用于给不参与图形界面的代码 使用的回调. 没有进度更新提示
     */

//    public interface Callback extends FullCallback {
//        default void updateProgress(int value){};
//    }


    /**
     * 用于给不参与图形界面的代码 使用的回调
     */
    public interface Callback {

        void onFinish(String message);

        default void updateProgress(int value) {
        }

        ;
    }
}
