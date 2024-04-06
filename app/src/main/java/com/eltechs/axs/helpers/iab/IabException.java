package com.eltechs.axs.helpers.iab;

/* loaded from: classes.dex */
public class IabException extends Exception {
    IabResult mResult;

    public IabException(IabResult iabResult) {
        this(iabResult, null);
    }

    public IabException(int response, String str) {
        this(new IabResult(response, str));
    }

    public IabException(IabResult iabResult, Exception e) {
        super(iabResult.getMessage(), e);
        this.mResult = iabResult;
    }

    public IabException(int resonpse, String str, Exception e) {
        this(new IabResult(resonpse, str), e);
    }

    public IabResult getResult() {
        return this.mResult;
    }
}
