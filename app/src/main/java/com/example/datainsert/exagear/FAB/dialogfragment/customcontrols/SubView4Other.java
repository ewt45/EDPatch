package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.PopupMenu;
import android.system.ErrnoException;
import android.system.Os;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.FileHelpers;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 一些说明。导入导出功能
 */
public class SubView4Other extends LinearLayout {
    private static final String TAG = "SubView4Other";
    TransferCallback mCallback;

    public SubView4Other(Context context) {
        super(context);

        setOrientation(VERTICAL);
        Context c = context;

        //试试html格式的textview？
        TextView textView = new TextView(c);
        textView.setLineSpacing(0, 1.2f);
        textView.setText(Html.fromHtml(getS(RR.cmCtrl_s4_tips)));
//        textView.setClickable(true);
//        textView.setTextIsSelectable(true);
        addView(textView);

        Button btnExport = new Button(c);
        btnExport.setText(getS(RR.cmCtrl_s4_export));
        btnExport.setOnClickListener(v -> mCallback.exportData());
        Button btnImport = new Button(c);
        btnImport.setText(getS(RR.cmCtrl_s4_import));
        btnImport.setOnClickListener(v -> mCallback.importData());
        LinearLayout linear2Btn = new LinearLayout(c);
        linear2Btn.addView(btnExport, new LayoutParams(-2, -2));
        linear2Btn.addView(btnImport, new LayoutParams(-2, -2));
        LinearLayout linearTransfer = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_s4_trsportTitle), linear2Btn, true);
        setDialogTooltip(linearTransfer.getChildAt(0), getS(RR.cmCtrl_s4_trsportTip));
        addView(linearTransfer);

        //如果在xserver界面，禁止导入（否则退出的时候还会重新把旧的存一次）
        if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity)
            btnImport.setEnabled(false);


        //新建，导入，导出，说明，切换 (切换的时候，如果没有，就创建一个再链接。）
        Button btnSelect = new Button(c);
        btnSelect.setText("编辑配置");
        btnSelect.setOnClickListener(v -> {
            File[] profiles = CustomControls.dataDir().listFiles(pathname -> pathname.isDirectory());
            String currProfileName = getCurrProfileName();
            PopupMenu popupMenu = new PopupMenu(c, v);
            Menu menu = popupMenu.getMenu();
            menu.add("新建").setOnMenuItemClickListener(item -> {
                File newPro = createNewProfile("new");
                makeProfileCurrent(newPro.getName());
                mCallback.dismiss();
                return true;
            });
            menu.add("导入").setOnMenuItemClickListener(item -> {
                File importProfileDir = null;
                try {
                    Log.d("SubView4Other", "SubView4Other: 旧配置名：" + currProfileName);
                    ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard.getPrimaryClip() == null || !(clipboard.hasPrimaryClip())
                            || !(Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                        throw new Exception("剪切板没有数据 或者剪切板数据类型不是文本");
                    }
                    String data = (String) clipboard.getPrimaryClip().getItemAt(0).getText(); // Gets the clipboard as text.
                    if (data == null)
                        throw new Exception("剪切板有文本，但不是普通文本。可能是URI？");// The clipboard does not contain text. If it contains a URI, attempts to get data from it

                    //尝试转换为按键数据
                    importProfileDir = createNewProfile("import");
                    makeProfileCurrent(importProfileDir.getName());
                    FormatHelper.dataImport(data);
                    //如果成功了，关闭对话框，并且设置本次存储新数据 (最好直接关闭了，不知道在中途修改model整体实例，会不会导致其他功能未更新而出错）
                    mCallback.dismiss();
                    Toast.makeText(c, getS(RR.cmCtrl_s4_importResult).split("\\$")[0], Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    mCallback.dismiss();
                    //如果导入失败，删除新存档文件夹，恢复旧存档为curr
                    if (importProfileDir != null)
                        FileUtils.deleteQuietly(importProfileDir);
                    if (currProfileName != null)
                        makeProfileCurrent(currProfileName);
                    Toast.makeText(c, getS(RR.cmCtrl_s4_importResult).split("\\$")[1], Toast.LENGTH_SHORT).show();

                }

                return true;
            });
            SubMenu subSelect = menu.addSubMenu("全部配置");
            menu.add("说明").setOnMenuItemClickListener(item -> {
                new AlertDialog.Builder(c).setMessage("导出：以文本形式导出到剪切板，请自行保存。\n\n导入：从剪切板中读取文本，保存为新配置。").setPositiveButton(android.R.string.yes, null).show();
                return true;
            });
            subSelect.add("当前: " + currProfileName);
            for (File file : profiles) {
                if (file.getName().equals("current"))
                    continue;
                String fileName = file.getName();
                SubMenu subMenu = subSelect.addSubMenu(fileName);
                subMenu.add("选择").setOnMenuItemClickListener(item -> {
                    makeProfileCurrent(fileName);
                    mCallback.dismiss();
                    return true;
                });
                subMenu.add("导出").setOnMenuItemClickListener(item -> {
                    mCallback.exportData();
                    return true;
                });
                subMenu.add("重命名").setEnabled(!fileName.equals("default")).setOnMenuItemClickListener(item -> {
                    EditText editText = new EditText(c);
                    editText.setText(fileName);
                    new AlertDialog.Builder(c).setView(editText).setPositiveButton(android.R.string.yes, ((dialog, which) -> {
                        String newName = editText.getText().toString();
                        if (newName.trim().length() == 0)
                            newName = fileName;
                        File newDir = createNewProfile(newName);
                        FileUtils.deleteQuietly(newDir);
                        try {
                            FileUtils.moveDirectory(file, newDir);
                            makeProfileCurrent(newDir.getName());
                            mCallback.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })).setCancelable(false).show();
                    return true;
                });
                subMenu.add("删除").setEnabled(!fileName.equals("default")).setOnMenuItemClickListener(item -> {
                    FileUtils.deleteQuietly(file);
                    makeProfileCurrent("default");
                    mCallback.dismiss();
                    return true;
                });
            }
            popupMenu.show();
        });
//        addView(btnSelect);
//        if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity)
//            btnSelect.setEnabled(false);

    }

    private static File getCurrProfileDir() {
        return new File(CustomControls.dataDir(), "current");
    }

    private static String getCurrProfileName() {
        try {
            return getCurrProfileDir().getCanonicalFile().getName();
        } catch (IOException e) {
            e.printStackTrace();
            return "default";
        }
    }

    private static File getDefaultProfileDir() {
        return new File(CustomControls.dataDir(), "default");
    }

    /**
     * 文件保存规则：/opt/edpatch/custom_controls下，一个文件夹就是一个配置，内含两个txt（txt不是必须包含）。
     * 默认必须存在default文件夹。current文件夹为当前所选配置的对应文件夹的符号链接，默认所选配置为default。
     * <p>
     * 算了不写了
     * <p>
     * 保证在读取配置之前，新位置current文件夹有两个txt且是链接
     * 1. 如果新位置default文件夹不存在，则创建一个，
     * 2. 如果新位置current文件夹不存在，则从default链接过去
     * 3. 如果有旧文件，挪到新位置default文件夹中
     */
    public static void prepareForProfilesV2() {
        try {
//            File defDir = getDefaultProfileDir();
//            if (!defDir.exists()) {
//                boolean b = defDir.mkdirs();
//
//            }
//
//            File currDir = getCurrProfileDir();
//            if (!currDir.exists()) {
//                FileUtils.deleteQuietly(currDir);
//                Os.symlink(defDir.getPath(), currDir.getPath());
//            }
//            if (KeyCodes2.oldKeyStoreFile.exists())
//                FileUtils.moveFile(KeyCodes2.oldKeyStoreFile, KeyCodes2.keyStoreFile);
//            if (KeyCodes3.oldKeyStoreFile.exists())
//                FileUtils.moveFile(KeyCodes3.oldKeyStoreFile, KeyCodes3.keyStoreFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个新的配置目录。若已经存在给定配置名的文件夹，则会在name后加_1
     *
     * @param name 理想的配置名。
     * @return 实际创建的配置目录
     */
    private File createNewProfile(String name) {
        File newDir = new File(CustomControls.dataDir(), name);
        while (newDir.exists())
            newDir = new File(newDir.getAbsolutePath() + "_1");
        boolean b = newDir.mkdirs();
        Log.d(TAG, "createNewProfile: 创建新配置: " + newDir.getName());
        return newDir;
    }

    /**
     * 将某个配置文件夹链接到current。调用此函数前请确保 此配置名 对应的目录存在。
     *
     * @param profileName 配置名。
     */
    private void makeProfileCurrent(String profileName) {
        File newDir = new File(CustomControls.dataDir(), profileName);
        try {
            File curr = getCurrProfileDir();
            FileUtils.deleteQuietly(curr);
            Os.symlink(newDir.getPath(), curr.getPath());
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化时设置导入或导出数据的回调
     */
    public void setCallback(TransferCallback mCallback) {
        this.mCallback = mCallback;
    }

    interface TransferCallback {
        void dismiss();

        void exportData();

        void importData();
    }
}
