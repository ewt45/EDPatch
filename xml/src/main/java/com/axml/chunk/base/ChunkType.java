package com.axml.chunk.base;

/**
 * Created by Sens on 2021/8/27.
 */
public enum ChunkType {
    CHUNK_STRING            /*chunk type*/(0x0001),
    CHUNK_RESOURCE          /*chunk type*/(0x0180),
    CHUNK_START_NAMESPACE   /*chunk type*/(0x0100),
    CHUNK_END_NAMESPACE     /*chunk type*/(0x0101),
    CHUNK_START_TAG         /*chunk type*/(0x0102),
    CHUNK_END_TAG           /*chunk type*/(0x0103),
    ;
    public final int TYPE;

    ChunkType(int TYPE) {
        this.TYPE = TYPE;
    }

    public static ChunkType valueOf(int TYPE) {
        for (ChunkType value : ChunkType.values())
            if (value.TYPE == TYPE) return value;
        return null;
    }
}
