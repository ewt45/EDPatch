package com.example.datainsert.exagear.controls.model;


import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ALPHA;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ROUND_SHAPE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_TXT_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN__TXT_SIZE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_OFFWINDOW_DISTANCE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_SENSITIVITY;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.ControlsResolver;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatHelper {

    //不知道会不会有换行问题。统一用这个吧
    public static final String lineSeparator = "\n";
    public static final String kvSeparator = "="; //键值对的分割符
    public static final String propSeparator = ";"; //每个属性间的分隔符（一行里有多个属性的话用分号分隔）
    public static final String mulVSeparator = ","; //一个键对应多个值，每个值用逗号分隔
    public static final String blockPrefix = "@";  //属于一个模块的标识行,接下来是该模块的多行。信息有：标识名，版本号，行数
    public static final String blockSubPrefix = "$"; //属于一个模块内的某个属性的表示行，接下来是该属性的多行。信息有：标识名 行数


    //在这里配置当前模块解析用到的具体类
    static Block<KeyCodes2> keyCodes2Block = new BlockKeyCodes2(new BlockOneCol(new BlockOneKey()));
    static Block<KeyCodes3> keyCodes3Block = new BlockKeyCodes3(new BlockOneKey(),new BlockJoyParams());
    static Block<Object> prefBlock = new BlockPref();

    //To-do : 现在还缺一个版本检查

    protected static List<String> keyCodes2ToString(KeyCodes2 keyCodes2) {
        return keyCodes2Block.objToString(keyCodes2);
    }

    protected static KeyCodes2 stringToKeyCodes2(List<String> lists) {
        return keyCodes2Block.stringToObj(lists);
    }

    protected static List<String> keyCodes3ToString(KeyCodes3 keyCodes3){
        return keyCodes3Block.objToString(keyCodes3);
    }

    protected static KeyCodes3 stringToKeyCodes3(List<String> lists) {
        return keyCodes3Block.stringToObj(lists);
    }


    /**
     * 将全部数据导入（写入本地），从剪切板获取文本，
     */
    public static void dataImport(String data){
        List<String> linesArray = Arrays.asList(data.split(lineSeparator));
        int curLine = 0;
        //prefBlock
        String[] prefHeader = linesArray.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(prefHeader[0].equals("Pref"));
        curLine++;
        prefBlock.stringToObj(linesArray.subList(curLine-1,curLine+ Integer.parseInt(prefHeader[2])));
        curLine += Integer.parseInt(prefHeader[2]);

        //keycodes2Block
        String[] keycodes2Header = linesArray.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(keycodes2Header[0].equals("KeyCodes2"));
        curLine++;
        KeyCodes2 keyCodes2 = keyCodes2Block.stringToObj(linesArray.subList(curLine-1,curLine+ Integer.parseInt(keycodes2Header[2])));
        KeyCodes2.write(keyCodes2,Globals.getAppContext());//写入本地
        curLine+= Integer.parseInt(keycodes2Header[2]);
        //keycodes3Block
        String[] keycodes3Header = linesArray.get(curLine).substring(1).split(propSeparator);
        Assert.isTrue(keycodes3Header[0].equals("KeyCodes3"));
        curLine++;
        KeyCodes3 keyCodes3 = keyCodes3Block.stringToObj(linesArray.subList(curLine-1, curLine+Integer.parseInt(keycodes3Header[2])));
        KeyCodes3.write(keyCodes3,Globals.getAppContext());
        curLine += Integer.parseInt(keycodes3Header[2]);

    }

    /**
     * 将全部数据导出为文本，复制到剪切板
     * @param keyCodes2
     * @param keyCodes3
     * @return
     */
    public static String dataExport(KeyCodes2 keyCodes2, KeyCodes3 keyCodes3){
        List<String> threePartLists = new ArrayList<>();
        threePartLists.addAll(prefBlock.objToString(null));
        threePartLists.addAll(keyCodes2Block.objToString(keyCodes2));
        threePartLists.addAll(keyCodes3Block.objToString(keyCodes3));

        //转换为str
        StringBuilder builder = new StringBuilder();
        for(String str:threePartLists){
            builder.append(str).append(lineSeparator);
        }
        if(builder.length()>0)
            builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }


    /**
     * 更新版本的话新建模块，更新版本
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
         * @return
         */
        public String getVersion();
    }

    static class BlockKeyCodes2 implements Block<KeyCodes2> {
        Block<OneCol> oneColBlock;


        public BlockKeyCodes2(Block<OneCol> blockOneCol) {
            oneColBlock =   blockOneCol;
        }

        @Override
        public KeyCodes2 stringToObj(List<String> lines) {
            KeyCodes2 keyCodes2 = new KeyCodes2();
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("KeyCodes2") && header[1].equals(getVersion()));
            curLine++;

            //mLeftSide
            String[] leftHeader = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(leftHeader[0].equals("mLeftSide"));
            curLine++;
            List<String> leftLines = lines.subList(curLine, curLine + Integer.parseInt(leftHeader[1]));
            curLine += Integer.parseInt(leftHeader[1]);
            int leftCurLine = 0;
            while (leftCurLine < leftLines.size()) {
                String[] oneColHeader = leftLines.get(leftCurLine).substring(1).split(propSeparator);
                Assert.isTrue(oneColHeader[0].equals("OneCol") && header[1].equals(oneColBlock.getVersion()));
                leftCurLine++;//需要把首行也传下去，截取列表记得-1
                List<String> oneColLines = leftLines.subList(leftCurLine - 1, leftCurLine + Integer.parseInt(oneColHeader[2]));
                leftCurLine += Integer.parseInt(oneColHeader[2]);//索引1是版本号，2才是行数
                keyCodes2.getLeftSide().add(oneColBlock.stringToObj(oneColLines));
            }

            //mRightSide
            String[] rightHeader = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(rightHeader[0].equals("mRightSide"));
            curLine++;
            List<String> rightLines = lines.subList(curLine, curLine + Integer.parseInt(rightHeader[1]));
            curLine += Integer.parseInt(rightHeader[1]);
            int rightCurLine = 0;
            while (rightCurLine < rightLines.size()) {
                String[] oneColHeader = rightLines.get(rightCurLine).substring(1).split(propSeparator);
                Assert.isTrue(oneColHeader[0].equals("OneCol") && header[1].equals(oneColBlock.getVersion()));
                rightCurLine++;
                List<String> oneColLines = rightLines.subList(rightCurLine - 1, rightCurLine + Integer.parseInt(oneColHeader[2]));
                rightCurLine +=  Integer.parseInt(oneColHeader[2]);
                keyCodes2.getRightSide().add(oneColBlock.stringToObj(oneColLines));
            }


            return keyCodes2;
        }

        @Override
        public List<String> objToString(KeyCodes2 keyCodes2) {
            List<String> lists = new ArrayList<>();

            //mLeftSide
            List<String> leftLines = new ArrayList<>();
            for (OneCol oneCol : keyCodes2.getLeftSide()) {
                leftLines.addAll(oneColBlock.objToString(oneCol));
            }
            leftLines.add(0, blockSubPrefix + "mLeftSide" + propSeparator + leftLines.size());            //添加标识行。 模块标识，，接下来要分配给这个标识的行数（行数先从内部开始计算吧,本模块全部转换好后再获取行数）

            //mRightSide
            List<String> rightLines = new ArrayList<>();
            for (OneCol oneCol : keyCodes2.getRightSide()) {
                rightLines.addAll(oneColBlock.objToString(oneCol));
            }
            rightLines.add(0, blockSubPrefix + "mRightSide" + propSeparator + rightLines.size());

            lists.addAll(leftLines);
            lists.addAll(rightLines);

            //添加版本号，方便日后更新
            lists.add(0, blockPrefix + "KeyCodes2" + propSeparator + getVersion() + propSeparator + lists.size());
            return lists;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }

    static class BlockOneCol implements Block<OneCol> {

        Block<OneKey> blockOneKey ;

        public BlockOneCol(Block<OneKey> blockOneKey) {
            this.blockOneKey = blockOneKey;
        }

        @Override
        public OneCol stringToObj(List<String> lines) {
            OneCol oneCol = new OneCol(new OneKey[0], 0);
            int curLine = 0;
            //标识行
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("OneCol") && header[1].equals(getVersion()));
            curLine++;
            if (curLine >= lines.size())
                throw new RuntimeException("如果OneCol个数为0，不应该走到这里");
            //id
            String[] idLine = lines.get(curLine).split(kvSeparator);
            Assert.isTrue(idLine[0].equals("id"));
            curLine++;
            oneCol.setId(Integer.parseInt(idLine[1]));
            //mAllKeys
            List<OneKey> allKeysList = new ArrayList<>(); //OneKey列表，最后转为数组设置到OneCol上
            String[] allKeysHeader = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(allKeysHeader[0].equals("mAllKeys"));
            curLine++;
            while (curLine < lines.size()) {
                String[] oneKeyHeader = lines.get(curLine).substring(1).split(propSeparator);
                curLine++;
                List<String> allKeysLines = lines.subList(curLine-1, curLine + Integer.parseInt(oneKeyHeader[2]));
                allKeysList.add(blockOneKey.stringToObj(allKeysLines));
                curLine += Integer.parseInt(oneKeyHeader[2]);
            }
            oneCol.setAllKeys(allKeysList.toArray(new OneKey[0]));
            return oneCol;
        }

        @Override
        public List<String> objToString(OneCol oneCol) {
            List<String> lists = new ArrayList<>();
            //id
            lists.add("id" + kvSeparator + oneCol.getId());
            //mAllKeys
            List<String> allkeysLists = new ArrayList<>();
            for (OneKey oneKey : oneCol.getAllKeys()) {
                allkeysLists.addAll(blockOneKey.objToString(oneKey));
            }
            allkeysLists.add(0, blockSubPrefix + "mAllKeys" + propSeparator + allkeysLists.size());
            lists.addAll(allkeysLists);

            lists.add(0, blockPrefix + "OneCol" + propSeparator + getVersion() + propSeparator + lists.size());
            return lists;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }

    static class BlockOneKey implements Block<OneKey> {

        @Override
        public OneKey stringToObj(List<String> lines) {
            OneKey oneKey = new OneKey(0);
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("OneKey") && header[1].equals(getVersion()));
            curLine++;
            //name，注意可能为空
            String[] nameLine = lines.get(curLine).split(kvSeparator, 2); //限制分割次数，字符串啥样都有
            Assert.isTrue(nameLine[0].equals("name"));
            curLine++;
            oneKey.setName(nameLine.length == 1 ? "" : nameLine[1]);
            //其余属性都在一行里了
            String[] propertiesLine = lines.get(curLine).split(propSeparator);
            //code
            oneKey.setCode(Integer.parseInt(propertiesLine[0].split(kvSeparator)[1]));
            //mIsShow
            oneKey.setShow(Boolean.parseBoolean(propertiesLine[1].split(kvSeparator)[1]));
            //marginLeft
            oneKey.setMarginLeft(Integer.parseInt(propertiesLine[2].split(kvSeparator)[1]));
            //marginTop
            oneKey.setMarginTop(Integer.parseInt(propertiesLine[3].split(kvSeparator)[1]));
            //mIsTrigger
            oneKey.setTrigger(Boolean.parseBoolean(propertiesLine[4].split(kvSeparator)[1]));
            //subCodes 因为有多个，所以用逗号分隔一下
            String[] subCodesProp = propertiesLine[5].split(kvSeparator);
            List<Integer> subCodes = new ArrayList<>();
            if (subCodesProp.length == 2) {
                for (String str : subCodesProp[1].split(mulVSeparator)) {
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
            lists.add("name" + kvSeparator + oneKey.getName());
            StringBuilder builder = new StringBuilder();
            //code
            builder.append("code").append(kvSeparator).append(oneKey.getCode());
            //mIsShow
            builder.append(propSeparator).append("mIsShow" + kvSeparator).append(oneKey.isShow());
            //marginLeft
            builder.append(propSeparator).append("marginLeft" + kvSeparator).append(oneKey.getMarginLeft());
            //marginTop
            builder.append(propSeparator).append("marginTop" + kvSeparator).append(oneKey.getMarginTop());
            //mIsTrigger
            builder.append(propSeparator).append("mIsTrigger" + kvSeparator).append(oneKey.isTrigger());
            //subCodes 因为有多个，所以用逗号分隔一下
            StringBuilder subCodesBuilder = new StringBuilder();
            for (int subCode : oneKey.getSubCodes()) {
                subCodesBuilder.append(subCode).append(mulVSeparator);
            }
            if (subCodesBuilder.length() > 0)
                subCodesBuilder.deleteCharAt(subCodesBuilder.length() - 1);
            builder.append(propSeparator).append("subCodes" + kvSeparator).append(subCodesBuilder);

            lists.add(builder.toString());
            lists.add(0, blockPrefix + "OneKey" + propSeparator +getVersion() + propSeparator + lists.size());

            return lists;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }

    static class BlockKeyCodes3 implements Block<KeyCodes3>{
        Block<OneKey> oneKeyBlock;
        Block<JoyParams> joyParamsBlock ;

        public BlockKeyCodes3(Block<OneKey> blockOneKey, Block<JoyParams> blockJoyParams) {
            this.oneKeyBlock = blockOneKey;
            this.joyParamsBlock = blockJoyParams;
        }

        @Override
        public KeyCodes3 stringToObj(List<String> lines) {
            KeyCodes3 keyCodes3 = new KeyCodes3()   ;
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
                Assert.isTrue(oneKeyHeader[0].equals("OneKey") && header[1].equals(oneKeyBlock.getVersion()));
                keyCurLine++;//需要把首行也传下去，截取列表记得-1
                List<String> oneKeyLines = keyLines.subList(keyCurLine - 1, keyCurLine + Integer.parseInt(oneKeyHeader[2]));
                keyCurLine += Integer.parseInt(oneKeyHeader[2]);//索引1是版本号，2才是行数
                keyCodes3.getKeyList().add(oneKeyBlock.stringToObj(oneKeyLines));
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
                Assert.isTrue(joyParamsHeader[0].equals("JoyParams") && header[1].equals(joyParamsBlock.getVersion()));
                joyCurLine++;
                List<String> joyParamsLines = joyLines.subList(joyCurLine - 1, joyCurLine + Integer.parseInt(joyParamsHeader[2]));
                joyCurLine +=  Integer.parseInt(joyParamsHeader[2]);
                keyCodes3.getJoyList().add(joyParamsBlock.stringToObj(joyParamsLines));
            }
            return keyCodes3;
        }

        @Override
        public List<String> objToString(KeyCodes3 keyCodes3) {
            List<String> lines = new ArrayList<>();
            //mKeyList
            List<String> keyLines = new ArrayList<>();
            for(OneKey oneKey:keyCodes3.getKeyList()){
                keyLines.addAll(oneKeyBlock.objToString(oneKey) );
            }
            keyLines.add(0,blockSubPrefix+"mKeyList"+propSeparator+keyLines.size());
            //mJoyList
            List<String> joyLines = new ArrayList<>();
            for(JoyParams joyParams: keyCodes3.getJoyList()){
                joyLines.addAll(joyParamsBlock.objToString(joyParams));
            }
            joyLines.add(0,blockSubPrefix+"mJoyList"+propSeparator+joyLines.size());

            lines.addAll(keyLines);
            lines.addAll(joyLines);
            lines.add(0,blockPrefix+"KeyCodes3"+propSeparator+getVersion()+propSeparator+lines.size());
            return lines;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }


    static class BlockJoyParams implements Block<JoyParams>{

        @Override
        public JoyParams stringToObj(List<String> lines) {
            JoyParams joyParams = new JoyParams();
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("JoyParams") && header[1].equals(getVersion()));
            curLine++;
            //属性都在一行里
            String[] properties = lines.get(curLine).split(propSeparator);
            //key4Directions
            String[] key4DStr = properties[0].split(kvSeparator)[1].split(mulVSeparator);
            joyParams.setKey4Directions(new int[]{
                    Integer.parseInt(key4DStr[0]),
                    Integer.parseInt(key4DStr[1]),
                    Integer.parseInt(key4DStr[2]),
                    Integer.parseInt(key4DStr[3])});
            //presetKey
            joyParams.setPresetKey(JoyParams.PresetKey.valueOf(properties[1].split(kvSeparator)[1]));
            //marginLeft
            joyParams.setMarginLeft(Integer.parseInt(properties[2].split(kvSeparator)[1]));
            //marginTop
            joyParams.setMarginTop(Integer.parseInt(properties[3].split(kvSeparator)[1]));
            //isFourDirections
            joyParams.setFourDirections(Boolean.parseBoolean(properties[4].split(kvSeparator)[1]));
            return joyParams;
        }

        @Override
        public List<String> objToString(JoyParams joyParams) {
            StringBuilder builder = new StringBuilder();
            List<String> lines = new ArrayList<>();
            //key4Directions
            builder.append("key4Directions").append(kvSeparator)
                    .append(joyParams.getKey4Directions()[0]).append(mulVSeparator)
                    .append(joyParams.getKey4Directions()[1]).append(mulVSeparator)
                    .append(joyParams.getKey4Directions()[2]).append(mulVSeparator)
                    .append(joyParams.getKey4Directions()[3]);
            //presetKey
            builder.append(propSeparator).append("presetKey").append(kvSeparator).append(joyParams.getPresetKey());
            //marginLeft
            builder.append(propSeparator).append("marginLeft").append(kvSeparator).append(joyParams.getMarginLeft());
            //marginTop
            builder.append(propSeparator).append("marginTop").append(kvSeparator).append(joyParams.getMarginTop());
            //isFourDirections
            builder.append(propSeparator).append("isFourDirections").append(kvSeparator).append(joyParams.isFourDirections());

            lines.add(builder.toString());
            lines.add(0,blockPrefix+"JoyParams"+propSeparator+getVersion()+propSeparator+ lines.size());
            return lines;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }


    /**
     * 标识名 Pref
     */
    static class BlockPref implements Block<Object>{

        @Override
        public Object stringToObj(List<String> lines) {
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("Pref") && header[1].equals(getVersion()));
            curLine++;

            SharedPreferences.Editor editor = Globals.getAppContext().getSharedPreferences(ControlsResolver.PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE   ).edit();
            String[] keysValues = lines.get(curLine).split(propSeparator);
            for(String kNVLine:keysValues){
                String[] kv = kNVLine.split(kvSeparator);
                switch (kv[0]) {
                    case PREF_KEY_SHOW_CURSOR:
                        editor.putBoolean(PREF_KEY_SHOW_CURSOR, Boolean.parseBoolean(kv[1]));
                        break;
                    case PREF_KEY_BTN_BG_COLOR:
                        editor.putInt(PREF_KEY_BTN_BG_COLOR, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_BTN_TXT_COLOR:
                        editor.putInt(PREF_KEY_BTN_TXT_COLOR, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_SIDEBAR_COLOR:
                        editor.putInt(PREF_KEY_SIDEBAR_COLOR, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_BTN_WIDTH:
                        editor.putInt(PREF_KEY_BTN_WIDTH, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_BTN_HEIGHT:
                        editor.putInt(PREF_KEY_BTN_HEIGHT, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_CUSTOM_BTN_POS:
                        editor.putBoolean(PREF_KEY_CUSTOM_BTN_POS, Boolean.parseBoolean(kv[1]));
                        break;
                    case PREF_KEY_MOUSE_MOVE_RELATIVE:
                        editor.putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, Boolean.parseBoolean(kv[1]));
                        break;
                    case PREF_KEY_BTN_ALPHA:
                        editor.putInt(PREF_KEY_BTN_ALPHA, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_MOUSE_SENSITIVITY:
                        editor.putInt(PREF_KEY_MOUSE_SENSITIVITY, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_MOUSE_OFFWINDOW_DISTANCE:
                        editor.putInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_BTN__TXT_SIZE:
                        editor.putInt(PREF_KEY_BTN__TXT_SIZE,Integer.parseInt(kv[1]));
                        break;
                    case PREF_KEY_BTN_ROUND_SHAPE:
                        editor.putBoolean(PREF_KEY_BTN_ROUND_SHAPE, Boolean.parseBoolean(kv[1]));
                        break;
                }
            }
            editor.apply();
            return null;
        }

        @Override
        public List<String> objToString(Object o) {

            SharedPreferences sp = Globals.getAppContext().getSharedPreferences(ControlsResolver.PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE   );
            List<String> lines = new ArrayList<>();
            String line = PREF_KEY_SHOW_CURSOR + kvSeparator +sp.getBoolean(PREF_KEY_SHOW_CURSOR, true)
                    + propSeparator + PREF_KEY_BTN_BG_COLOR + kvSeparator+sp.getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)
                    + propSeparator + PREF_KEY_BTN_TXT_COLOR + kvSeparator +sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK)
                    + propSeparator + PREF_KEY_SIDEBAR_COLOR + kvSeparator +sp.getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK)
                    + propSeparator + PREF_KEY_BTN_WIDTH + kvSeparator +sp.getInt(PREF_KEY_BTN_WIDTH, -2)
                    + propSeparator + PREF_KEY_BTN_HEIGHT + kvSeparator +sp.getInt(PREF_KEY_BTN_HEIGHT, -2)
                    + propSeparator + PREF_KEY_CUSTOM_BTN_POS + kvSeparator +sp.getBoolean(PREF_KEY_CUSTOM_BTN_POS, false)
                    + propSeparator + PREF_KEY_MOUSE_MOVE_RELATIVE + kvSeparator +sp.getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false)
                    + propSeparator + PREF_KEY_BTN_ALPHA + kvSeparator +sp.getInt(PREF_KEY_BTN_ALPHA, 255)
                    + propSeparator + PREF_KEY_MOUSE_SENSITIVITY + kvSeparator +sp.getInt(PREF_KEY_MOUSE_SENSITIVITY,80)
                    + propSeparator + PREF_KEY_MOUSE_OFFWINDOW_DISTANCE + kvSeparator +sp.getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE,0)
                    + propSeparator + PREF_KEY_BTN__TXT_SIZE + kvSeparator + sp.getInt(PREF_KEY_BTN__TXT_SIZE,4)
                    + propSeparator + PREF_KEY_BTN_ROUND_SHAPE + kvSeparator + sp.getBoolean(PREF_KEY_BTN_ROUND_SHAPE,false);

            lines.add(line);
            lines.add(0,blockPrefix+"Pref"+propSeparator+getVersion()+propSeparator+lines.size());
            return lines;
        }

        @Override
        public String getVersion() {
            return "1";
        }
    }
}
