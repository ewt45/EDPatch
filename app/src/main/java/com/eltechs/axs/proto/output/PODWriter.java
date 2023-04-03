package com.eltechs.axs.proto.output;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.impl.ProtoHelpers;
import com.eltechs.axs.proto.output.PODVisitor;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class PODWriter {
    private PODWriter() {
    }

    public static void write(XOutputStream xOutputStream, Object obj) throws IOException {
        PODVisitor.visit(obj, new XStreamWriter(xOutputStream));
    }

    public static void write(ByteBuffer byteBuffer, Object obj) {
        try {
            PODVisitor.visit(obj, new BufferWriter(byteBuffer));
        } catch (IOException unused) {
        }
    }

    public static int getOnWireLength(Object obj) {
        try {
            LengthFinder lengthFinder = new LengthFinder();
            PODVisitor.visit(obj, lengthFinder);
            return lengthFinder.size;
        } catch (IOException unused) {
            Assert.state(false, "IOException can't be thrown by LengthFinder.");
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class XStreamWriter implements PODVisitor.Callback {
        private final XOutputStream outputStream;

        XStreamWriter(XOutputStream xOutputStream) {
            this.outputStream = xOutputStream;
        }

        @Override // com.eltechs.axs.proto.output.PODVisitor.Callback
        public void apply(Object obj) throws IOException {
            if (obj instanceof Byte) {
                this.outputStream.writeByte(((Byte) obj).byteValue());
            } else if (obj instanceof Boolean) {
                this.outputStream.writeByte(obj.equals(Boolean.TRUE) ? (byte) 1 : (byte) 0);
            } else if (obj instanceof Short) {
                this.outputStream.writeShort(((Short) obj).shortValue());
            } else if (obj instanceof Integer) {
                this.outputStream.writeInt(((Integer) obj).intValue());
            } else if (obj instanceof String) {
                this.outputStream.writeString8((String) obj);
            } else {
                Assert.isTrue(false, String.format("Unsupported POD member type %s.", obj.getClass()));
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class BufferWriter implements PODVisitor.Callback {
        private final ByteBuffer buffer;

        BufferWriter(ByteBuffer byteBuffer) {
            this.buffer = byteBuffer;
        }

        @Override // com.eltechs.axs.proto.output.PODVisitor.Callback
        public void apply(Object obj) throws IOException {
            if (obj instanceof Byte) {
                this.buffer.put(((Byte) obj).byteValue());
            } else if (obj instanceof Boolean) {
                this.buffer.put(obj.equals(Boolean.TRUE) ? (byte) 1 : (byte) 0);
            } else if (obj instanceof Short) {
                this.buffer.putShort(((Short) obj).shortValue());
            } else if (obj instanceof Integer) {
                this.buffer.putInt(((Integer) obj).intValue());
            } else {
                Assert.isTrue(false, String.format("Unsupported POD member type %s.", obj.getClass()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LengthFinder implements PODVisitor.Callback {
        private int size;

        private LengthFinder() {
        }

        @Override // com.eltechs.axs.proto.output.PODVisitor.Callback
        public void apply(Object obj) throws IOException {
            if (obj instanceof Byte) {
                this.size++;
            } else if (obj instanceof Boolean) {
                this.size++;
            } else if (obj instanceof Short) {
                this.size += 2;
            } else if (obj instanceof Integer) {
                this.size += 4;
            } else if (obj instanceof String) {
                this.size += ProtoHelpers.roundUpLength4(((String) obj).length());
            } else {
                Assert.isTrue(false, String.format("Unsupported POD member type %s.", obj.getClass()));
            }
        }
    }
}
