package com.example.datainsert.exagear.controls.model;

import com.eltechs.axs.helpers.Assert;

import java.util.ArrayList;
import java.util.List;

public class FormatHelper {

    //不知道会不会有换行问题。统一用这个吧
    public static final String lineSeparator = "\n";
    public static final String kvSeparator = "="; //键值对的分割符
    public static final String propSeparator = ";"; //每个属性间的分隔符（一行里有多个属性的话用分号分隔）
    public static final String mulKSeparator = ","; //一个键对应多个值，每个值用逗号分隔
    public static final String blockPrefix = "@";  //属于一个模块的标识行,接下来是该模块的多行。信息有：标识名，版本号，行数
    public static final String blockSubPrefix = "$"; //属于一个模块内的某个属性的表示行，接下来是该属性的多行。信息有：标识名 行数

    protected static List<String> keyCodes2ToString(KeyCodes2 keyCodes2) {
        return new BlockKeyCodes2().objToString(keyCodes2);
    }

    protected static KeyCodes2 stringToKeyCodes2(List<String> lists) {
        return new BlockKeyCodes2().stringToObj(lists);
    }

    protected static List<String> keyCodes3ToString(KeyCodes3 keyCodes3){
        return new BlockKeyCodes3().objToString(keyCodes3);
    }

    protected static KeyCodes3 stringToKeyCodes3(List<String> lists) {
        return new BlockKeyCodes3().stringToObj(lists);
    }

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
    }

    static class BlockKeyCodes2 implements Block<KeyCodes2> {
        Block<OneCol> oneColBlock = new BlockOneCol();

        @Override
        public KeyCodes2 stringToObj(List<String> lines) {
            KeyCodes2 keyCodes2 = new KeyCodes2();
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("KeyCodes2") && header[1].equals("1"));
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
                Assert.isTrue(oneColHeader[0].equals("OneCol") && header[1].equals("1"));
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
                Assert.isTrue(oneColHeader[0].equals("OneCol") && header[1].equals("1"));
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
            lists.add(0, blockPrefix + "KeyCodes2" + propSeparator + "1" + propSeparator + lists.size());
            return lists;
        }
    }

    static class BlockOneCol implements Block<OneCol> {
        Block<OneKey> blockOneKey = new BlockOneKey();

        @Override
        public OneCol stringToObj(List<String> lines) {
            OneCol oneCol = new OneCol(new OneKey[0], 0);
            int curLine = 0;
            //标识行
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("OneCol") && header[1].equals("1"));
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

            lists.add(0, blockPrefix + "OneCol" + propSeparator + "1" + propSeparator + lists.size());
            return lists;
        }
    }

    static class BlockOneKey implements Block<OneKey> {

        @Override
        public OneKey stringToObj(List<String> lines) {
            OneKey oneKey = new OneKey(0);
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("OneKey") && header[1].equals("1"));
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
                for (String str : subCodesProp[1].split(mulKSeparator)) {
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
                subCodesBuilder.append(subCode).append(mulKSeparator);
            }
            if (subCodesBuilder.length() > 0)
                subCodesBuilder.deleteCharAt(subCodesBuilder.length() - 1);
            builder.append(propSeparator).append("subCodes" + kvSeparator).append(subCodesBuilder);

            lists.add(builder.toString());
            lists.add(0, blockPrefix + "OneKey" + propSeparator + "1" + propSeparator + lists.size());

            return lists;
        }
    }

    static class BlockKeyCodes3 implements Block<KeyCodes3>{
        Block<OneKey> oneKeyBlock = new BlockOneKey();
        Block<JoyParams> joyParamsBlock = new BlockJoyParams();
        @Override
        public KeyCodes3 stringToObj(List<String> lines) {
            KeyCodes3 keyCodes3 = new KeyCodes3()   ;
            keyCodes3.getKeyList().clear();//这里会自动创建全部code的按键。先清空
            keyCodes3.getJoyList().clear();
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("KeyCodes3") && header[1].equals("1"));
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
                Assert.isTrue(oneKeyHeader[0].equals("OneKey") && header[1].equals("1"));
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
                Assert.isTrue(joyParamsHeader[0].equals("JoyParams") && header[1].equals("1"));
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
            lines.add(0,blockPrefix+"KeyCodes3"+propSeparator+"1"+propSeparator+lines.size());
            return lines;
        }
    }


    static class BlockJoyParams implements Block<JoyParams>{

        @Override
        public JoyParams stringToObj(List<String> lines) {
            JoyParams joyParams = new JoyParams();
            int curLine = 0;
            String[] header = lines.get(curLine).substring(1).split(propSeparator);
            Assert.isTrue(header[0].equals("JoyParams") && header[1].equals("1"));
            curLine++;
            //属性都在一行里
            String[] properties = lines.get(curLine).split(propSeparator);
            //key4Directions
            String[] key4DStr = properties[0].split(kvSeparator)[1].split(mulKSeparator);
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
                    .append(joyParams.getKey4Directions()[0]).append(mulKSeparator)
                    .append(joyParams.getKey4Directions()[1]).append(mulKSeparator)
                    .append(joyParams.getKey4Directions()[2]).append(mulKSeparator)
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
            lines.add(0,blockPrefix+"JoyParams"+propSeparator+"1"+propSeparator+ lines.size());
            return lines;
        }
    }
}
