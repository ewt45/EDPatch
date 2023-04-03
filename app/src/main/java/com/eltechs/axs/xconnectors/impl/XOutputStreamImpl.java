package com.eltechs.axs.xconnectors.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.impl.ProtoHelpers;
import com.eltechs.axs.xconnectors.BufferFiller;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class XOutputStreamImpl implements XOutputStream {
    private ByteBuffer buffer;
    private final SocketWriter socketWriter;
    private int bufferSizeLimit = 65536;
    private int bufferSizeHardLimit = 2097152;
    private final ReentrantLock lock = new ReentrantLock();

    public XOutputStreamImpl(SocketWriter socketWriter, int i) {
        this.socketWriter = socketWriter;
        this.buffer = ByteBuffer.allocateDirect(i);
    }

    public void setBufferSizeSoftLimit(int i) {
        Assert.isTrue(i > 0, "Buffer capacity must be positive.");
        this.bufferSizeLimit = i;
    }

    public void setBufferSizeHardLimit(int i) {
        this.bufferSizeHardLimit = i;
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void setByteOrder(ByteOrder byteOrder) {
        Assert.state(this.buffer.position() == 0, "Byte order of XOutputStream may not be changed when it contains unsent data.");
        this.buffer.order(byteOrder);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void writeByte(byte b) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        ensureSpaceIsAvailable(1);
        this.buffer.put(b);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void writeShort(short s) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        ensureSpaceIsAvailable(2);
        this.buffer.putShort(s);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void writeInt(int i) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        ensureSpaceIsAvailable(4);
        this.buffer.putInt(i);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void writeString8(String str) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        byte[] bytes = str.getBytes(Charset.forName("latin1"));
        byte[] bArr = new byte[ProtoHelpers.calculatePad(str.length())];
        ensureSpaceIsAvailable(bytes.length + bArr.length);
        this.buffer.put(bytes);
        this.buffer.put(bArr);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        ensureSpaceIsAvailable(i2);
        this.buffer.put(bArr, i, i2);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void write(int i, BufferFiller bufferFiller) throws IOException {
        Assert.state(this.lock.isLocked(), "XOutputStream must be locked when used.");
        ensureSpaceIsAvailable(i);
        this.buffer.limit(this.buffer.position() + i);
        ByteBuffer slice = this.buffer.slice();
        slice.order(this.buffer.order());
        this.buffer.position(this.buffer.position() + i);
        this.buffer.limit(this.buffer.capacity());
        bufferFiller.write(slice);
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public void flush() throws IOException {
        drainBuffer();
    }

    @Override // com.eltechs.axs.xconnectors.XOutputStream
    public XStreamLock lock() {
        return new OutputStreamLock();
    }

    private void ensureSpaceIsAvailable(int i) throws IOException {
        if ((this.buffer.capacity() - this.buffer.position()) + i > this.bufferSizeLimit) {
            drainBuffer();
        }
        int position = this.buffer.position();
        if (this.buffer.capacity() - position >= i) {
            return;
        }
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.buffer.capacity() + i);
        allocateDirect.order(this.buffer.order());
        this.buffer.rewind();
        allocateDirect.put(this.buffer);
        allocateDirect.position(position);
        this.buffer = allocateDirect;
    }

    private void drainBuffer() throws IOException {
        if (this.buffer.position() != 0) {
            this.buffer.flip();
            this.socketWriter.write(this.buffer);
            this.buffer.compact();
        }
    }

    public boolean hasBufferedData() {
        this.lock.lock();
        try {
            return this.buffer.position() != 0;
        } finally {
            this.lock.unlock();
        }
    }

    /* loaded from: classes.dex */
    public class OutputStreamLock implements XStreamLock {
        public OutputStreamLock() {
            XOutputStreamImpl.this.lock.lock();
        }

        @Override // com.eltechs.axs.xconnectors.XStreamLock, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                XOutputStreamImpl.this.flush();
            } finally {
                XOutputStreamImpl.this.lock.unlock();
            }
        }
    }
}