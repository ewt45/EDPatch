package com.example.datainsert.exagear.FAB.dialogfragment.drived;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.RR;

import java.io.File;

/**
 * 用于存储当前路径的数据，检查路径是否可用 这个类用于action里检测是否有修改磁盘功能，不要乱动
 */
public class DrivePathChecker {
    private static final String TAG = "PathChecker";
    final TextView tvPar;
    final TextView tvDst;
    final TextView tvResult;
    String strPar;
    String strDst;

    public DrivePathChecker(TextView tvPar, TextView tvDst, TextView tvResult) {
        this.tvPar = tvPar;
        this.tvDst = tvDst;
        this.tvResult = tvResult;
    }

    public void setStrPar(String strPar) {
        this.strPar = strPar;
    }

    public void setStrDst(String strDst) {
        this.strDst = strDst;
    }


    /**
     * 更新路径后应该调用，重新检查可用性
     * 检查当前设置的文件夹是否有效，并将检查结果显示到textview上
     */
    public void updateCheckResult() {
        Log.d(TAG, "updateCheckResult: ");
        String currentDstDirName = strDst, currentParDir = strPar;
        boolean result = false;
        String fullPath = (currentDstDirName == null || currentDstDirName.equals("") || currentParDir == null || currentParDir.length() == 0)
                ? null : currentParDir + "/" + currentDstDirName;
        String situation = null;
        if (fullPath == null)
            situation = getS(RR.DriveD_getPathFail);
        else {
            String[] errors = getSArr(RR.DriveD2_errors);
            try {
                File dstFile = new File(currentParDir, currentDstDirName);
                if (ContextCompat.checkSelfPermission(Globals.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    situation = errors[0];//"请先授予app存储权限"
                else if (!dstFile.exists()) situation = errors[1];//该文件夹不存在
                else if (!dstFile.isDirectory()) situation = errors[2];//该路径指向的是文件而不是文件夹
                else if (!dstFile.canRead()) situation = errors[3];//没有对该文件夹的读取权限
                else if (!dstFile.canWrite()) situation = errors[4];//没有对该文件夹的写入权限
                else result = true;
            } catch (Exception e) {
                situation = errors[5] + e.getLocalizedMessage();//"读取该路径时出现错误: "
            }
        }

        //将结果显示到textview上
        SpannableStringBuilder tvResultStr = new SpannableStringBuilder();
        int colorCorrect = 0xff67C23A, colorWrong = 0xffF56C6C;
        //完整路径
        if (fullPath != null) {

            String fullPathDisplay = fullPath + (situation == null ? "  √" : "  ×");
            tvResultStr.append(fullPathDisplay);
            tvResultStr.setSpan(new StyleSpan(Typeface.BOLD), tvResultStr.length() - fullPathDisplay.length(), tvResultStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvResultStr.setSpan(new ForegroundColorSpan(situation == null ? colorCorrect : colorWrong), tvResultStr.length() - 1, tvResultStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //错误信息
        if (situation != null) {
            tvResultStr.append(fullPath != null ? "\n\n" : "").append("⚠");
            tvResultStr.setSpan(new ForegroundColorSpan(colorWrong), tvResultStr.length() - 1, tvResultStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvResultStr.append("  ").append(situation);
//            tvResultStr.setSpan(new ForegroundColorSpan(colorWrong), tvResultStr.length() - situation.length(), tvResultStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().getPackageName(), null)));
        tvResult.setText(tvResultStr);
    }
}
