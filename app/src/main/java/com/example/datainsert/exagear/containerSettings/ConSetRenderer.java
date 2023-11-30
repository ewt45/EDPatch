package com.example.datainsert.exagear.containerSettings;

import static com.example.datainsert.exagear.RR.dimen.dialogPadding;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConSetRenderer {
    public static String DEFAULT_RENDERER_TXT_CONTENT = "" +
            "# 每个key代表一种渲染方式，该值不能随意修改。name代表环境设置中此选项显示的名称。env代表选择该选项后，启动容器时设置环境变量。其中LD_LIBRARY_PATH路径可以存放libGl.so.1等文件。删去某个key及其对应name,env行后，该渲染方式不会显示在选项中。" +
            "\n# Each 'key' represents a renderer, its value shouldn't be changed. 'name' represents the name of this renderer option in container settings. 'env' is the env variables added when launching container. LD_LIBRARY_PATH is the path where libs like libGL.so.1 are placed. Delete 'key', 'name', 'env' lines and its corresponding renderer will not be added to options in container settings." +

            "\n\nkey:" + RenEnum.LLVMPipe +
            "\n  name:LLVMPipe" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/llvm" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/lvp_icd.i686.json" +

            "\n\nkey:" + RenEnum.VirGL_Overlay +
            "\n  name:VirGL Overlay" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/vo" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/virtio_icd.i686.json" +
            "\n  env:VTEST_WIN=1" +
            "\n  env:VTEST_SOCK=" +

            "\n\nkey:" + RenEnum.VirGL_built_in +
            "\n  name:VirGL built-in" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/vb" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/virtio_icd.i686.json" +

            "\n\nkey:" + RenEnum.VirtIO_GPU +
            "\n  name:VirtIO-GPU" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/vg" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/virtio_icd.i686.json" +

            "\n\nkey:" + RenEnum.Turnip_Zink +
            "\n  name:Turnip Zink" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/tz" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/freedreno_icd.i686.json" +

            "\n\nkey:" + RenEnum.Turnip_DXVK +
            "\n  name:Turnip DXVK" +
            "\n  env:LD_LIBRARY_PATH=/opt/lib/td" +
            "\n  env:VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/freedreno_icd.i686.json" +
            "\n  env:GALLIUM_DRIVER=zink" +
            "\n  env:MESA_VK_WSI_DEBUG=sw";
    public static final File configFile = new File(QH.Files.edPatchDir(), "renderers.txt");
    /**
     * 1: 初次添加
     * 2: 路径和名称存在/opt/renderers.txt中。点击选项下方有文字提示修改了哪些内容
     * 3: renderers.txt 中，改为存储环境变量，以便用户可以自定义 （icd，为dxvk的tz指定不同的libvulkan_freedreno.so）
     * 4: 支持未知渲染（没有在enum中定义的）
     * 5: txt存储路径改为/opt/edpatch/renderers.txt
     */
    private static final int VERSION_FOR_EDPATCH = 5;
    private static final String TAG = "ConSetRenderer";
    /**
     * 从/opt/renderers.txt读取并存储到map.
     * <p>
     * map的key为txt中的key，value为包含txt中name和path的bundle。
     * 通过getString("name") 或 "path" 获取
     * <p>
     * 存入pref中的数值为key，所以要求key不要随便修改
     */
    public static Map<String, Bundle> renderersMap = new LinkedHashMap<>();//要求有序，否则顺序会乱
//    static {
//        ConSetRenderer.readRendererTxt();
//    }

    /**
     * 从/opt/renderers.txt读取并存储到map
     * `#` 为注释。 空行跳过
     * 每行：key:keyValue name:nameValue path:pathValue
     * key的值不能修改，每个key对应一个渲染方式，name为显示在容器设置里的名称，path为该渲染方式对应读取的libGL.so.1等库的路径
     * 若一行没有name: 或 path: ，则该行无效，不会被添加到选项中
     * key是key，value是name和path的bundle
     */
    public static void readRendererTxt() {

        renderersMap.clear();
        try {
            //迁移txt位置
            File oldFile = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "opt/renderers.txt");
            if (oldFile.exists() && !configFile.exists())
                FileUtils.moveFile(oldFile, configFile);

            //若没有该文件，自己创建一个并写入默认内容
            if (!configFile.exists()) {
                FileUtils.writeLines(configFile, "UTF-8", Arrays.asList(DEFAULT_RENDERER_TXT_CONTENT.split("\n")));
            }

            List<String> lines = new ArrayList<>();
            for (String s : FileUtils.readLines(configFile)) {
                String trim = s.trim();
                if (!(trim.startsWith("#") || trim.length() == 0))
                    lines.add(trim);
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (!line.startsWith("key:") || i + 1 >= lines.size() || !lines.get(i + 1).trim().startsWith("name:"))
                    continue;

                //读取2个属性
                String key = lines.get(i).trim().substring("key:".length()).trim();
                i++;
                String name = lines.get(i).trim().substring("name:".length()).trim();

                //读取环境变量
                ArrayList<String> envList = new ArrayList<>();
                while (i + 1 < lines.size() && lines.get(i + 1).trim().startsWith("env:")) {
                    envList.add(lines.get(i + 1).trim().substring("env:".length()).trim());
                    i++;
                }

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putStringArrayList("env", envList);
                renderersMap.put(key, bundle);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void buildRendererDialog(ListPreference preference) {
        readRendererTxt();

        CharSequence[] names = preference.getEntries();
        CharSequence[] keys = preference.getEntryValues();
        Context c = preference.getContext();

        ScrollView dialogView = new ScrollView(c);
        LinearLayout linearLayout = new LinearLayout(c);
        dialogView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int px = dialogPadding();
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

            //如果该值在txt中未记录，就不显示介绍。如果在txt中记录但不属于enum中已定义的，可以显示环境变量
            Bundle rendBundle = renderersMap.get(keys[i].toString());
            if (rendBundle == null)
                continue;

            LinearLayout linearInfo = new LinearLayout(c);
            linearInfo.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-1, -2);
            tvInfoParams.topMargin = AndroidHelpers.dpToPx(4);
            tvInfoParams.setMarginStart(AndroidHelpers.dpToPx(16));

            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> envList = rendBundle.getStringArrayList("env");
            assert envList != null;
            for (String s : envList)
                stringBuilder.append(s).append('\n');

            //预定义渲染的简介
            try {
                RenEnum renEnum = RenEnum.valueOf(keys[i].toString());
                stringBuilder.append(renEnum.info);
            } catch (Exception e) {
//                continue; //找不到对应的enum就直接跳过了，下面应该没啥其他要做的了吧？
            }

            //每一项太长了，先默认缩成一行，点击展开
            for (String oneStr : stringBuilder.toString().split("\n")) {
                if (oneStr.equals(""))
                    continue;
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
        LLVMPipe(""),
        VirGL_Overlay(""),
        VirGL_built_in("TMPDIR=z:/tmp libvirgl_test_server.so"),
        VirtIO_GPU("new Mcat().start()"),
        Turnip_Zink(""),
        Turnip_DXVK(""),
        ;
        public final String info;

        RenEnum(String s) {
            info = s;
        }
    }
}
