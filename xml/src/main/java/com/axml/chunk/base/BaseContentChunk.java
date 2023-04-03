package com.axml.chunk.base;

import com.axml.chunk.StringChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public class BaseContentChunk extends BaseChunk {
    public final int lineNumber;
    public final int comment;

    protected final StringChunk stringChunk;

    public BaseContentChunk(
             short chunkType, short headerSize, int chunkSize,
            int lineNumber, int comment, StringChunk stringChunk){
        super(chunkType,headerSize,chunkSize);
        this.lineNumber = lineNumber;
        this.comment = comment;
        this.stringChunk = stringChunk;
    }

    public BaseContentChunk(ByteBuffer byteBuffer, StringChunk stringChunk) {
        super(byteBuffer);
        lineNumber = byteBuffer.getInt();
        comment = byteBuffer.getInt();

        this.stringChunk = stringChunk;
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.lineNumber);
        byteBuffer.putInt(this.comment);
        stream.write(byteBuffer.array());
    }

    protected String getString(int index) {
        if (index == -1) return "";
        return stringChunk.getString(index);
    }
}
