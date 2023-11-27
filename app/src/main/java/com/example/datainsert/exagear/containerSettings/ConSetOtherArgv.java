package com.example.datainsert.exagear.containerSettings;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.RR.dimen.margin8Dp;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_EARLIER;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_FRONT;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_LATER;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.TYPE_CMD;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.TYPE_ENV;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.ed.fragments.ContainerSettingsFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.containerSettings.otherargv.AllArgsAdapter;
import com.example.datainsert.exagear.containerSettings.otherargv.Argument;
import com.example.datainsert.exagear.containerSettings.otherargv.Arguments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置处理器核心
 */
public class ConSetOtherArgv {

    /**
     * 1: 初版。
     * 点击后显示参数库，显示全部可用的参数列表。被勾选的参数会在当前容器被启用。
     * 第一个参数固定显示cpu核心选择。
     * 参数分为单参数和参数组。若多个单参数包含相同的名称（以---分割）则合并为参数组。参数组中最多只能有一个被勾选。
     */
    private static final int VERSION_FOR_EDPATCH = 1;
    private static final String TAG = "ConSetOtherArgv";
    public static String KEY_OTHER_ARGV_STUB = "OTHER_ARGV_STUB", KEY_PREVIEW_ORI_CMD = "OTHER_ARGV_PREVIEW_ORI_CMD",
            VAL_OTHER_ARGV_STUB = getS(RR.conSet_otherArgv_prefValue), VAL_PREVIEW_ORI_CMD = "wine explorer /desktop=shell,800x600 /opt/TFM.exe D:/";//VAL_OTHER_ARGV_STUB = getS(RR.conSet_otherArgv_hint)
    public static String KEY_TASKSET = "KEY_TASKSET", VAL_TASKSET_DEFAULT = "4,5,6,7";
    private static int[] defaultAvailableCores = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    public static void buildTasksetPref(ContainerSettingsFragment fragment) {

        //新建preference时的context需要为 从已构建的preference获取的contextwrapper，否则样式会不同
        EditTextPreference tasksetPref = new EditTextPreference(fragment.getPreferenceManager().getContext());
        tasksetPref.setTitle(getS(RR.conSet_otherArgv_title));
        tasksetPref.setDialogTitle(getS(RR.conSet_otherArgv_title));
        tasksetPref.setKey(KEY_OTHER_ARGV_STUB);
//        tasksetPref.setSummary("%s");
        tasksetPref.setOrder(3);
        tasksetPref.setDefaultValue(VAL_OTHER_ARGV_STUB);
        tasksetPref.setSummary(VAL_OTHER_ARGV_STUB);
        tasksetPref.setText(VAL_OTHER_ARGV_STUB);
        SharedPreferences sp = fragment.getPreferenceManager().getSharedPreferences();
        if (sp != null)
            sp.edit().putString(KEY_OTHER_ARGV_STUB, VAL_OTHER_ARGV_STUB).apply();
        fragment.getPreferenceScreen().addPreference(tasksetPref);

    }

    public static void buildDialog(EditTextPreference preference) {
        Context c = preference.getContext();
        String prefName = preference.getPreferenceManager().getSharedPreferencesName();
        int contId = Integer.parseInt(prefName.substring(prefName.lastIndexOf("_") + 1));

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);

        LinearLayout.LayoutParams topMarginParams = new LinearLayout.LayoutParams(-1, -2);
        topMarginParams.topMargin = RR.dimen.margin8Dp();
        LinearLayout.LayoutParams wrapWidthParams = new LinearLayout.LayoutParams(-2, -2);
        wrapWidthParams.topMargin = RR.dimen.margin8Dp();
        LinearLayout.LayoutParams marginStartParam = new LinearLayout.LayoutParams(-1, -2);
        marginStartParam.setMarginStart(margin8Dp());
        LinearLayout.LayoutParams topStartMarginParams = new LinearLayout.LayoutParams(topMarginParams);
        topStartMarginParams.setMarginStart(margin8Dp());
        LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(0, -2);
        weightParams.weight = 1;

        Arguments.allFromPoolFile(contId);
        AllArgsAdapter allArgsAdapter = new AllArgsAdapter(contId);

        //参数编辑按钮
        Button btnAdd = new Button(c);
        btnAdd.setText(getS(RR.global_add));//编辑
        QH.setButtonBorderless(btnAdd);
        btnAdd.setOnClickListener(v -> allArgsAdapter.buildEditArgDialog(v.getContext(), -1, -1,getS(RR.global_add)));
        btnAdd.setAllCaps(false);

        Button btnPreview = new Button(c);
        btnPreview.setText(getS(RR.othArg_preview));//预览
        btnPreview.setOnClickListener(v -> buildPreviewDialog(c, contId));
        QH.setButtonBorderless(btnPreview);
        btnPreview.setAllCaps(false);

        LinearLayout linearArgBtns = new LinearLayout(c);
        linearArgBtns.setOrientation(HORIZONTAL);
        linearArgBtns.addView(btnAdd, weightParams);
        linearArgBtns.addView(btnPreview, weightParams);

        //现在一旦显示dialog，只在初始化时创建一次all，之后全部修改都同步到all列表，在关闭dialog时将all列表写入文本。所以在此期间请勿重复读取txt，以防all被替换
        TextView tvInfo = new TextView(c);
        tvInfo.setLineSpacing(0, 1.1f);
        String[] infoStrs = getSArr(RR.othArg_info);
        SpannableStringBuilder infoBuilder = new SpannableStringBuilder();
        infoBuilder.append(infoStrs[0]).append(" ").append(infoStrs[1]).setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                new AlertDialog.Builder(widget.getContext())
                        .setMessage(getS(RR.othArg_info_more)+"\n\n"+getS((RR.othArg_edit_typeInfo))).show();
            }
        }, infoBuilder.length() - infoStrs[1].length(), infoBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvInfo.setText(infoBuilder);

        RecyclerView recyclerView = new RecyclerView(c);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(allArgsAdapter);
        //加到exagear里报错。只好删了。
//        recyclerView.setVerticalScrollBarEnabled(true);
//        recyclerView.setScrollbarFadingEnabled(false);

        linearRoot.addView(tvInfo);
        linearRoot.addView(linearArgBtns, topMarginParams);
        linearRoot.addView(recyclerView, topMarginParams);

        new AlertDialog.Builder(c)
                .setTitle(preference.getDialogTitle())
                .setView(QH.wrapAsDialogScrollView(linearRoot))//QH.wrapAsDialogScrollView(linearRoot)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                //编辑参数完成时，将新的参数库的全部参数写入文件，将勾选的参数写入当前容器参数文件。
                .setPositiveButton(android.R.string.ok, (dialog, which) -> Arguments.allToPoolFile(contId))
                .show();
    }

    /**
     * 点击预览按钮后，显示dialog，提供一个可编辑的示例命令及添加额外参数后的命令。<br/>
     * 直接从all读取当前已启用的参数，不会从txt中读取
     */
    private static void buildPreviewDialog(Context c, int contId) {
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        LinearLayout.LayoutParams marginTopParams = new LinearLayout.LayoutParams(-1, -2);
        marginTopParams.topMargin = margin8Dp();

        TextView textNewCmd = new TextView(c);
        textNewCmd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textNewCmd.setLineSpacing(0, 1.1f);

        EditText editOriCmd = new EditText(c);
        editOriCmd.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL);
        editOriCmd.setSingleLine(true);
        editOriCmd.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            String newOriCmd = s.toString().trim().length() > 0 ? s.toString() : VAL_PREVIEW_ORI_CMD;
            QH.getPreference().edit().putString(KEY_PREVIEW_ORI_CMD, newOriCmd).apply();
            ArrayList<String> envList = new ArrayList<>();
            String newCmd = insertArgsToWineCmd(newOriCmd, envList, contId); //不能和下面一行合并，因为需要先填充envList
            textNewCmd.setText(String.format("env:  %s\n\ncmd:  %s", envList, newCmd));
        });
        editOriCmd.setText(QH.getPreference().getString(KEY_PREVIEW_ORI_CMD, VAL_PREVIEW_ORI_CMD));

        String[] titlesStr = getSArr(RR.othArg_preview_titles);
        linearRoot.addView(QH.getOneLineWithTitle(c, titlesStr[0], editOriCmd, true), marginTopParams); //原命令
        linearRoot.addView(QH.getOneLineWithTitle(c, titlesStr[1], textNewCmd, true), marginTopParams); //插入参数后

        new AlertDialog.Builder(c)
                .setTitle(getS(RR.othArg_preview))
                .setView(QH.wrapAsDialogScrollView(linearRoot))//QH.wrapAsDialogScrollView(linearRoot)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .show();
    }

    /**
     * 将该容器启用的参数插入wine命令中。<br/>
     * 已启用的参数直接从all读取，不会从txt刷新，因此调用此函数前请确保all是最新的。
     *
     * @param oriWineCmd 原wine命令
     * @param envList    当前已存在的环境变量列表。对环境变量的更改会直接应用到这个列表中。
     * @param contId     容器id
     * @return 插入后的wine命令，(目前只有一个元素）第二个往后是环境变量。包含原有和新增的环境变量
     */
    public static String insertArgsToWineCmd(String oriWineCmd, List<String> envList, int contId) {

        String wineCmd = oriWineCmd;

        //设置cpu核心（仅当符合要求时才插入：用户开启此设置；原cmd没有taskset参数；原cmd包含 wine 参数
        int wineIndex = setOtherArgv_findTargetIndexInCmd(wineCmd, "wine");
        String currCores = Arguments.all.get(0).getArg();
        if (!wineCmd.contains("taskset ") && !"".equals(currCores) && wineIndex != -1) {
            //将字符串插入命令行中
            String taskSetStr = "taskset -c " + currCores + " ";
            wineCmd = wineCmd.substring(0, wineIndex) + taskSetStr + wineCmd.substring(wineIndex);
        }

        //其他参数
        List<Argument> parsingList = new ArrayList<>(Arguments.all); //用于转换参数组为多个参数
        for (int argInd = 1; argInd < parsingList.size(); argInd++) {
            Argument preArg = parsingList.get(argInd);
            //如果是参数组，应该获取其包含的应该启用的子参数
            Argument arg = preArg.isGroup() ? preArg.getCheckedSubParamsInGroup() : preArg;
            if (arg == null || !arg.isChecked())
                continue;

            //环境变量（添加到ubtConfig的环境变量列表里。）
            if (TYPE_ENV.equals(arg.getArgType())) {
                //如果有重复的先删除。（虽然没什么必要，添加到末尾就直接覆盖前面的了。
                String header = arg.getArg().substring(0, arg.getArg().indexOf("=") + 1);
                for (int i = 0; i < envList.size(); i++) {
                    if (envList.get(i).startsWith(header)) {
                        envList.remove(i);
                        break;
                    }
                }
                envList.add(arg.getArg().trim());
            }

            //命令
            else if (TYPE_CMD.equals(arg.getArgType())) {
                //wine命令开头。仅当原wine命令不包含该命令时，才添加
                if (POS_FRONT.equals(arg.getArgPos())) {
                    if (setOtherArgv_findTargetIndexInCmd(wineCmd, arg.getArg()) == -1) {
                        int insertIndex = wineCmd.startsWith("eval \"") ? 6 : 0;
                        wineCmd = wineCmd.substring(0, insertIndex) + arg.getArg() + " " + wineCmd.substring(insertIndex);
                    }
                }
                //wine命令执行前
                else if (POS_EARLIER.equals(arg.getArgPos())) {
                    int insertIndex = wineCmd.startsWith("eval \"") ? 6 : 0;
                    wineCmd = wineCmd.substring(0, insertIndex) + arg.getArg() + " & " + wineCmd.substring(insertIndex);
                }
                //wine命令执行后
                else if (POS_LATER.equals(arg.getArgPos())) {
                    int insertIndex = wineCmd.startsWith("eval \"") ? wineCmd.length() - 1 : wineCmd.length();
                    wineCmd = wineCmd.substring(0, insertIndex) + " & " + arg.getArg() + wineCmd.substring(insertIndex);
                }
            }
        }

        return wineCmd;
    }


    /**
     * 在执行命令中找到目标字符串的起始位置.
     * 规则（以wine为例）：
     * 1. 开头就是  wine+空格。返回0
     * 2. 开头就是 eval[空格]"wine[空格] 返回6
     * 3. 否则寻找 空格+wine+空格，返回找到的位置（如果不是-1，则+1 跳过开头的空格）
     *
     * @param target  目标字符串 前后不要带空格
     * @param wineCmd 执行命令
     * @return wine的起始位置，或-1
     */
    private static int setOtherArgv_findTargetIndexInCmd(String wineCmd, String target) {
        target = target.trim();
        int findIndex = -1;
        if (wineCmd.startsWith(target + " ")) return 0;
        if (wineCmd.startsWith("eval \"" + target + " ")) return 6;
        findIndex = wineCmd.indexOf(" " + target + " ");
        if (findIndex != -1) findIndex++;
        return findIndex;
    }


    private static int[] getAvailableCpuCores() {

        try {
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                List<Integer> availableCores = new ArrayList<>();

                String proc = null;
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "getAvailableCpuCores: " + line);

                    //怎么突然正则匹配需要完全匹配了。。。末尾不加.* 前面都匹配也返回false。。。
                    if (line.trim().matches("^processor\\s*:.*")) {
                        proc = line.split(":", 2)[1].trim();
                    } else if (proc != null && line.trim().matches("^CPU architecture\\s*:.*")) {
                        int arch = Integer.parseInt(line.split(":", 2)[1].trim());
                        if (arch <= 8)
                            availableCores.add(Integer.parseInt(proc));
                        //找到了一个完整的proc和arch信息之后，置为null
                        proc = null;
                    }
                }
                Log.d(TAG, "getAvailableCpuCores: 总共读取的cpu核心：" + availableCores);
                int[] returnedArr = new int[availableCores.size()];
                for (int i = 0; i < returnedArr.length; i++)
                    returnedArr[i] = availableCores.get(i);
                return returnedArr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultAvailableCores;
        }
    }

}
