package com.eltechs.axs.applicationState;

import com.eltechs.axs.configuration.XServerViewConfiguration;

public interface EnvironmentAware {
//    AXSEnvironment getEnvironment();
//
//    AXSEnvironmentService getEnvironmentServiceInstance();

    XServerViewConfiguration getXServerViewConfiguration();
//
//    void setEnvironment(AXSEnvironment aXSEnvironment);
//
//    void setEnvironmentServiceInstance(AXSEnvironmentService aXSEnvironmentService);

    void setXServerViewConfiguration(XServerViewConfiguration xServerViewConfiguration);

}
