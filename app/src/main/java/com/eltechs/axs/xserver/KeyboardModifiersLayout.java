package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.Assert;

import java.util.Iterator;

public class KeyboardModifiersLayout {
    public static final int KEYCODES_PER_MODIFIER_NUM = 4;
    private boolean[] modifierStickness;
    private byte[][] modifierToKeycodes = new byte[MODIFIERS_NUM][];
    private static final int MODIFIER_MAX = KeyButNames.MOD5.ordinal();
    public static final int MODIFIERS_NUM = MODIFIER_MAX + 1;

    public KeyboardModifiersLayout() {
        for (int i = 0; i < MODIFIERS_NUM; i++) {
            this.modifierToKeycodes[i] = new byte[4];
        }
        this.modifierStickness = new boolean[MODIFIERS_NUM];
    }

    public Iterable<Byte> getModifierKeycodes(final KeyButNames keyButNames) {
        return new Iterable<Byte>() { // from class: com.eltechs.axs.xserver.KeyboardModifiersLayout.1
            @Override // java.lang.Iterable
            public Iterator<Byte> iterator() {
                final byte[] bArr = KeyboardModifiersLayout.this.modifierToKeycodes[keyButNames.ordinal()];
                return new Iterator<Byte>() { // from class: com.eltechs.axs.xserver.KeyboardModifiersLayout.1.1
                    int currentNum = 0;

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.currentNum < 4 && bArr[this.currentNum] != 0;
                    }


                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    /* renamed from: next */
                    public Byte next() {
                        if (hasNext()) {
                            byte[] bArr2 = bArr;
                            int i = this.currentNum;
                            this.currentNum = i + 1;
                            return Byte.valueOf(bArr2[i]);
                        }
                        return (byte) 0;
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        Assert.state(false, "Read-only iterator.");
                    }
                };
            }
        };
    }

    public void setKeycodeToModifier(byte b, KeyButNames keyButNames) {
        Assert.isTrue(keyButNames.ordinal() <= MODIFIER_MAX);
        byte[] bArr = this.modifierToKeycodes[keyButNames.ordinal()];
        for (int i = 0; i < 4; i++) {
            if (bArr[i] == 0) {
                bArr[i] = b;
                return;
            }
        }
        Assert.state(false, String.format("Unable to assign keycode %d to modifier %d: too many keycodes already assigned", Byte.valueOf(b), Integer.valueOf(keyButNames.ordinal())));
    }

    public KeyButNames getModifierByKeycode(byte b) {
        for (int i = 0; i < MODIFIERS_NUM; i++) {
            for (int i2 = 0; i2 < 4 && this.modifierToKeycodes[i][i2] != 0; i2++) {
                if (this.modifierToKeycodes[i][i2] == b) {
                    return KeyButNames.values()[i];
                }
            }
        }
        return null;
    }

    public boolean isModifierSticky(KeyButNames keyButNames) {
        return this.modifierStickness[keyButNames.ordinal()];
    }

    public void setModifierSticky(KeyButNames keyButNames, boolean z) {
        this.modifierStickness[keyButNames.ordinal()] = z;
    }
}