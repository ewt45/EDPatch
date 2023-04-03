package com.axml.chunk;

import com.axml.chunk.base.BaseChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 * Used system resource ID [android.R.**]
 */
public class ResourceChunk extends BaseChunk {
    public final List<Integer> resourceIDs;

    public ResourceChunk(ByteBuffer byteBuffer) {
        super(byteBuffer);
        int idCount = chunkSize / 4 - 2;
        resourceIDs = new ArrayList<>(idCount);
        for (int i = 0; i < idCount; i++)
            resourceIDs.add(byteBuffer.getInt());
        byteBuffer.position(ChunkStartPosition + chunkSize);
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(resourceIDs.size() * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (Integer integer : resourceIDs)
            byteBuffer.putInt(integer);
        stream.write(byteBuffer.array());
    }
}
