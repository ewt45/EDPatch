package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.KeyboardModel;
import com.eltechs.axs.xserver.KeyboardModelManager;

public class KeyboardModelManagerImpl implements KeyboardModelManager {
    private KeyboardModel keyboardModel;

    public KeyboardModelManagerImpl(KeyboardModel keyboardModel) {
        this.keyboardModel = keyboardModel;
    }

    @Override // com.eltechs.axs.xserver.KeyboardModelManager
    public KeyboardModel getKeyboardModel() {
        return this.keyboardModel;
    }

    @Override // com.eltechs.axs.xserver.KeyboardModelManager
    public void setKeyboardModel(KeyboardModel keyboardModel) {
        Assert.notImplementedYet();
    }
}
