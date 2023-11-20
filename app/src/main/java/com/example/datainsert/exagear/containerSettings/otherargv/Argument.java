package com.example.datainsert.exagear.containerSettings.otherargv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Argument {
    public static  String TYPE_ENV = "env", TYPE_CMD = "cmd", TYPE_GROUP="group";
    public static  String POS_ENV = "env", POS_FRONT = "front", POS_EARLIER = "earlier", POS_LATER = "later";
    private boolean mIsEnableByDefault;
    private String mArgType;
    private String mArgPos;
    private String mArg;
    private String mAlias = "";
    private boolean mIsChecked = false; //当前容器是否勾选此参数
    /**
     * 存储一组参数，只能勾选一个。若元素个数不为0，则说明应该分组
     * 参数组的大部分成员变量没有用，比如具体的Arg，都是从子参数中获取
     */
    private final List<Argument> mGroup = new ArrayList<>();

    public Argument(String enableByDefault, String argType, String argPos, String alias, String arg) {
        mIsEnableByDefault = enableByDefault.equals("e");
        mArgType = argType;
        mArgPos = argPos;
        mArg = arg;
        mAlias = alias.replace(" ","");
    }


    public List<Argument> getGroup() {
        return mGroup;
    }

    /**
     * @return 自定义的别名。若为空则会返回参数本身。若为参数组，则返回---分割的前半部分
     */
    public String getAlias() {
        if(mAlias.trim().length()==0)
            return mArg;

        return mAlias.trim().length()==0?mArg:mAlias;
    }



    public void setAlias(String mAlias) {
        this.mAlias = mAlias;
    }

    public void setArg(String mArg) {
        this.mArg = mArg;
    }

    /**
     * 单参数，返回自身参数。<br />
     * 参数组，返回第一个找到的被勾选的参数，若没有勾选任何参数，则返回第一个参数。<br/>
     * 若为参数组，推荐使用getCheckedSubParamsInGroup获取勾选的参数对象（不过此函数返回的参数应该与isChecked的对应是同一个参数）
     */
    public String getArg() {
        if(!isGroup())
            return mArg;

        //检查子参数是否有勾选的，如果有就用它
        for(Argument arg:mGroup)
            if(arg.isChecked())
                return arg.mArg;

        return  mGroup.get(0).getArg();
    }

    /**
     * 返回参数组中，启动容器是时应该是用的参数选项。若为null，则说明不启用任何选项 <br />
     * 若非参数组调用此方法，会抛出异常
     */
    public @Nullable Argument getCheckedSubParamsInGroup(){
        if(!isGroup())
            throw new RuntimeException("不是参数组不能调用这个函数");

        //先检查子参数是否有勾选的，如果有就用它
        for(Argument arg:mGroup)
            if(arg.isChecked())
                return arg;

        return null;
    }

    public String getArgType() {
        return mArgType;
    }

    public void setArgType(String ArgType) {
        this.mArgType = ArgType;
    }

    public String getArgPos() {
        return mArgPos;
    }

    public void setArgPos(String mArgPos) {
        this.mArgPos = mArgPos;
    }

    /**
     * @return 自身被勾选，或参数组中至少有一个被勾选
     */
    public boolean isChecked() {
        boolean isAnyChecked = mIsChecked;
        int cnt = 0;
        while (!isAnyChecked && cnt<mGroup.size()){
            isAnyChecked = mGroup.get(cnt).isChecked();
            cnt++;
        }
        return mIsChecked;
    }

    public boolean isGroup(){
        return mGroup.size()>0;
    }

    public void setChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

    /**
     * 创建Argument实例时，已经将默认启用的情况考虑到check中，所以在启动容器时，不应调用该方法，只需检查是否check即可。 <br/>
     * 如果是参数组，如果至少有一个自参数为true，则返回true。全false则返回false。注意如果有多个true，默认会开启第一个为true的子参数。
     *
     */
    public boolean isEnableByDefault() {
        //TODO 解决参数组中可能有多个默认启用的问题（从文本中读取时修正？新建Argument时修正？添加到group时修正？）
        if(!isGroup())
            return mIsEnableByDefault;

        for(Argument arg:mGroup)
            if(arg.isEnableByDefault())
                return true;

        return false;
    }

    public void setEnableByDefault(boolean IsEnableByDefault) {
        this.mIsEnableByDefault = IsEnableByDefault;
    }



    @NonNull
    @Override
    public String toString() {
        if(!isGroup())
            return (mIsEnableByDefault ? "e " : "d ") + mArgType + " " + mArgPos + " "
                + mAlias + " " + mArg;
        else{
            StringBuilder builder = new StringBuilder();
            for(Argument arg:mGroup)
                builder.append(arg).append('\n');
            return builder.deleteCharAt(builder.length()-1).toString();//至少有一个，所以最后一个肯定是换行
        }
    }

}
