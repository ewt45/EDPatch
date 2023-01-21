package com.eltechs.axs.xserver.rendering;

public interface RenderingEngine {
    String getGLXExtensionsList();

    String getVendor();

    boolean isRenderingAvailable();
}