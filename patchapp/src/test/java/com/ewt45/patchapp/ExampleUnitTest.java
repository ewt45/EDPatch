package com.ewt45.patchapp;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import static org.junit.Assert.*;

import com.axml.AndroidBinaryXml;
import com.axml.chunk.EndTagChunk;
import com.axml.chunk.StartTagChunk;
import com.axml.chunk.StringChunk;
import com.axml.chunk.base.BaseChunk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void byteToByte() throws IOException {
        /*
         * 测试读取文件转为类之后，再转为二进制存为文件是否有变化：没变化
         */
        File file = new File("E:\\Tencent archive\\1224749786\\FileRecv\\AndroidManifest.xml");
        AndroidBinaryXml androidBinaryXml = new AndroidBinaryXml(file);
        byte[] datas = androidBinaryXml.toBytes();
        File file2 = new File("E:\\Tencent archive\\1224749786\\FileRecv\\AndroidManifest2.xml");
        if (file2.exists()) file2.delete();
        FileUtils.writeByteArrayToFile(file2, datas);
        AndroidBinaryXml manifest = new AndroidBinaryXml(datas);
        System.out.println(manifest);

    }

    @Test
    public void addServiceChunk() throws IOException {
        //尝试添加一个service标签
        File file = new File("E:\\Tencent archive\\1224749786\\FileRecv\\AndroidManifest.xml");
        AndroidBinaryXml androidBinaryXml = new AndroidBinaryXml(file);
        //定位到application的结束标签(是EndTagChunk且name是application）
        List<BaseChunk> structList = androidBinaryXml.structList;
        StringChunk stringChunk = androidBinaryXml.stringChunk;
        List<String> stringPool = stringChunk.stringList;
        int indexOfAppEndTag = -1; //插入标签的位置
        for (int i = structList.size() - 1; i >= 0; i--) {
            if (structList.get(i) instanceof EndTagChunk) {
                indexOfAppEndTag = i;
                EndTagChunk etChunk = (EndTagChunk) structList.get(i);
                if (stringPool.get(etChunk.name).equals("application"))
                    break;
            }
        }
        //开始新建标签

        List<StartTagChunk.Attribute> attributes = new ArrayList<>();
        //attr属性.新建Attribute并添加到列表中
        //1. android:name="com.mittorn.virgloverlay.process.p2"
        int namespaceUri = indexOfStringPool(stringChunk, "http://schemas.android.com/apk/res/android");
        int name = indexOfStringPool(stringChunk, "name");
        int value = indexOfStringPool(stringChunk, "com.mittorn.virgloverlay.process.p2");
        short structureSize = 0x0008;
        int res0 = 0x00;
        int type = 0x03;
        attributes.add(new StartTagChunk.Attribute(namespaceUri, name, value, structureSize, res0, type, value));
        //2. android:exported="true"
        name = indexOfStringPool(stringChunk, "exported");
        value = indexOfStringPool(stringChunk, "true");
        attributes.add(new StartTagChunk.Attribute(namespaceUri, name, value, structureSize, res0, type, value));
        //3. android:process=":p2"
        name = indexOfStringPool(stringChunk, "process");
        value = indexOfStringPool(stringChunk, ":p2");
        attributes.add(new StartTagChunk.Attribute(namespaceUri, name, value, structureSize, res0, type, value));

        //service属性 开始和结束tag
        int lineNumber = ((EndTagChunk)structList.get(indexOfAppEndTag)).lineNumber; //该tag在原xml文件中的行数，希望乱写没事。要不就写application endtag的同一行
        int chunkSize = 0x24 + attributes.size() * 0x14; //这个chunk的总体积（包含头部）
        StartTagChunk serviceStart = new StartTagChunk(
                (short) 0x0102, (short) 0x0010, chunkSize, //BaseChunk
                lineNumber, -1, stringChunk,//BaseContentChunk
                -1, indexOfStringPool(stringChunk,"service"), (short) 0x0014, (short) 0x0014,
                (short) 3, (short) 0, (short) 0, (short) 0, attributes, androidBinaryXml.namespaceChunkList
        );

        EndTagChunk serviceEnd = new EndTagChunk(
                (short) 0x0103, (short) 0x0010, 0x0018, //BaseChunk
                lineNumber, -1, stringChunk,//BaseContentChunk
                -1, indexOfStringPool(stringChunk,"service")
        );
        //添加到tag列表时，先在该位置加end标签，再在同位置加start标签就正好了
        androidBinaryXml.structList.add(indexOfAppEndTag,serviceEnd);
        androidBinaryXml.structList.add(indexOfAppEndTag,serviceStart);
        System.out.println("定位到application的结束标签：" + indexOfAppEndTag);
        byte[] datas = androidBinaryXml.toBytes();
        File file2 = new File("E:\\Tencent archive\\1224749786\\FileRecv\\AndroidManifest2.xml");
        if(file2.exists()) file2.delete();
        FileUtils.writeByteArrayToFile(file2,datas);
        AndroidBinaryXml manifest = new AndroidBinaryXml(datas);
        System.out.println(manifest);
    }

    /**
     * 用于获取对应字符串在字符池中的索引，如果没有的话就添加到字符池中
     */
    private int indexOfStringPool(StringChunk stringChunk, String s) {
        int index = stringChunk.stringList.indexOf(s);
        if (index == -1) {
            index = stringChunk.stringCount;
            //stringList 列表中添加一个字符串；stringCount字符串总数+1，
            // stringOffsets由于在toBytes中判断和stringCount不等的话会根据count数量新建int[] 所以交给toBytes了(不行，分配byteBuffer空间的时候要用到这个，那就new个新的吧）
            stringChunk.stringList.add(s);
            stringChunk.stringCount++;
            stringChunk.stringOffsets = new int[stringChunk.stringCount];
            //stringStart也要手动改。。。offset个数增加了，字符池起始位置也会向后移动
            stringChunk.stringStart+=4;
        }
        return index;
    }
}