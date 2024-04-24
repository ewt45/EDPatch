package com.eltechs.axs.configuration.startup.actions;

import android.net.LocalServerSocket;
import android.util.Log;
import android.widget.Toast;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.ExagearImageConfiguration.TempDirMaintenanceComponent;
import com.eltechs.axs.Globals;
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
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.network.SocketPaths;
import com.eltechs.axs.productsRegistry.ProductIDs;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;

import java.io.IOException;

/* loaded from: classes.dex */
public class CreateTypicalEnvironmentConfiguration<StateClass extends EnvironmentAware & ExagearImageAware & SelectedExecutableFileAware<StateClass>> extends AbstractStartupAction<StateClass> {
    private final boolean forceUseAbstractSockets;
    private final int productId;
    private final XServerViewConfiguration xServerConf;

    public CreateTypicalEnvironmentConfiguration(int productId, boolean forceUseAbstractSockets) {
        this(productId, XServerViewConfiguration.DEFAULT, forceUseAbstractSockets);
    }

    public CreateTypicalEnvironmentConfiguration(int productId, XServerViewConfiguration xServerViewConfiguration, boolean forceUseAbstractSockets) {
        this.productId = productId;
        this.xServerConf = xServerViewConfiguration;
        this.forceUseAbstractSockets = forceUseAbstractSockets;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        EnvironmentAware environmentAware = getApplicationState();
        EnvironmentCustomisationParameters environmentCustomisationParameters = ((SelectedExecutableFileAware) environmentAware).getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        AXSEnvironment aXSEnvironment = new AXSEnvironment(getAppContext());
        aXSEnvironment.addComponent(new SysVIPCEmulatorComponent(ProductIDs.getPackageName(this.productId)));
        aXSEnvironment.addComponent(new XServerComponent(environmentCustomisationParameters.getScreenInfo(), this.productId, createXServerSocketConf()));
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
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.VIRGL_SERVER, this.productId)) : UnixSocketConfiguration.createRegularSocket(getApplicationState().getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.VIRGL_SERVER, this.productId));
    }

    private UnixSocketConfiguration createALSASocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.ALSA_SERVER, this.productId)) : UnixSocketConfiguration.createRegularSocket(getApplicationState().getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.ALSA_SERVER, this.productId));
    }

    private UnixSocketConfiguration createXServerSocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.XSERVER, this.productId)) : UnixSocketConfiguration.createRegularSocket(getApplicationState().getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.XSERVER, this.productId));
    }

    private UnixSocketConfiguration createDSoundServerSocketConf() {
        return this.forceUseAbstractSockets ? UnixSocketConfiguration.createAbstractSocket(String.format("%s%d", SocketPaths.DSOUND_SERVER, this.productId)) : UnixSocketConfiguration.createRegularSocket(getApplicationState().getExagearImage().getPath().getAbsolutePath(), String.format("%s%d", SocketPaths.DSOUND_SERVER, this.productId));
    }

    private UnixSocketConfiguration createGATServerSocketConf() {
        // 避开socket冲突，以便多个exa能同时运行。
        int increase = 0;
        String name = "";
        for(; increase<10; increase++){
            name = SocketPaths.GUEST_APPLICATIONS_TRACKER + (productId + increase);
            try (LocalServerSocket ignored = new LocalServerSocket(name)) {
                Log.d("ConnectionListener", "forAbstractAfUnixAddress: 该af_unix socket 文件地址可用 "+name);
                break;
            }catch (IOException e){
                UiThread.post(()-> Toast.makeText(Globals.getAppContext(), "检测到多个exa同时运行。尝试更换socket地址", Toast.LENGTH_SHORT).show());
                Log.e("ConnectionListener", "forAbstractAfUnixAddress: 该af_unix socket 文件地址不可用 "+name, e);
            }
        }
        return UnixSocketConfiguration.createAbstractSocket(name);
    }
}