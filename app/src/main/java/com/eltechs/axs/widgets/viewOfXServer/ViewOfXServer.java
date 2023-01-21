package com.eltechs.axs.widgets.viewOfXServer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.XServer;

public class ViewOfXServer extends GLSurfaceView {
    private final ViewFacade xServerFacade;
    private final XServerViewConfiguration configuration;

    public ViewOfXServer(Context context, XServer xServer, ViewFacade viewFacade, XServerViewConfiguration configuration) {
        super(context);
        this.configuration = configuration;
        if (viewFacade == null) {
            this.xServerFacade = new ViewFacade(xServer);
        } else {
            this.xServerFacade = viewFacade;
        }

    }

    public ViewFacade getXServerFacade() {
        return this.xServerFacade;
    }

    public void setHorizontalStretchEnabled(boolean horizontalStretchEnabled) {
    }

    public XServerViewConfiguration getConfiguration() {
        return this.configuration;
    }

}
