package com.eltechs.axs;

import com.eltechs.axs.xserver.ViewFacade;

public class KeyEventReporter {
    private final ViewFacade xServerFacade;
    public KeyEventReporter(ViewFacade viewFacade) {
        this.xServerFacade = viewFacade;
    }


    public void reportKeyWithSym(KeyCodesX keycode, int keysym) {
        this.xServerFacade.injectKeyTypeWithSym((byte) keycode.getValue(), keysym);

    }
}
