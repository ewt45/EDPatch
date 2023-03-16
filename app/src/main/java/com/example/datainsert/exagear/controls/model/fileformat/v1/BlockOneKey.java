package com.example.datainsert.exagear.controls.model.fileformat.v1;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockOneKey implements FormatHelper.Block<OneKey> {


    @Override
    public OneKey stringToObj(List<String> lines) {
        OneKey oneKey = new OneKey(0);
        int curLine = 0;
        String[] header = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
        Assert.isTrue(header[0].equals("OneKey") && header[1].equals(getVersion()));
        curLine++;
        //name，注意可能为空
        String[] nameLine = lines.get(curLine).split(FormatHelper.kvSeparator, 2); //限制分割次数，字符串啥样都有
        Assert.isTrue(nameLine[0].equals("name"));
        curLine++;
        oneKey.setName(nameLine.length == 1 ? "" : nameLine[1]);
        //其余属性都在一行里了
        String[] propertiesLine = lines.get(curLine).split(FormatHelper.propSeparator);
        //code
        oneKey.setCode(Integer.parseInt(propertiesLine[0].split(FormatHelper.kvSeparator)[1]));
        //mIsShow
        oneKey.setShow(Boolean.parseBoolean(propertiesLine[1].split(FormatHelper.kvSeparator)[1]));
        //marginLeft
        oneKey.setMarginLeft(Integer.parseInt(propertiesLine[2].split(FormatHelper.kvSeparator)[1]));
        //marginTop
        oneKey.setMarginTop(Integer.parseInt(propertiesLine[3].split(FormatHelper.kvSeparator)[1]));
        //mIsTrigger
        oneKey.setTrigger(Boolean.parseBoolean(propertiesLine[4].split(FormatHelper.kvSeparator)[1]));
        //subCodes 因为有多个，所以用逗号分隔一下
        String[] subCodesProp = propertiesLine[5].split(FormatHelper.kvSeparator);
        List<Integer> subCodes = new ArrayList<>();
        if (subCodesProp.length == 2) {
            for (String str : subCodesProp[1].split(FormatHelper.mulVSeparator)) {
                subCodes.add(Integer.valueOf(str));
            }
            oneKey.setSubCodes(subCodes);
        }
        return oneKey;
    }

    @Override
    public List<String> objToString(OneKey oneKey) {
        List<String> lists = new ArrayList<>();
        //name 字符串这种东西单独一行吧
        lists.add("name" + FormatHelper.kvSeparator + oneKey.getName());
        StringBuilder builder = new StringBuilder();
        //code
        builder.append("code").append(FormatHelper.kvSeparator).append(oneKey.getCode());
        //mIsShow
        builder.append(FormatHelper.propSeparator).append("mIsShow" + FormatHelper.kvSeparator).append(oneKey.isShow());
        //marginLeft
        builder.append(FormatHelper.propSeparator).append("marginLeft" + FormatHelper.kvSeparator).append(oneKey.getMarginLeft());
        //marginTop
        builder.append(FormatHelper.propSeparator).append("marginTop" + FormatHelper.kvSeparator).append(oneKey.getMarginTop());
        //mIsTrigger
        builder.append(FormatHelper.propSeparator).append("mIsTrigger" + FormatHelper.kvSeparator).append(oneKey.isTrigger());
        //subCodes 因为有多个，所以用逗号分隔一下
        StringBuilder subCodesBuilder = new StringBuilder();
        for (int subCode : oneKey.getSubCodes()) {
            subCodesBuilder.append(subCode).append(FormatHelper.mulVSeparator);
        }
        if (subCodesBuilder.length() > 0)
            subCodesBuilder.deleteCharAt(subCodesBuilder.length() - 1);
        builder.append(FormatHelper.propSeparator).append("subCodes" + FormatHelper.kvSeparator).append(subCodesBuilder);

        lists.add(builder.toString());
        lists.add(0, FormatHelper.blockPrefix + "OneKey" + FormatHelper.propSeparator + getVersion() + FormatHelper.propSeparator + lists.size());

        return lists;
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
