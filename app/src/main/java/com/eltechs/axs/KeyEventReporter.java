package com.eltechs.axs;

import com.eltechs.axs.xserver.ViewFacade;

/* loaded from: classes.dex */
public class KeyEventReporter {
    private final ViewFacade xServerFacade;

    public KeyEventReporter(ViewFacade viewFacade) {
        this.xServerFacade = viewFacade;
    }

    public void reportKeysPress(KeyCodesX... keyCodesXArr) {
        byte[] bArr = new byte[keyCodesXArr.length];
        int i = 0;
        for (KeyCodesX keyCodesX : keyCodesXArr) {
            if (keyCodesX != KeyCodesX.KEY_NONE) {
                bArr[i] = (byte) keyCodesX.getValue();
                i++;
            }
        }
        this.xServerFacade.injectMultiKeyPress(bArr);
    }

    public void reportKeyPressWithSym(KeyCodesX keyCodesX, int i) {
        this.xServerFacade.injectKeyPressWithSym((byte) keyCodesX.getValue(), i);
    }

    public void reportKeysRelease(KeyCodesX... keyCodesXArr) {
        byte[] bArr = new byte[keyCodesXArr.length];
        int i = 0;
        for (KeyCodesX keyCodesX : keyCodesXArr) {
            if (keyCodesX != KeyCodesX.KEY_NONE) {
                bArr[i] = (byte) keyCodesX.getValue();
                i++;
            }
        }
        this.xServerFacade.injectMultiKeyRelease(bArr);
    }

    public void reportKeyReleaseWithSym(KeyCodesX keyCodesX, int i) {
        this.xServerFacade.injectKeyReleaseWithSym((byte) keyCodesX.getValue(), i);
    }

    public void reportKeys(KeyCodesX... keyCodesXArr) {
        byte[] bArr = new byte[keyCodesXArr.length];
        int i = 0;
        for (KeyCodesX keyCodesX : keyCodesXArr) {
            if (keyCodesX != KeyCodesX.KEY_NONE) {
                bArr[i] = (byte) keyCodesX.getValue();
                i++;
            }
        }
        this.xServerFacade.injectMultiKeyType(bArr);
    }

    public void reportKeyWithSym(KeyCodesX keyCodesX, int i) {
        this.xServerFacade.injectKeyTypeWithSym((byte) keyCodesX.getValue(), i);
    }

    public void reportKeyPressReleaseNoDelay(KeyCodesX keyCodesX) {
        this.xServerFacade.injectKeyPress((byte) keyCodesX.getValue());
        this.xServerFacade.injectKeyRelease((byte) keyCodesX.getValue());
    }
}