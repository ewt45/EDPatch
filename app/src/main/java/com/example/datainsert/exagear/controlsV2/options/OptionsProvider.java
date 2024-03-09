package com.example.datainsert.exagear.controlsV2.options;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

//TODO 日后想办法实现一个接口，让应用可以自己实现对应action，比如显示输入法，关闭xserver，更多自定义操作等

/**
 * 提供flag OPTION_xxx，从外部获取自定义的option
 */
public class OptionsProvider {
//    public static final int OPTION_EMPTY = 0;
    public static final int OPTION_SHOW_SOFT_INPUT = 1;
    public static final int OPTION_SHOW_ALL_OPTIONS = 2;
    public static final int[] optionsInt = {OPTION_SHOW_SOFT_INPUT,OPTION_SHOW_ALL_OPTIONS};
    public static final String[] optionsName;
    /**
     * 初始时必须将全部OPTION放入map中，没有的就放EMPTY
     */
    private static final Map<Integer, AbstractOption> optionsLoader = new HashMap<>();
    //TODO 要不直接存实例吧，然后名字应该实时获取，因为有的涉及到显示隐藏的


    static {
//        optionsLoader.put(OPTION_EMPTY, EMPTY_OPTION.class);
        optionsLoader.put(OPTION_SHOW_SOFT_INPUT, new OptionToggleSoftInput());
        optionsLoader.put(OPTION_SHOW_ALL_OPTIONS, new OptionShowAllOptions());

        optionsName = new String[optionsInt.length];
        for(int i=0; i< optionsInt.length; i++){
            optionsName[i] = getOption(optionsInt[i]).getName();
        }
    }

    public static AbstractOption getOption(@OptionType int optionType) {
        return optionsLoader.get(optionType);
//        Class<? extends AbstractOption> clz = optionsLoader.get(optionType);
//        try {
//            assert clz != null;
//            return clz.newInstance();
//        } catch (IllegalAccessException | InstantiationException e) {
//            return new EMPTY_OPTION();
//        }
    }

    @IntDef({ OPTION_SHOW_SOFT_INPUT,OPTION_SHOW_ALL_OPTIONS})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OptionType {
    }

    public static class EMPTY_OPTION extends AbstractOption {

        @Override
        public void run() {

        }
    }
}
