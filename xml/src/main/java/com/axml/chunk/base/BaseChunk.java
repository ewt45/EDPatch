package com.axml.chunk.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public abstract class BaseChunk {
    public final int ChunkStartPosition;

    public final short chunkType;
    public final short headerSize;
    public int chunkSize;


    public BaseChunk( short chunkType, short headerSize, int chunkSize){
        ChunkStartPosition = 0;//在bytebuffer中的位置。随便填一个吧
        this.chunkType = chunkType;
        this.headerSize = headerSize;
        this.chunkSize = chunkSize;
    }

    public BaseChunk(ByteBuffer byteBuffer) {
        byteBuffer.position(byteBuffer.position() - 2);
        ChunkStartPosition = byteBuffer.position();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.chunkType = byteBuffer.getShort();
        this.headerSize = byteBuffer.getShort();
        this.chunkSize = byteBuffer.getInt();
    }

    protected abstract void toBytes(ByteArrayOutputStream stream) throws IOException;

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        toBytes(stream);
        byte[] bytes = stream.toByteArray();
        this.chunkSize = bytes.length + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 + bytes.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(this.chunkType);
        byteBuffer.putShort(this.headerSize);
        byteBuffer.putInt(this.chunkSize);
        byteBuffer.put(bytes);
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        return "";
    }

}
