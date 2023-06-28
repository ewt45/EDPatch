package com.example.datainsert.exagear.controls.model.fileformat.v1;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.FormatHelper.Block;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockOneCol implements Block<OneCol> {

    Map<String, Block<OneKey>> onekeyBlockMap;
//    public BlockOneCol(){
//        onekeyBlockMap = new HashMap<>();
//        Block<OneKey> oneKeyBlock = new BlockOneKey();
//        onekeyBlockMap.put(oneKeyBlock.getVersion(),oneKeyBlock);
//    }
    public BlockOneCol(Map<String, Block<OneKey>> blockOneKey) {
        onekeyBlockMap = blockOneKey;
    }

    @Override
    public OneCol stringToObj(List<String> lines) {
        OneCol oneCol = new OneCol(new OneKey[0], 0);
        int curLine = 0;
        //标识行
        String[] header = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
        Assert.isTrue(header[0].equals("OneCol") && header[1].equals(getVersion()));
        curLine++;
        if (curLine >= lines.size())
            throw new RuntimeException("如果OneCol个数为0，不应该走到这里");
        //id
        String[] idLine = lines.get(curLine).split(FormatHelper.kvSeparator);
        Assert.isTrue(idLine[0].equals("id"));
        curLine++;
        oneCol.setId(Integer.parseInt(idLine[1]));
        //mAllKeys
        List<OneKey> allKeysList = new ArrayList<>(); //OneKey列表，最后转为数组设置到OneCol上
        String[] allKeysHeader = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
        Assert.isTrue(allKeysHeader[0].equals("mAllKeys"));
        curLine++;
        while (curLine < lines.size()) {
            String[] oneKeyHeader = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
            Assert.isTrue(oneKeyHeader[0].equals("OneKey"));
            curLine++;
            Block<OneKey> oneKeyBlock = onekeyBlockMap.get(oneKeyHeader[1]);
            if(oneKeyBlock!=null){
                List<String> allKeysLines = lines.subList(curLine - 1, curLine + Integer.parseInt(oneKeyHeader[2]));
                allKeysList.add(oneKeyBlock.stringToObj(allKeysLines));
            }

            curLine += Integer.parseInt(oneKeyHeader[2]);
        }
        oneCol.setAllKeys(allKeysList.toArray(new OneKey[0]));
        return oneCol;
    }

    @Override
    public List<String> objToString(OneCol oneCol) {
        List<String> lists = new ArrayList<>();
        //id
        lists.add("id" + FormatHelper.kvSeparator + oneCol.getId());
        //mAllKeys
        List<String> mapKeys = new ArrayList<>(onekeyBlockMap.keySet());
        Block<OneKey> oneKeyBlock = onekeyBlockMap.get(mapKeys.get(mapKeys.size()-1));
        assert  oneKeyBlock!=null;
        List<String> allkeysLists = new ArrayList<>();
        for (OneKey oneKey : oneCol.getAllKeys()) {
            allkeysLists.addAll(oneKeyBlock.objToString(oneKey));
        }
        allkeysLists.add(0, FormatHelper.blockSubPrefix + "mAllKeys" + FormatHelper.propSeparator + allkeysLists.size());
        lists.addAll(allkeysLists);

        lists.add(0, FormatHelper.blockPrefix + "OneCol" + FormatHelper.propSeparator + getVersion() + FormatHelper.propSeparator + lists.size());
        return lists;
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
