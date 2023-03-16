package com.example.datainsert.exagear.controls.model.fileformat.v1;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.JoyParams;

import java.util.ArrayList;
import java.util.List;

public class BlockJoyParams implements FormatHelper.Block<JoyParams> {

    @Override
    public JoyParams stringToObj(List<String> lines) {
        JoyParams joyParams = new JoyParams();
        int curLine = 0;
        String[] header = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
        Assert.isTrue(header[0].equals("JoyParams") && header[1].equals(getVersion()));
        curLine++;
        //属性都在一行里
        String[] properties = lines.get(curLine).split(FormatHelper.propSeparator);
        //key4Directions
        String[] key4DStr = properties[0].split(FormatHelper.kvSeparator)[1].split(FormatHelper.mulVSeparator);
        joyParams.setKey4Directions(new int[]{
                Integer.parseInt(key4DStr[0]),
                Integer.parseInt(key4DStr[1]),
                Integer.parseInt(key4DStr[2]),
                Integer.parseInt(key4DStr[3])});
        //presetKey
        joyParams.setPresetKey(JoyParams.PresetKey.valueOf(properties[1].split(FormatHelper.kvSeparator)[1]));
        //marginLeft
        joyParams.setMarginLeft(Integer.parseInt(properties[2].split(FormatHelper.kvSeparator)[1]));
        //marginTop
        joyParams.setMarginTop(Integer.parseInt(properties[3].split(FormatHelper.kvSeparator)[1]));
        //isFourDirections
        joyParams.setFourDirections(Boolean.parseBoolean(properties[4].split(FormatHelper.kvSeparator)[1]));
        return joyParams;
    }

    @Override
    public List<String> objToString(JoyParams joyParams) {
        StringBuilder builder = new StringBuilder();
        List<String> lines = new ArrayList<>();
        //key4Directions
        builder.append("key4Directions").append(FormatHelper.kvSeparator)
                .append(joyParams.getKey4Directions()[0]).append(FormatHelper.mulVSeparator)
                .append(joyParams.getKey4Directions()[1]).append(FormatHelper.mulVSeparator)
                .append(joyParams.getKey4Directions()[2]).append(FormatHelper.mulVSeparator)
                .append(joyParams.getKey4Directions()[3]);
        //presetKey
        builder.append(FormatHelper.propSeparator).append("presetKey").append(FormatHelper.kvSeparator).append(joyParams.getPresetKey());
        //marginLeft
        builder.append(FormatHelper.propSeparator).append("marginLeft").append(FormatHelper.kvSeparator).append(joyParams.getMarginLeft());
        //marginTop
        builder.append(FormatHelper.propSeparator).append("marginTop").append(FormatHelper.kvSeparator).append(joyParams.getMarginTop());
        //isFourDirections
        builder.append(FormatHelper.propSeparator).append("isFourDirections").append(FormatHelper.kvSeparator).append(joyParams.isFourDirections());

        lines.add(builder.toString());
        lines.add(0, FormatHelper.blockPrefix + "JoyParams" + FormatHelper.propSeparator + getVersion() + FormatHelper.propSeparator + lines.size());
        return lines;
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
