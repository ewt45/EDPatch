package com.eltechs.axs.xconnectors.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class XInputStreamImpl implements XInputStream {
    private ByteBuffer activeRegion;
    private int bufferSizeHardLimit = 2097152;
    private ByteBuffer inputBuffer;
    private final SocketReader socketReader;

    public XInputStreamImpl(SocketReader socketReader, int i) {
        this.socketReader = socketReader;
        this.inputBuffer = ByteBuffer.allocateDirect(i);
    }

    public void setBufferSizeHardLimit(int i) {
        this.bufferSizeHardLimit = i;
    }

    public int readMoreData() throws IOException {
        growInputBufferIfNecessary();
        return this.socketReader.read(this.inputBuffer);
    }

    private void growInputBufferIfNecessary() throws IOException {
        Assert.state(this.activeRegion == null, "Can't resize an input buffer while processing messages contained in it.");
        if (this.inputBuffer.position() == this.inputBuffer.capacity()) {
            if (this.inputBuffer.capacity() > this.bufferSizeHardLimit) {
                throw new IOException("Input buffer size has exceeded the hard limit.");
            }
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(2 * this.inputBuffer.capacity());
            allocateDirect.order(this.inputBuffer.order());
            this.inputBuffer.rewind();
            allocateDirect.put(this.inputBuffer);
            this.inputBuffer = allocateDirect;
        }
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public void setByteOrder(ByteOrder byteOrder) {
        this.inputBuffer.order(byteOrder);
        if (this.activeRegion != null) {
            this.activeRegion.order(byteOrder);
        }
    }

    public void prepareForReading() {
        Assert.state(this.activeRegion == null, "prepareForReading() called when a reading operation is in progress.");
        int position = this.inputBuffer.position();
        this.inputBuffer.position(0);
        this.inputBuffer.limit(position);
        this.activeRegion = this.inputBuffer.slice();
        this.activeRegion.order(this.inputBuffer.order());
        this.inputBuffer.limit(this.inputBuffer.capacity());
        this.inputBuffer.position(position);
    }

    public void doneWithReading(int i) {
        Assert.state(this.activeRegion != null, "doneWithReading() called when no reading operation is in progress.");
        Assert.isTrue(i <= this.activeRegion.capacity(), "NioProcessorThread claims to have processed more data than is available.");
        this.activeRegion = null;
        if (i == this.inputBuffer.position()) {
            this.inputBuffer.clear();
        } else if (i > 0) {
            int position = this.inputBuffer.position();
            this.inputBuffer.position(i);
            this.inputBuffer.limit(position);
            this.inputBuffer.compact();
        }
    }

    public int getActiveRegionPosition() {
        Assert.state(this.activeRegion != null, "getActiveRegionPosition() called when no reading operation is in progress.");
        return this.activeRegion.position();
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public int getAvailableBytesCount() {
        return this.activeRegion.remaining();
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public byte getByte() {
        return this.activeRegion.get();
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public short getShort() {
        return this.activeRegion.getShort();
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public int getInt() {
        return this.activeRegion.getInt();
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public void get(byte[] bArr) {
        this.activeRegion.get(bArr);
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public ByteBuffer getAsByteBuffer(int i) {
        ByteBuffer slice = this.activeRegion.slice();
        slice.limit(i);
        slice.order(this.activeRegion.order());
        this.activeRegion.position(this.activeRegion.position() + i);
        return slice;
    }

    @Override // com.eltechs.axs.xconnectors.XInputStream
    public void skip(int i) {
        this.activeRegion.position(this.activeRegion.position() + i);
    }
}