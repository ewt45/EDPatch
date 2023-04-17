package com.eltechs.ed.startupActions;

import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.ed.WineRegistryEditor;
import com.eltechs.ed.guestContainers.GuestContainer;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ContainerStartupAction extends AbstractStartupAction {
    public static String ID_DIVINE_DIVINITY_SETTINGS = "divine_divinity_settings";
    public static String ID_MM7_SETTINGS = "mm7_settings";
    public static String ID_MM8_SETTINGS = "mm8_settings";
    private static final HashMap<String, AbstractAction> actionsMap = new HashMap<String, AbstractAction>() { // from class: com.eltechs.ed.startupActions.ContainerStartupAction.1
        {
            put(ContainerStartupAction.ID_DIVINE_DIVINITY_SETTINGS, new DivineDivinitySettings());
            put(ContainerStartupAction.ID_MM7_SETTINGS, new MM7Settings());
            put(ContainerStartupAction.ID_MM8_SETTINGS, new MM8Settings());
        }
    };
    private GuestContainer mCont;
    private String mIdList;

    /* loaded from: classes.dex */
    private interface AbstractAction {
        void run(GuestContainer guestContainer);
    }

    public ContainerStartupAction(GuestContainer guestContainer, String str) {
        this.mCont = guestContainer;
        this.mIdList = str;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        String[] split;
        for (String str : this.mIdList.split(" ")) {
            if (actionsMap.containsKey(str)) {
                actionsMap.get(str).run(this.mCont);
            }
        }
        sendDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DivineDivinitySettings implements AbstractAction {
        @Override // com.eltechs.ed.startupActions.ContainerStartupAction.AbstractAction
        public void run(GuestContainer guestContainer) {
        }

        private DivineDivinitySettings() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MM7Settings implements AbstractAction {
        private MM7Settings() {
        }

        @Override // com.eltechs.ed.startupActions.ContainerStartupAction.AbstractAction
        public void run(GuestContainer guestContainer) {
            WineRegistryEditor wineRegistryEditor = new WineRegistryEditor(new File(guestContainer.mWinePrefixPath, "system.reg"));
            try {
                wineRegistryEditor.read();
                Integer dwordParam = wineRegistryEditor.getDwordParam("Software\\New World Computing\\Might and Magic VII\\1.0", "Use D3D");
                if (dwordParam == null || dwordParam.intValue() != 0) {
                    wineRegistryEditor.setDwordParam("Software\\New World Computing\\Might and Magic VII\\1.0", "Use D3D", 0);
                }
                Integer dwordParam2 = wineRegistryEditor.getDwordParam("Software\\New World Computing\\Might and Magic VII\\1.0", "startinwindow");
                if (dwordParam2 == null || dwordParam2.intValue() != 0) {
                    wineRegistryEditor.setDwordParam("Software\\New World Computing\\Might and Magic VII\\1.0", "startinwindow", 0);
                }
                wineRegistryEditor.write();
            } catch (IOException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MM8Settings implements AbstractAction {
        private MM8Settings() {
        }

        @Override // com.eltechs.ed.startupActions.ContainerStartupAction.AbstractAction
        public void run(GuestContainer guestContainer) {
            WineRegistryEditor wineRegistryEditor = new WineRegistryEditor(new File(guestContainer.mWinePrefixPath, "system.reg"));
            try {
                wineRegistryEditor.read();
                Integer dwordParam = wineRegistryEditor.getDwordParam("Software\\New World Computing\\Might and Magic Day of the Destroyer\\1.0", "Use D3D");
                if (dwordParam == null || dwordParam.intValue() != 0) {
                    wineRegistryEditor.setDwordParam("Software\\New World Computing\\Might and Magic Day of the Destroyer\\1.0", "Use D3D", 0);
                }
                Integer dwordParam2 = wineRegistryEditor.getDwordParam("Software\\New World Computing\\Might and Magic Day of the Destroyer\\1.0", "startinwindow");
                if (dwordParam2 == null || dwordParam2.intValue() != 0) {
                    wineRegistryEditor.setDwordParam("Software\\New World Computing\\Might and Magic Day of the Destroyer\\1.0", "startinwindow", 0);
                }
                wineRegistryEditor.write();
            } catch (IOException unused) {
            }
        }
    }
}