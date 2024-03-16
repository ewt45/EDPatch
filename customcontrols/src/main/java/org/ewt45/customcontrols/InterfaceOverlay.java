package org.ewt45.customcontrols;

import android.app.Activity;
import android.view.View;

public interface InterfaceOverlay<T extends Activity,O> {
    View attach(T activity, O viewOfXServer);

    void detach();
}
