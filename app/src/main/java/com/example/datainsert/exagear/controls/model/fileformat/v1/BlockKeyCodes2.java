package com.example.datainsert.exagear.controls.model.fileformat.v1;

import static com.example.datainsert.exagear.controls.model.FormatHelper.blockPrefix;
import static com.example.datainsert.exagear.controls.model.FormatHelper.blockSubPrefix;
import static com.example.datainsert.exagear.controls.model.FormatHelper.propSeparator;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.FormatHelper.Block;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockKeyCodes2 implements Block<KeyCodes2> {
//    Block<OneCol> oneColBlock;

    Map<String,Block<OneCol>> oneColBlockMap;

    public BlockKeyCodes2(Map<String,Block<OneCol>> blockOneCol) {
        oneColBlockMap = blockOneCol;
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
            Assert.isTrue(oneColHeader[0].equals("OneCol") );
            leftCurLine++;//需要把首行也传下去，截取列表记得-1
            Block<OneCol> oneColBlock = oneColBlockMap.get(oneColHeader[1]);
            if(oneColBlock!=null){
                List<String> oneColLines = leftLines.subList(leftCurLine - 1, leftCurLine + Integer.parseInt(oneColHeader[2]));
                keyCodes2.getLeftSide().add(oneColBlock.stringToObj(oneColLines));
            }
            leftCurLine += Integer.parseInt(oneColHeader[2]);//索引1是版本号，2才是行数
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
            Assert.isTrue(oneColHeader[0].equals("OneCol") );
            rightCurLine++;
            Block<OneCol> oneColBlock = oneColBlockMap.get(oneColHeader[1]);
            if(oneColBlock!=null){
                List<String> oneColLines = rightLines.subList(rightCurLine - 1, rightCurLine + Integer.parseInt(oneColHeader[2]));
                keyCodes2.getRightSide().add(oneColBlock.stringToObj(oneColLines));
            }
            rightCurLine += Integer.parseInt(oneColHeader[2]);

        }


        return keyCodes2;
    }

    @Override
    public List<String> objToString(KeyCodes2 keyCodes2) {
        List<String> lists = new ArrayList<>();

        //从map中获取最新版本的Block
        List<String> mapKeys = new ArrayList<>(oneColBlockMap.keySet());
        Block<OneCol> oneColBlock = oneColBlockMap.get(mapKeys.get(mapKeys.size()-1));
        assert  oneColBlock!=null;

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
