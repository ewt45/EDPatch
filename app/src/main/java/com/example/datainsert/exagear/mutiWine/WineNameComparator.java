package com.example.datainsert.exagear.mutiWine;

import java.util.Comparator;

/**
 * 根据wine名称对列表进行排序
 * 比较WineInfo，通过其toString方法来对比，所以需要重写其toString方法并返回类似tagName格式的字符串以便比较
 */
public class WineNameComparator implements Comparator<Object> {

    /**
     * 数字字符串转数字数组
     * 适配 1.25.3.5 这种情况 ，同时如果不不包含小数点【整数情况】
     *
     * @return
     */
    private static String[] numberStrToNumberArray(String numberStr) {
        // 按小数点分割字符串数组
        String[] numberArray = numberStr.split("\\.");
        // 长度为0说明没有小数点，则整个字符串作为第一个元素
        if (numberArray.length == 0) {
            numberArray = new String[]{numberStr};
        }
        return numberArray;

    }

    /**
     * 比较两个数字数组
     *
     * @param numberArray1
     * @param numberArray2
     * @return
     */
    private static int compareNumberArray(String[] numberArray1, String[] numberArray2) {
        for (int i = 0; i < numberArray1.length; i++) {
            if (numberArray2.length < i + 1) { // 此时数字数组2比1短，直接返回
                return 1;
            }
            int compareResult = Integer.valueOf(numberArray1[i]).compareTo(Integer.valueOf(numberArray2[i]));
            if (compareResult != 0) {
                return compareResult;
            }
        }
        // 说明数组1比数组2短，返回小于
        return -1;
    }


    @Override
    public int compare(Object o1, Object o2) {
        String str1 = o1.toString();
        String str2 = o2.toString();
        // 处理数据为null的情况
        // 比较字符串中的每个字符
        char c1;
        char c2;
        // 逐字比较返回结果
        for (int i = 0; i < str1.length(); i++) {
            c1 = str1.charAt(i);
            try {
                c2 = str2.charAt(i);
            } catch (StringIndexOutOfBoundsException e) { // 如果在该字符前，两个串都一样，str2更短，则str1较大
                return 1;
            }
            // 如果都是数字的话，则需要考虑多位数的情况，取出完整的数字字符串，转化为数字再进行比较
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                StringBuilder numStr1 = new StringBuilder();
                StringBuilder numStr2 = new StringBuilder();
                // 获取数字部分字符串
                for (int j = i; j < str1.length(); j++) {
                    c1 = str1.charAt(j);
                    if (!Character.isDigit(c1) && c1 != '.') { // 不是数字则直接退出循环
                        break;
                    }
                    numStr1.append(c1);
                }
                for (int j = i; j < str2.length(); j++) {
                    c2 = str2.charAt(j);
                    if (!Character.isDigit(c2) && c2 != '.') { // 考虑可能带小数的情况
                        break;
                    }
                    numStr2.append(c2);
                }
                // 转换成数字数组进行比较 适配 1.25.3.5 这种情况
                String[] numberArray1 = numberStrToNumberArray(numStr1.toString());
                String[] numberArray2 = numberStrToNumberArray(numStr2.toString());
                return compareNumberArray(numberArray1, numberArray2);
            }

            // 不是数字的比较方式
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return 0;
    }
}
