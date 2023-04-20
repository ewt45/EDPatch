package com.eltechs.ed;

import android.util.Log;

import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.ed.guestContainers.GuestContainer;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class XDGLink {
    public final String exec;
    public final GuestContainer guestCont;
    public final String icon;
    public final File linkFile;
    public final String name;
    public final String path;

    public XDGLink(GuestContainer guestContainer, String str) throws IOException {
        this(guestContainer, new File(str));
    }

    public XDGLink(GuestContainer guestContainer, File file) throws IOException {
        this.guestCont = guestContainer;
        this.linkFile = file;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        for (String str5 : FileHelpers.readAsLines(file)) {
            str = str5.startsWith("Name=") ? str5.substring("Name=".length()) : str;
            str2 = str5.startsWith("Exec=") ? str5.substring("Exec=".length()) : str2;
            str3 = str5.startsWith("Path=") ? str5.substring("Path=".length()) : str3;
            if (str5.startsWith("Icon=")) {
                str4 = str5.substring("Icon=".length());
            }
        }
        this.name = str;
        this.exec = str2;
        this.path = str3;
        this.icon = str4;
    }

    public String toString() {
        return this.name;
    }
}