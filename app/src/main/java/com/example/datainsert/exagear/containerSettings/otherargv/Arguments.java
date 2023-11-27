package com.example.datainsert.exagear.containerSettings.otherargv;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.containerSettings.ConSetOtherArgv.KEY_TASKSET;
import static com.example.datainsert.exagear.containerSettings.ConSetOtherArgv.VAL_TASKSET_DEFAULT;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_FRONT;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.TYPE_CMD;

import android.view.ViewGroup;
import android.view.ViewParent;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Argument的helper类
 */
public class Arguments {
    /**
     * 全部参数库。存在edpatch/contArgs.txt。可以用allFromFile 和allToFile读取或保存
     */
    public final static List<Argument> all = new ArrayList<>();
    private final static File mPoolFile = new File(QH.Files.edPatchDir(), "contArgs.txt");

    private static String DEFAULT_POOL = "" +
            "d cmd front " + getS(RR.othArg_ib_autorun) + " ib\n" +
            "#d cmd earlier wine之前执行脚本 simple.sh\n" +
            "d cmd later " + getS(RR.othArg_serviceExeDisable) + " wine taskkill /f /im services.exe\n" +
            "d cmd later parallel_run_some_exe_test wine explorer /desktop=shell taskmgr\n" +
            "d env env MESA_GL_VERSION_OVERRIDE---2.1 MESA_GL_VERSION_OVERRIDE=2.1\n" +
            "d env env MESA_GL_VERSION_OVERRIDE---3.3 MESA_GL_VERSION_OVERRIDE=3.3\n" +
            "d env env MESA_GL_VERSION_OVERRIDE---3.3COMPAT MESA_GL_VERSION_OVERRIDE=3.3COMPAT\n" +
            "d env env MESA_GL_VERSION_OVERRIDE---4.6 MESA_GL_VERSION_OVERRIDE=4.6\n" +
            "#d env env timezone---Asia/Tokyo TZ=Asia/Tokyo\n" +
            "#d env env timezone---Asia/Shanghai TZ=Asia/Shanghai";

    /**
     * 编辑容器参数，关闭dialog时，将all中已勾选的选项存入/home/xroid_n/contArgs.txt中<br/>
     * 调用此函数时，应保证all的第一个参数不是cpu核心设置。
     */
    private static void checkedToContFile(int contId) {
        List<String> lines = new ArrayList<>();
        for (Argument arg : all) {
            Argument realArg = arg.isGroup() ? arg.getCheckedSubParamsInGroup() : arg;
            if (realArg != null && realArg.isChecked())
                lines.add(realArg.toString());
        }
        try {
            FileUtils.writeLines(getArgsContTxt(contId), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取指定容器已启用的参数列表存入的txt
     */
    public static File getArgsContTxt(int contId) {
        return new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "home/xdroid_" + contId + "/contArgs.txt");

    }

    /**
     * 从edpatch/contArgs.txt读取函数库全部参数，存入all。若txt不存在，则会生成一个。
     * 然后从/home/xdroid_n/contArgs.txt中读取当前容器启用的参数，标记all中的isChecked
     */
    public static void allFromPoolFile(int contId) {
        all.clear();
        try {
            File contArgsFile = getArgsContTxt(contId);
            List<String> contArgsList = !contArgsFile.exists() ? null : FileUtils.readLines(contArgsFile);

            if (!mPoolFile.exists())
                FileUtils.writeStringToFile(mPoolFile, DEFAULT_POOL);

            List<String> lines = FileUtils.readLines(mPoolFile);
            for (String line : lines) {
                if (line.trim().startsWith("#") || line.trim().length() == 0)
                    continue;

                String[] splits = line.trim().split(" ", 5);
                Argument newArg = new Argument(splits[0], splits[1], splits[2], splits[3], splits[4]);
                //若容器的参数列表还不存在，则是否勾选等于默认是否启用。然后全部设置完之后再生成容器参数列表txt
                newArg.setChecked(contArgsList == null
                        ? newArg.isEnableByDefault()
                        : contArgsList.contains(line));

                //和现有参数组成参数组/加入已有参数组, 或者作为单参数加到末尾
                if (addArgAsGroup(all, newArg) == -1)
                    all.add(newArg);
            }

            //若不存在容器参数列表，就生成一个
            if (contArgsList == null)
                checkedToContFile(contId);

            //最后的最后把cpu核心设置 添加到第一项 (保存时也要注意，要不就直接从arg读取taskset的值吧，然后存到pref里）
            all.add(0, getCPUCoresArg(contId));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 想向参数组中添加一个新的参数时，首先调用此函数尝试加入参数组。<br/>
     * 若能加入参数组，则会插入到原先的兄弟参数或参数组的位置。<br/>
     * 若不能加入参数组，则不会加入list中，需要再手动插入。
     *
     * @param list   当前全部参数
     * @param newArg 新添加的参数
     * @return 找到了合适的参数组并插入到列表中：插入位置，没找到：-1
     */
    public static int addArgAsGroup(List<Argument> list, Argument newArg) {
        String[] aliasSplits = newArg.getAlias().split("---", 2);
        int insertedPos = -1;
        //若能用---分割出两部分且长度均不为0，说明可能存在同名的参数。需要遍历列表
        if (aliasSplits.length == 2 && aliasSplits[0].length() > 0 && aliasSplits[1].length() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String oneAlias = list.get(i).getAlias();
                if (oneAlias.startsWith(aliasSplits[0] + "---")) {
                    Argument brother = list.remove(i);
                    Argument group = new Argument("d", TYPE_CMD, POS_FRONT, aliasSplits[0], "group_argument_placeholder");
                    Collections.addAll(group.getGroup(), brother, newArg);
                    list.add(i, group);
                    setOnly1CheckedInGroup(group.getGroup());
                    insertedPos = i;
                    break;
                } else if (oneAlias.equals(aliasSplits[0])) {
                    list.get(i).getGroup().add(newArg);
                    setOnly1CheckedInGroup(list.get(i).getGroup());
                    insertedPos = i;
                    break;
                }
            }
        }


        return insertedPos;
    }

    /**
     * 从列表中，某一个参数组中移除一个参数。移除后会检查该参数组是否只剩一个子参数，因为此时应该退化为单参数。<br/>
     * adapterPos对应的必须为参数组
     * @param mData 包含参数组的列表
     * @param adapterPos 参数组的序号
     * @param subInd 要删除的子参数在参数组中的序号
     */
    public static void removeArgFromGroup(List<Argument> mData, int adapterPos, int subInd) {
        Argument group = mData.get(adapterPos);
        group.getGroup().remove(subInd);

        //参数组最少子参数个数为2，所以移除后最少为1，此时退化为单参数
        if(group.getGroup().size()==1){
            Argument onlyOne = group.getGroup().remove(0);
            inflateArgument(group,onlyOne.isEnableByDefault()?"e":"d",onlyOne.getArgType(),onlyOne.getArgPos(),onlyOne.getAlias(),onlyOne.getArg());
            group.setChecked(onlyOne.isChecked());
        }
    }

    /**
     * 如果是参数组，返回去除掉相同名称部分后的子参数别名。否则返回正常别名
     */
    public static String getRippedAlias(Argument group, Argument sub) {
        String headerStr = group.getAlias() + "---";
        if (sub.getAlias().startsWith(headerStr))
            return sub.getAlias().replace(headerStr, "");
        else
            return sub.getAlias();
    }

    /**
     * 让参数组中，保留仅一个check的参数。
     * 优先保留后面的，前面设置成false。
     */
    private static void setOnly1CheckedInGroup(List<Argument> arguments) {
        boolean hasCheckedEarlier = false;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            Argument argument = arguments.get(i);
            if (hasCheckedEarlier)
                argument.setChecked(false);
            else hasCheckedEarlier = argument.isChecked();
        }
    }

    /**
     * 将cpu核心数包装为Argument形式。
     */
    private static Argument getCPUCoresArg(int contId) {
        String currArg = QH.getContPref(contId).getString(KEY_TASKSET, VAL_TASKSET_DEFAULT);
        return new Argument("e", TYPE_CMD, POS_FRONT, getS(RR.othArg_taskset_useCustom), currArg) {
            @Override
            public boolean isEnableByDefault() {
                return VAL_TASKSET_DEFAULT.length() == 0; //是否默认启用，取决于其单独设置的默认值
            }

            @Override
            public String getAlias() {
                return super.getAlias();
            }

            @Override
            public boolean isChecked() {
                return getArg().length() > 0;
            }
        };
    }

    /**
     * all中第一项必定为cpu核心数设置，这个写入容器设置的pref。
     * 其余全部参数存入edpatch/contArgs.txt。
     * 然后把勾选的参数存入容器的参数txt
     */
    public static void allToPoolFile(int contId) {
        try {
            assert all.size() > 0;
            //cpu核心数存入容器pref
            Argument cpuCoresArg = all.remove(0);
            QH.getContPref(contId).edit().putString(KEY_TASKSET, cpuCoresArg.getArg()).apply();

            //全部参数存入参数库
            FileUtils.writeLines(mPoolFile, all);

            //勾选参数存入容器参数txt
            checkedToContFile(contId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向Argument对象填充数据，并返回。若old为null，则生成一个新对象
     * @param old 需要修改属性的arg对象，或null
     * @param p 构造一个新Argument所需参数
     * @return 填充数据后的arg对象
     */
    public static Argument inflateArgument(Argument old,String... p){
        Argument newArg = old==null?new Argument(p[0],p[1],p[2],p[3],p[4]):old;
        newArg.setEnableByDefault(p[0].equals("e"));
        newArg.setArgType(p[1]);
        newArg.setArgPos(p[2]);
        newArg.setAlias(p[3]);
        newArg.setArg(p[4]);
        return newArg;
    }


}
