package com.eltechs.axs.helpers.iab;

/* loaded from: classes.dex */
public class IabResult {
    String mMessage;
    int mResponse;

    public IabResult(int response, String str) {
        this.mResponse = response;
        if (str == null || str.trim().length() == 0) {
            this.mMessage = IabHelper.getResponseDesc(response);
            return;
        }
        this.mMessage = str + " (response: " + IabHelper.getResponseDesc(response) + ")";
    }

    public int getResponse() {
        return this.mResponse;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public boolean isSuccess() {
        return this.mResponse == 0;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public String toString() {
        return "IabResult: " + getMessage();
    }
}
