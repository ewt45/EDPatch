package com.example.datainsert.exagear.containerSettings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConSetRenderer {
    public static final String DEFAULT_RENDERER_TXT_CONTENT = "" +
            "# 每个key代表一种渲染方式，该值不能随意修改。name代表环境设置中此选项显示的名称。path代表选择该选项后，启动容器时设置的LD_LIBRARY_PATH路径。若缺少name行或path行，该渲染方式不会显示在选项中。" +
            "\n# Each 'key' represents a renderer, its value shouldn't be changed. 'name' represents the name of this renderer option in container settings. 'path' represents the path to LD_LIBRARY_PATH that is set when the container is started. 'key' with no 'name' or 'path' line will not be added to options in container settings." +
            "\n\nkey:" + RenEnum.LLVMPipe + "\n  name:LLVMPipe" + "\n  path:/opt/lib/llvm" +
            "\n\nkey:" + RenEnum.VirGL_Overlay + "\n  name:VirGL Overlay" + "\n  path:/opt/lib/vo" +
            "\n\nkey:" + RenEnum.VirGL_built_in + "\n  name:VirGL built-in" + "\n  path:/opt/lib/vb" +
            "\n\nkey:" + RenEnum.VirtIO_GPU + "\n  name:VirtIO-GPU" + "\n  path:/opt/lib/vg" +
            "\n\nkey:" + RenEnum.Turnip_Zink + "\n  name:Turnip Zink" + "\n  path:/opt/lib/tz" +
            "\n\nkey:" + RenEnum.Turnip_DXVK + "\n  name:Turnip DXVK" + "\n  path:/opt/lib/td";
    private static final String TAG = "ConSetRenderer";
    /**
     * 1: 初次添加
     * 2: 路径和名称存在/opt/renderers.txt中。点击选项下方有文字提示修改了哪些内容
     */
    private static final int VERSION_FOR_EDPATCH = 2;
    /**
     * 从/opt/renderers.txt读取并存储到map.
     * <p>
     * map的key为txt中的key，value为包含txt中name和path的bundle。
     * 通过getString("name") 或 "path" 获取
     * <p>
     * 存入pref中的数值为key，所以要求key不要随便修改
     */
    public static Map<String, Bundle> renderersMap = new LinkedHashMap<>();//要求有序，否则顺序会乱

    static {
        ConSetRenderer.readRendererTxt();
    }

    /**
     * 从/opt/renderers.txt读取并存储到map
     * `#` 为注释。 空行跳过
     * 每行：key:keyValue name:nameValue path:pathValue
     * key的值不能修改，每个key对应一个渲染方式，name为显示在容器设置里的名称，path为该渲染方式对应读取的libGL.so.1等库的路径
     * 若一行没有name: 或 path: ，则该行无效，不会被添加到选项中
     * key是key，value是name和path的bundle
     */
    public static void readRendererTxt() {
        File configFile = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "opt/renderers.txt");


        renderersMap.clear();
        try {
            //若没有该文件，自己创建一个并写入默认内容
            if (!configFile.exists()) {
                FileUtils.writeLines(configFile, "UTF-8", Arrays.asList(DEFAULT_RENDERER_TXT_CONTENT.split("\n")));
            }

            List<String> lines = FileUtils.readLines(configFile);

            for (int i = 0; i < lines.size(); i++) {
                String trimLine = lines.get(i).trim();
                if (trimLine.startsWith("#") || trimLine.equals("") || i + 3 > lines.size())
                    continue;

                if (!(trimLine.startsWith("key:") || i + 3 <= lines.size()))
                    continue;

                //读取三个属性
                String localLine;
                localLine = lines.get(i).trim();
                String key = localLine.startsWith("key:") ? localLine.substring("key:".length()).trim() : null;
                localLine = lines.get(i + 1).trim();
                String name = localLine.startsWith("name:") ? localLine.substring("name:".length()).trim() : null;
                localLine = lines.get(i + 2).trim();
                String path = localLine.startsWith("path:") ? localLine.substring("path:".length()).trim() : null;

                if (key != null && name != null && path != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("path", path);
                    renderersMap.put(key, bundle);

                    i += 2;//只有在完整读取三行之后才跳到第三行，否则不管，让for自动一行一行过渡
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildRendererDialog(ListPreference preference) {
        ConSetRenderer.readRendererTxt();

        CharSequence[] names = preference.getEntries();
        CharSequence[] keys = preference.getEntryValues();
        Context c = preference.getContext();

        ScrollView dialogView = new ScrollView(c);
        LinearLayout linearLayout = new LinearLayout(c);
        dialogView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int px = QH.px(c, RR.attr.dialogPaddingDp);
        linearLayout.setPadding(px, px, px, px);

        RadioGroup radioGroup = new RadioGroup(c);
        for (int i = 0; i < names.length; i++) {
            RadioButton radioButton = new RadioButton(c);
            radioButton.setTag(i);
            radioButton.setText(names[i]);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            RadioGroup.LayoutParams radioBtnParams = new RadioGroup.LayoutParams(-1, -2);
            radioBtnParams.topMargin = AndroidHelpers.dpToPx(16);
            radioGroup.addView(radioButton, radioBtnParams);

            //每个渲染的简介
            ConSetRenderer.RenEnum renEnum;
            try {
                renEnum = ConSetRenderer.RenEnum.valueOf(keys[i].toString());
            } catch (Exception e) {
                continue; //找不到对应的enum就直接跳过了，下面应该没啥其他要做的了吧？
            }

            LinearLayout linearInfo = new LinearLayout(c);
            linearInfo.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-1, -2);
            tvInfoParams.topMargin = AndroidHelpers.dpToPx(4);
            tvInfoParams.setMarginStart(AndroidHelpers.dpToPx(16));

            //每一项太长了，先默认缩成一行，点击展开
            Bundle rendBundle = renderersMap.get(keys[i].toString()); //一定能获取到，因为上面获取renEnum如果报错就不会走到这
            assert rendBundle != null;
            String infoTotal = String.format("LD_LIBRARY_PATH=%s", rendBundle.getString("path")) +
                    ((renEnum.info.length() > 0) ? "\n" + renEnum.info : "");
            for (String oneStr : infoTotal.split("\n")) {
                TextView tvInfo = new TextView(c);
                tvInfo.setTextIsSelectable(true);
//                tvInfo.setLineSpacing(0, 1.2f);
                tvInfo.setSingleLine(true);
                tvInfo.setEllipsize(TextUtils.TruncateAt.END);
                tvInfo.setText(oneStr);
                tvInfo.setOnClickListener(v -> tvInfo.setSingleLine(tvInfo.getMaxLines() != 1));
                linearInfo.addView(tvInfo, tvInfoParams);
            }

            radioGroup.addView(linearInfo);
            linearInfo.setVisibility(View.GONE);

            //选择变化时，隐藏上一个渲染的简介，先显示勾选渲染的简介。将勾选的渲染的key存入pref中。
            String value = keys[i].toString();
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                linearInfo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (isChecked)
                    preference.setValue(value);
            });

            //勾选当前设置项
            if (keys[i].equals(preference.getValue()))
                radioButton.setChecked(true);
        }

        linearLayout.addView(radioGroup);

        new AlertDialog.Builder(c)
                .setView(dialogView)
                .setTitle(preference.getDialogTitle())
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                })
                .show();


    }

    /**
     * 渲染器选项。entry和entryvalue都用这一个了
     * 需要在添加环境变量的那个action（即外部）获取对应的字符串，如果硬编码，modder需要修改两处，可能会忽略。用enum只需要改一处即可。
     */
    public enum RenEnum {
        LLVMPipe("VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/lvp_icd.i686.json"),
        VirGL_Overlay("VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/lvp_icd.i686.json" +
                "\nVTEST_WIN=1" +
                "\nVTEST_SOCK="),
        VirGL_built_in("VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/lvp_icd.i686.json" +
                "\nTMPDIR=z:/tmp libvirgl_test_server.so"),
        VirtIO_GPU("new Mcat().start()"),
        Turnip_Zink(""),
        Turnip_DXVK("GALLIUM_DRIVER=zink" +
                "\nMESA_VK_WSI_DEBUG=sw"),
        ;
        public final String info ;

        RenEnum(String s) {
            info = s;
        }
    }
}
