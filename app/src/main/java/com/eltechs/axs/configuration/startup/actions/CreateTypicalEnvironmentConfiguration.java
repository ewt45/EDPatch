package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.ExagearImageConfiguration.TempDirMaintenanceComponent;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.ALSAServerComponent;
import com.eltechs.axs.environmentService.components.DirectSoundServerComponent;
import com.eltechs.axs.environmentService.components.EtcHostsFileUpdaterComponent;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.environmentService.components.SysVIPCEmulatorComponent;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.network.SocketPaths;
import com.eltechs.axs.productsRegistry.ProductIDs;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;

/* loaded from: classes.dex */
public class CreateTypicalEnvironmentConfiguration<StateClass extends EnvironmentAware & ExagearImageAware & SelectedExecutableFileAware<StateClass>> extends AbstractStartupAction<StateClass> {
    private final boolean forceUseAbstractSockets;
    private final int productId;
    private final XServerViewConfiguration xServerConf;

    public CreateTypicalEnvironmentConfiguration(int i, boolean z) {
        this(i, XServerViewConfiguration.DEFAULT, z);
    }

    public CreateTypicalEnvironmentConfiguration(int i, XServerViewConfiguration xServerViewConfiguration, boolean z) {
        this.productId = i;
        this.xServerConf = xServerViewConfiguration;
        this.forceUseAbstractSockets = z;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        EnvironmentAware environmentAware = getApplicationState();
        EnvironmentCustomisationParameters environmentCustomisationParameters = ((SelectedExecutableFileAware) environmentAware).getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        AXSEnvironment aXSEnvironment = new AXSEnvironment(getAppContext());
        int i = this.productId;
        aXSEnvironment.addComponent(new SysVIPCEmulatorComponent(ProductIDs.getPackageName(this.productId)));
        aXSEnvironment.addComponent(new XServerComponent(environmentCustomisationParameters.getScreenInfo(), i, createXServerSocketConf()));
        aXSEnvironment.addComponent(new ALSAServerComponent(createALSASocketConf()));
        aXSEnvironment.addComponent(new DirectSoundServerComponent(createDSoundServerSocketConf()));
        aXSEnvironment.addComponent(new GuestApplicationsTrackerComponent(createGATServerSocketConf()));
        ExagearImage exagearImage = ((ExagearImageAware) environmentAware).getExagearImage();
        aXSEnvironment.addComponent(new TempDirMaintenanceComponent(exagearImage));
        aXSEnvironment.addComponent(new EtcHostsFileUpdaterComponent(exagearImage));
        environmentAware.setEnvironment(aXSEnvironment);
        environmentAware.setXServerViewConfiguration(this.xServerConf);
        sendDone();
    }

    private UnixSocketConfiguration createVirglServerSocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.VIRGL_SERVER, Integer.valueOf(this.productId))) : UnixSocketConfiguration.createRegularSocket(((ExagearImageAware) ((EnvironmentAware) getApplicationState())).getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.VIRGL_SERVER, Integer.valueOf(this.productId)));
    }

    private UnixSocketConfiguration createALSASocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.ALSA_SERVER, Integer.valueOf(this.productId))) : UnixSocketConfiguration.createRegularSocket(((ExagearImageAware) ((EnvironmentAware) getApplicationState())).getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.ALSA_SERVER, Integer.valueOf(this.productId)));
    }

    private UnixSocketConfiguration createXServerSocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.XSERVER, Integer.valueOf(this.productId))) : UnixSocketConfiguration.createRegularSocket(((ExagearImageAware) ((EnvironmentAware) getApplicationState())).getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.XSERVER, Integer.valueOf(this.productId)));
    }

    private UnixSocketConfiguration createDSoundServerSocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.DSOUND_SERVER, Integer.valueOf(this.productId))) : UnixSocketConfiguration.createRegularSocket(((ExagearImageAware) ((EnvironmentAware) getApplicationState())).getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.DSOUND_SERVER, Integer.valueOf(this.productId)));
    }

    private UnixSocketConfiguration createGATServerSocketConf() {
        return UnixSocketConfiguration.createAbstractSocket(SocketPaths.GUEST_APPLICATIONS_TRACKER + this.productId);
    }
}