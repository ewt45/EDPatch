package com.example.datainsert.exagear.controls.model;


import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockJoyParams;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockKeyCodes2;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockKeyCodes3;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockOneCol;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockOneKey;
import com.example.datainsert.exagear.controls.model.fileformat.v1.BlockPref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatHelper {

    //不知道会不会有换行问题。统一用这个吧
    public static final String lineSeparator = "\n";
    public static final String kvSeparator = "="; //键值对的分割符
    public static final String propSeparator = ";"; //每个属性间的分隔符（一行里有多个属性的话用分号分隔）
    public static final String mulVSeparator = ","; //一个键对应多个值，每个值用逗号分隔
    public static final String blockPrefix = "@";  //属于一个模块的标识行,接下来是该模块的多行。信息有：标识名，版本号，行数
    public static final String blockSubPrefix = "$"; //属于一个模块内的某个属性的表示行，接下来是该属性的多行。信息有：标识名 行数


    //在这里配置当前模块解析用到的具体类
    static Map<String, Block<KeyCodes2>> keyCodes2BlockMap = new HashMap<>();
    static Map<String, Block<KeyCodes3>> keyCodes3BlockMap = new HashMap<>();
    static Block<Object> prefBlock = new BlockPref();

    static {
        //OneKey
        Map<String, Block<OneKey>> oneKeyBlockMap = new HashMap<>();
        Block<OneKey> oneKeyBlock = new BlockOneKey();
        oneKeyBlockMap.put(oneKeyBlock.getVersion(), oneKeyBlock);
        //JoyParams
        Map<String, Block<JoyParams>> joyParamsBlockMap = new HashMap<>();
        Block<JoyParams> joyParamsBlock = new BlockJoyParams();
        joyParamsBlockMap.put(joyParamsBlock.getVersion(), joyParamsBlock);
        //OneBlock
        Map<String, Block<OneCol>> oneColBlockMap = new HashMap<>();
        Block<OneCol> oneColBlock = new BlockOneCol(oneKeyBlockMap);
        oneColBlockMap.put(oneColBlock.getVersion(), oneColBlock);
        //KeyCodes2
        Block<KeyCodes2> keyCodes2Block = new BlockKeyCodes2(oneColBlockMap);
        keyCodes2BlockMap.put(keyCodes2Block.getVersion(), keyCodes2Block);
        //KeyCodes3
        Block<KeyCodes3> keyCodes3Block = new BlockKeyCodes3(oneKeyBlockMap, joyParamsBlockMap);
        keyCodes3BlockMap.put(keyCodes3Block.getVersion(), keyCodes3Block);
    }


    //To-do : 现在还缺一个版本检查

    protected static List<String> keyCodes2ToString(KeyCodes2 keyCodes2) {
        //从map中获取最新版本的Block
        List<String> keyList = new ArrayList<>(keyCodes2BlockMap.keySet());
        Block<KeyCodes2> keyCodes2Block = keyCodes2BlockMap.get(keyList.get(keyList.size() - 1));
        assert keyCodes2Block != null;
        return keyCodes2Block.objToString(keyCodes2);
    }

    protected static KeyCodes2 stringToKeyCodes2(List<String> lines) {
        if (lines.size() == 0)
            return new KeyCodes2();
        String[] header = lines.get(0).substring(1).split(propSeparator);
        Assert.isTrue(header[0].equals("KeyCodes2"));
        Block<KeyCodes2> keyCodes2Block = keyCodes2BlockMap.get(header[1]);
        if (keyCodes2Block != null)
            return keyCodes2Block.stringToObj(lines);
        else return new KeyCodes2();
    }

    protected static List<String> keyCodes3ToString(KeyCodes3 keyCodes3) {
        List<String> keyList = new ArrayList<>(keyCodes3BlockMap.keySet());
        Block<KeyCodes3> keyCodes3Block = keyCodes3BlockMap.get(keyList.get(keyList.size() - 1));
        assert keyCodes3Block != null;
        return keyCodes3Block.objToString(keyCodes3);
    }

    protected static KeyCodes3 stringToKeyCodes3(List<String> lines) {
        if (lines.size() == 0)
            return new KeyCodes3();
        String[] header = lines.get(0).substring(1).split(propSeparator);
        Assert.isTrue(header[0].equals("KeyCodes3"));
        Block<KeyCodes3> keyCodes3Block = keyCodes3BlockMap.get(header[1]);
        if (keyCodes3Block != null)
            return keyCodes3Block.stringToObj(lines);
        else return new KeyCodes3();
    }


    /**
     * 将全部数据导入（写入本地），从剪切板获取文本，
     * 需要退出XServerDisplayActivity后再使用这个方法，否则会导致导入失败
     */
    public static void dataImport(String data) {
        List<String> linesArray = Arrays.asList(data.split(lineSeparator));
        int curLine = 0;

        while (curLine < linesArray.size()) {
            String[] header = linesArray.get(curLine).substring(1).split(propSeparator);
            curLine++;

            switch (header[0]) {
                case "Pref":
                    prefBlock.stringToObj(linesArray.subList(curLine - 1, curLine + Integer.parseInt(header[2])));
                    break;
                case "KeyCodes2": {
                    List<String> keyList = new ArrayList<>(keyCodes2BlockMap.keySet());
                    Block<KeyCodes2> keyCodes2Block = keyCodes2BlockMap.get(keyList.get(keyList.size() - 1));
                    if (keyCodes2Block != null) {
                        KeyCodes2 keyCodes2 = keyCodes2Block.stringToObj(linesArray.subList(curLine - 1, curLine + Integer.parseInt(header[2])));
                        KeyCodes2.write(keyCodes2, Globals.getAppContext());//写入本地
                    }
                    break;
                }
                case "KeyCodes3": {
                    List<String> keyList = new ArrayList<>(keyCodes3BlockMap.keySet());
                    Block<KeyCodes3> keyCodes3Block = keyCodes3BlockMap.get(keyList.get(keyList.size() - 1));
                    if (keyCodes3Block != null) {
                        KeyCodes3 keyCodes3 = keyCodes3Block.stringToObj(linesArray.subList(curLine - 1, curLine + Integer.parseInt(header[2])));
                        KeyCodes3.write(keyCodes3, Globals.getAppContext());//写入本地
                    }
                    break;
                }
            }
            curLine += Integer.parseInt(header[2]);
        }

    }

    /**
     * 将全部数据导出为文本，复制到剪切板
     *
     * @param keyCodes2
     * @param keyCodes3
     * @return
     */
    public static String dataExport(KeyCodes2 keyCodes2, KeyCodes3 keyCodes3) {
        List<String> keyList = new ArrayList<>(keyCodes2BlockMap.keySet());
        Block<KeyCodes2> keyCodes2Block = keyCodes2BlockMap.get(keyList.get(keyList.size() - 1));
        assert keyCodes2Block != null;

        List<String> keyList2 = new ArrayList<>(keyCodes3BlockMap.keySet());
        Block<KeyCodes3> keyCodes3Block = keyCodes3BlockMap.get(keyList2.get(keyList2.size() - 1));
        assert keyCodes3Block != null;


        List<String> threePartLists = new ArrayList<>();
        threePartLists.addAll(prefBlock.objToString(null));
        threePartLists.addAll(keyCodes2Block.objToString(keyCodes2));
        threePartLists.addAll(keyCodes3Block.objToString(keyCodes3));

        //转换为str
        StringBuilder builder = new StringBuilder();
        for (String str : threePartLists) {
            builder.append(str).append(lineSeparator);
        }
        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    /**
     * 更新版本的话新建模块，更新版本
     * <p>
     * 成员变量：
     * - 一个map，里面装了对应的模块和版本号（字符串格式）。如果本Block的某个属性需要调用子Block处理，那么这个Block就应该加到这个map里。
     * 读取字符串转为obj时，自身属性格式是固定的无需从map里寻找，对于需要用到子Block的地方尝试从map中通过版本号获取对应的Block
     * 如果升级了，给对应Block的map中添加一项即可
     * 写入到字符串时，获取map中最新的Block
     *
     * @param <T>
     */
    public interface Block<T> {
        /**
         * 如果开头不是@或$，就是多个属性.
         * 接收的列表首行应该是模块标识行，除非列表长度为0
         */
        public T stringToObj(List<String> lines);

        /**
         * 将obj转为字符串。并在首行添加块信息(标识名，版本号，行数（不包括标识行））
         */
        public List<String> objToString(T t);

        /**
         * 获取自身版本号。用于判断应该使用哪个实现类
         *
         * @return
         */
        public String getVersion();
    }


}
