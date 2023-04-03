package com.axml.chunk;

import com.axml.chunk.base.BaseChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 */
public class StringChunk extends BaseChunk {
    public int stringCount;
    public int styleCount;
    public boolean isUTF8;
    public boolean isSorted;
    public int stringStart;
    public int styleStart;

    public int[] stringOffsets;
    public int[] styleOffsets;

    public List<String> stringList;

    public StringChunk(ByteBuffer byteBuffer) {
        super(byteBuffer);
        this.stringCount = byteBuffer.getInt();
        this.styleCount = byteBuffer.getInt();
        //utf8(0x0100) or default utf16le(0x0000)
        this.isUTF8 = byteBuffer.getShort() != 0;
        this.isSorted = byteBuffer.getShort() != 0;
        this.stringStart = byteBuffer.getInt();
        this.styleStart = byteBuffer.getInt();

        stringOffsets = new int[stringCount];
        for (int i = 0; i < stringOffsets.length; i++)
            stringOffsets[i] = byteBuffer.getInt();

        styleOffsets = new int[styleCount];
        for (int i = 0; i < styleOffsets.length; i++)
            styleOffsets[i] = byteBuffer.getInt();

        stringList = new ArrayList<>(stringCount);

        for (int i = 0; i < stringCount; i++) {
            byteBuffer.position(ChunkStartPosition + stringStart + stringOffsets[i]);
            int byteCount;
            if (isUTF8) {
                int strCount = byteBuffer.get() & 0xFF;
                byteCount = byteBuffer.get() & 0xFF;
            } else
                byteCount = byteBuffer.getShort() * 2;

            byte[] string = new byte[byteCount];
            byteBuffer.get(string);
            if (isUTF8) stringList.add(new String(string, StandardCharsets.UTF_8));
            else stringList.add(new String(string, StandardCharsets.UTF_16LE));
        }
        //styleCount always = 0  [skip]
        byteBuffer.position(ChunkStartPosition + chunkSize);
    }

    public String getString(int index) {
        return stringList.get(index);
    }

    private void stringToBytes(ByteArrayOutputStream stream, String str) throws IOException {
        ByteBuffer byteBuffer;
        if (isUTF8) {
            byte[] chars = str.getBytes(StandardCharsets.UTF_8);
            byteBuffer = ByteBuffer.allocate(2 + chars.length + 1);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.put((byte) str.length());
            byteBuffer.put((byte) chars.length);
            byteBuffer.put(chars);
            byteBuffer.put((byte) 0);
        } else {
            byte[] chars = str.getBytes(StandardCharsets.UTF_16LE);
            byteBuffer = ByteBuffer.allocate(2 + chars.length + 2);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putShort((short) str.length());
            byteBuffer.put(chars);
            byteBuffer.putShort((short) 0);
        }
        stream.write(byteBuffer.array());
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        stringCount = stringList.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 4 + stringOffsets.length * 4 + styleOffsets.length * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(stringCount);
        byteBuffer.putInt(styleCount);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putShort((short) (isUTF8 ? 1 : 0));
        byteBuffer.putShort((short) (isSorted ? 1 : 0));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(stringStart);
        byteBuffer.putInt(styleStart);
        int stringOffset = 0;
        if (stringOffsets.length != stringCount)
            stringOffsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            stringOffsets[i] = stringOffset;
            byteBuffer.putInt(stringOffset);
            if (isUTF8)
                stringOffset += 3 + stringList.get(i).getBytes(StandardCharsets.UTF_8).length;
            else
                stringOffset += 4 + stringList.get(i).getBytes(StandardCharsets.UTF_16LE).length;
        }
        //styleCount always = 0  [skip]
        for (int offset : styleOffsets) byteBuffer.putInt(offset);
        stream.write(byteBuffer.array());
        for (String str : stringList)
            stringToBytes(stream, str);
    }
}
