package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class Keyboard {
    private final XServer xServer;
    private Mask<KeyButNames> currentModifiersMask = Mask.emptyMask(KeyButNames.class);
    private Mask<KeyButNames> ignoreKeyReleaseModifiersMask = Mask.emptyMask(KeyButNames.class);
    private final KeymapStateMask keymapStateMask = new KeymapStateMask();
    private final KeyboardListenersList keylisteners = new KeyboardListenersList();
    private final KeyboardModifiersListenerList modifierListeners = new KeyboardModifiersListenerList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private static class KeymapStateMask {
        private final byte[] keys;

        private KeymapStateMask() {
            this.keys = new byte[32];
        }

        public void setKeycode(byte b) {
            int extendAsUnsigned = ArithHelpers.extendAsUnsigned(b) / 8;
            keys[extendAsUnsigned] = (byte) ((1 << (b & 7)) | keys[extendAsUnsigned]);
        }

        public void clearKeycode(byte b) {
            int extendAsUnsigned = ArithHelpers.extendAsUnsigned(b) / 8;
            keys[extendAsUnsigned] = (byte) ((~(1 << (b & 7))) & keys[extendAsUnsigned]);
        }

        public boolean isKeycodePressed(byte b) {
            return ((1 << (b & 7)) & this.keys[ArithHelpers.extendAsUnsigned(b) / 8]) != 0;
        }
    }

    public Keyboard(XServer xServer) {
        this.xServer = xServer;
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

    private KeyButNames getModifierForKeycode(byte b) {
        return this.xServer.getKeyboardModelManager().getKeyboardModel().getModifiersLayout().getModifierByKeycode(b);
    }

    public void keyPressed(byte b, int i) {
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

    public Mask<KeyButNames> getModifiersMask() {
        return this.currentModifiersMask;
    }

    public void addKeyListener(KeyboardListener keyboardListener) {
        this.keylisteners.addListener(keyboardListener);
    }

    public void removeKeyListener(KeyboardListener keyboardListener) {
        this.keylisteners.removeListener(keyboardListener);
    }

    public void addModifierListener(KeyboardModifiersListener keyboardModifiersListener) {
        this.modifierListeners.addListener(keyboardModifiersListener);
    }

    public void removeModifierListener(KeyboardModifiersListener keyboardModifiersListener) {
        this.modifierListeners.removeListener(keyboardModifiersListener);
    }
}