package com.example.datainsert.exagear.controls.model.fileformat.v1;

import static com.example.datainsert.exagear.controls.model.FormatHelper.blockPrefix;
import static com.example.datainsert.exagear.controls.model.FormatHelper.blockSubPrefix;
import static com.example.datainsert.exagear.controls.model.FormatHelper.propSeparator;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.FormatHelper.Block;
import com.example.datainsert.exagear.controls.model.JoyParams;
import com.example.datainsert.exagear.controls.model.KeyCodes3;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockKeyCodes3 implements Block<KeyCodes3> {
    //    Block<OneKey> oneKeyBlock;
//    Block<JoyParams> joyParamsBlock;
    Map<String, Block<OneKey>> oneKeyBlockMap;
    Map<String, Block<JoyParams>> joyParamsBlockMap;

    public BlockKeyCodes3(Map<String, Block<OneKey>> blockOneKey, Map<String, Block<JoyParams>> blockJoyParams) {
        this.oneKeyBlockMap = blockOneKey;
        this.joyParamsBlockMap = blockJoyParams;
    }

    @Override
    public KeyCodes3 stringToObj(List<String> lines) {
        KeyCodes3 keyCodes3 = new KeyCodes3();
        keyCodes3.getKeyList().clear();//这里会自动创建全部code的按键。先清空
        keyCodes3.getJoyList().clear();
        int curLine = 0;
        String[] header = lines.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(header[0].equals("KeyCodes3") && header[1].equals(getVersion()));
        curLine++;

        //mKeyList
        String[] keyHeader = lines.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(keyHeader[0].equals("mKeyList"));
        curLine++;
        List<String> keyLines = lines.subList(curLine, curLine + Integer.parseInt(keyHeader[1]));
        curLine += Integer.parseInt(keyHeader[1]);
        int keyCurLine = 0;
        while (keyCurLine < keyLines.size()) {
            String[] oneKeyHeader = keyLines.get(keyCurLine).substring(1).split(propSeparator);
            Assert.isTrue(oneKeyHeader[0].equals("OneKey"));
            keyCurLine++;//需要把首行也传下去，截取列表记得-1
            Block<OneKey> oneKeyBlock = oneKeyBlockMap.get(oneKeyHeader[1]);
            if (oneKeyBlock != null) {
                List<String> oneKeyLines = keyLines.subList(keyCurLine - 1, keyCurLine + Integer.parseInt(oneKeyHeader[2]));
                keyCodes3.getKeyList().add(oneKeyBlock.stringToObj(oneKeyLines));
            }
            keyCurLine += Integer.parseInt(oneKeyHeader[2]);//索引1是版本号，2才是行数
        }

        //mJoyList
        String[] joyHeader = lines.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(joyHeader[0].equals("mJoyList"));
        curLine++;
        List<String> joyLines = lines.subList(curLine, curLine + Integer.parseInt(joyHeader[1]));
        curLine += Integer.parseInt(joyHeader[1]);
        int joyCurLine = 0;
        while (joyCurLine < joyLines.size()) {
            String[] joyParamsHeader = joyLines.get(joyCurLine).substring(1).split(propSeparator);
            Assert.isTrue(joyParamsHeader[0].equals("JoyParams"));
            joyCurLine++;
            Block<JoyParams> joyParamsBlock = joyParamsBlockMap.get(joyParamsHeader[1]);
            if (joyParamsBlock != null) {
                List<String> joyParamsLines = joyLines.subList(joyCurLine - 1, joyCurLine + Integer.parseInt(joyParamsHeader[2]));
                keyCodes3.getJoyList().add(joyParamsBlock.stringToObj(joyParamsLines));
            }
            joyCurLine += Integer.parseInt(joyParamsHeader[2]);
        }
        return keyCodes3;
    }

    @Override
    public List<String> objToString(KeyCodes3 keyCodes3) {


        //从map中获取最新版本的Block
        List<String> oneKeyMapKeys = new ArrayList<>(oneKeyBlockMap.keySet());
        Block<OneKey> oneKeyBlock = oneKeyBlockMap.get(oneKeyMapKeys.get(oneKeyMapKeys.size()-1));
        assert  oneKeyBlock!=null;


        //从map中获取最新版本的Block
        List<String> joyParamsMapKeys = new ArrayList<>(joyParamsBlockMap.keySet());
        Block<JoyParams> joyParamsBlock = joyParamsBlockMap.get(joyParamsMapKeys.get(joyParamsMapKeys.size() - 1));
        assert joyParamsBlock != null;

        List<String> lines = new ArrayList<>();
        //mKeyList
        List<String> keyLines = new ArrayList<>();
        for (OneKey oneKey : keyCodes3.getKeyList()) {
            keyLines.addAll(oneKeyBlock.objToString(oneKey));
        }
        keyLines.add(0, blockSubPrefix + "mKeyList" + propSeparator + keyLines.size());
        //mJoyList
        List<String> joyLines = new ArrayList<>();
        for (JoyParams joyParams : keyCodes3.getJoyList()) {
            joyLines.addAll(joyParamsBlock.objToString(joyParams));
        }
        joyLines.add(0, blockSubPrefix + "mJoyList" + propSeparator + joyLines.size());

        lines.addAll(keyLines);
        lines.addAll(joyLines);
        lines.add(0, blockPrefix + "KeyCodes3" + propSeparator + getVersion() + propSeparator + lines.size());
        return lines;
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
