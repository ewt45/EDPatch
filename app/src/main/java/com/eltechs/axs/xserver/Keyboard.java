package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.impl.masks.Mask;

public class Keyboard {
    private final XServer xServer;
    private final KeymapStateMask keymapStateMask = new KeymapStateMask();
    private final KeyboardListenersList keylisteners = new KeyboardListenersList();
    private Mask<KeyButNames> currentModifiersMask = Mask.emptyMask(KeyButNames.class);
    private Mask<KeyButNames> ignoreKeyReleaseModifiersMask = Mask.emptyMask(KeyButNames.class);
    private final KeyboardModifiersListenerList modifierListeners = new KeyboardModifiersListenerList();



    public Keyboard(XServer xServer) {
        this.xServer = xServer;
    }
    private KeyButNames getModifierForKeycode(byte b) {
        return this.xServer.getKeyboardModelManager().getKeyboardModel().getModifiersLayout().getModifierByKeycode(b);
    }
    private Mask<KeyButNames> updateModifiersMaskForKeyPressed(byte b) {
        Mask<KeyButNames> mask = this.currentModifiersMask;
        KeyboardModifiersLayout modifiersLayout = this.xServer.getKeyboardModelManager().getKeyboardModel().getModifiersLayout();
        KeyButNames modifierByKeycode = modifiersLayout.getModifierByKeycode(b);
        if (modifierByKeycode != null && !this.currentModifiersMask.isSet(modifierByKeycode)) {
            mask = Mask.create(KeyButNames.class, this.currentModifiersMask.getRawMask());
            this.currentModifiersMask.set(modifierByKeycode);
            if (modifiersLayout.isModifierSticky(modifierByKeycode)) {
                Assert.isFalse(this.ignoreKeyReleaseModifiersMask.isSet(modifierByKeycode));
                this.ignoreKeyReleaseModifiersMask.set(modifierByKeycode);
            }
            this.modifierListeners.sendModifiersChanged(this.currentModifiersMask);
        }
        return mask;
    }
    private Mask<KeyButNames> updateModifiersMaskForKeyReleased(byte b) {
        Mask<KeyButNames> mask = this.currentModifiersMask;
        KeyboardModifiersLayout modifiersLayout = this.xServer.getKeyboardModelManager().getKeyboardModel().getModifiersLayout();
        KeyButNames modifierByKeycode = modifiersLayout.getModifierByKeycode(b);
        if (modifierByKeycode == null || countModifierPressedKeys(modifierByKeycode) != 0) {
            return mask;
        }
        Assert.isTrue(this.currentModifiersMask.isSet(modifierByKeycode));
        if (this.ignoreKeyReleaseModifiersMask.isSet(modifierByKeycode)) {
            Assert.isTrue(modifiersLayout.isModifierSticky(modifierByKeycode));
            this.ignoreKeyReleaseModifiersMask.clear(modifierByKeycode);
            return mask;
        }
        Mask<KeyButNames> create = Mask.create(KeyButNames.class, this.currentModifiersMask.getRawMask());
        this.currentModifiersMask.clear(modifierByKeycode);
        this.modifierListeners.sendModifiersChanged(this.currentModifiersMask);
        return create;
    }

    private int countModifierPressedKeys(KeyButNames keyButNames) {
        int i = 0;
        for (Byte b : this.xServer.getKeyboardModelManager().getKeyboardModel().getModifiersLayout().getModifierKeycodes(keyButNames)) {
            if (this.keymapStateMask.isKeycodePressed(b)) {
                i++;
            }
        }
        return i;
    }

    public void keyPressed(byte b, int i) {
        //如果当前keycode没有被按下，且没有组合键
        if (!this.keymapStateMask.isKeycodePressed(b) || getModifierForKeycode(b) == null) {
            this.keymapStateMask.setKeycode(b);
            this.keylisteners.sendKeyPressed(b, i, updateModifiersMaskForKeyPressed(b));
        }
    }

    public void keyReleased(byte b, int i) {
        if (this.keymapStateMask.isKeycodePressed(b)) {
            this.keymapStateMask.clearKeycode(b);
            this.keylisteners.sendKeyReleased(b, i, updateModifiersMaskForKeyReleased(b));
        }
    }

    public static class KeymapStateMask {
        private final byte[] keys;

        private KeymapStateMask() {
            this.keys = new byte[32];
        }

        public void setKeycode(byte b) {
            int extendAsUnsigned = ArithHelpers.extendAsUnsigned(b) / 8;
            this.keys[extendAsUnsigned] = (byte) ((1 << (b & 7)) | this.keys[extendAsUnsigned]);
        }

        public void clearKeycode(byte b) {
            byte[] bArr = this.keys;
            int extendAsUnsigned = ArithHelpers.extendAsUnsigned(b) / 8;
            bArr[extendAsUnsigned] = (byte) ((~(1 << (b & 7))) & bArr[extendAsUnsigned]);
        }

        public boolean isKeycodePressed(byte b) {
            return ((1 << (b & 7)) & this.keys[ArithHelpers.extendAsUnsigned(b) / 8]) != 0;
        }
    }


}
