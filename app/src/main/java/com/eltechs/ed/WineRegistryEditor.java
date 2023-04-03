package com.eltechs.ed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/* loaded from: classes.dex */
public class WineRegistryEditor {
    private boolean mChanged = false;
    private String mContents;
    private File mFile;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    private class KeyLocation {
        int mBegin;
        int mContentsBegin;
        int mEnd;

        KeyLocation(int i, int i2, int i3) {
            this.mBegin = i;
            this.mContentsBegin = i2;
            this.mEnd = i3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    private class ParamLocation {
        int mBegin;
        int mEnd;
        int mValBegin;

        ParamLocation(int i, int i2, int i3) {
            this.mBegin = i;
            this.mValBegin = i2;
            this.mEnd = i3;
        }
    }

    public WineRegistryEditor(File file) {
        this.mFile = file;
    }

    private String stringToInternal(String str) {
        return str.replace("\\", "\\\\");
    }

    private String stringFromInternal(String str) {
        return str.replace("\\\\", "\\");
    }

    private String insertString(String str, int i, String str2) {
        return str.substring(0, i) + str2 + str.substring(i);
    }

    private String replaceString(String str, int i, int i2, String str2) {
        return str.substring(0, i) + str2 + str.substring(i2);
    }

    public void read() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(this.mFile);
        byte[] bArr = new byte[(int) this.mFile.length()];
        fileInputStream.read(bArr);
        fileInputStream.close();
        this.mContents = new String(bArr);
        this.mChanged = false;
    }

    public void write() throws IOException {
        if (this.mChanged) {
            FileOutputStream fileOutputStream = new FileOutputStream(this.mFile);
            fileOutputStream.write(this.mContents.getBytes());
            fileOutputStream.close();
            this.mChanged = false;
        }
    }

    private KeyLocation getKeyLocation(String str) {
        int indexOf = this.mContents.indexOf("[" + stringToInternal(str) + "]");
        if (indexOf == -1) {
            return new KeyLocation(-1, -1, -1);
        }
        int indexOf2 = this.mContents.indexOf(10, indexOf) + 1;
        int indexOf3 = this.mContents.indexOf(91, indexOf2);
        if (indexOf3 == -1) {
            indexOf3 = this.mContents.length();
        }
        do {
            indexOf3--;
        } while (this.mContents.charAt(indexOf3) == '\n');
        return new KeyLocation(indexOf, indexOf2, indexOf3 + 1);
    }

    private ParamLocation getParamLocation(KeyLocation keyLocation, String str) {
        String str2 = "\"" + stringToInternal(str) + "\"=";
        String substring = this.mContents.substring(keyLocation.mContentsBegin, keyLocation.mEnd);
        int indexOf = substring.indexOf(str2);
        if (indexOf == -1) {
            return new ParamLocation(-1, -1, -1);
        }
        int length = str2.length() + indexOf;
        int indexOf2 = substring.indexOf(10, length);
        if (indexOf2 == -1) {
            indexOf2 = substring.length();
        }
        return new ParamLocation(keyLocation.mContentsBegin + indexOf, keyLocation.mContentsBegin + length, keyLocation.mContentsBegin + indexOf2);
    }

    private KeyLocation createKey(String str) {
        this.mContents += "\n[" + stringToInternal(str) + String.format("] %d\n", Long.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
        int length = this.mContents.length() - 1;
        return new KeyLocation(this.mContents.length() + 1, length, length);
    }

    public boolean isParamExists(String str, String str2) {
        KeyLocation keyLocation = getKeyLocation(str);
        return (keyLocation.mBegin == -1 || getParamLocation(keyLocation, str2).mBegin == -1) ? false : true;
    }

    public String getStringParam(String str, String str2) {
        KeyLocation keyLocation = getKeyLocation(str);
        if (keyLocation.mBegin == -1) {
            return null;
        }
        ParamLocation paramLocation = getParamLocation(keyLocation, str2);
        if (paramLocation.mBegin == -1) {
            return null;
        }
        return stringFromInternal(this.mContents.substring(paramLocation.mValBegin + 1, paramLocation.mEnd - 1));
    }

    public void setStringParam(String str, String str2, String str3) {
        KeyLocation keyLocation = getKeyLocation(str);
        if (keyLocation.mBegin == -1) {
            keyLocation = createKey(str);
        }
        ParamLocation paramLocation = getParamLocation(keyLocation, str2);
        if (paramLocation.mBegin == -1) {
            int i = keyLocation.mEnd;
            String str4 = "\n\"" + stringToInternal(str2) + "\"=\"\"";
            int length = (keyLocation.mEnd + str4.length()) - 2;
            this.mContents = insertString(this.mContents, keyLocation.mEnd, str4);
            paramLocation = new ParamLocation(i, length, length + 2);
        }
        this.mContents = replaceString(this.mContents, paramLocation.mValBegin + 1, paramLocation.mEnd - 1, stringToInternal(str3));
        this.mChanged = true;
    }

    public Integer getDwordParam(String str, String str2) {
        KeyLocation keyLocation = getKeyLocation(str);
        if (keyLocation.mBegin == -1) {
            return null;
        }
        ParamLocation paramLocation = getParamLocation(keyLocation, str2);
        if (paramLocation.mBegin == -1) {
            return null;
        }
        return Integer.decode("0x" + this.mContents.substring(paramLocation.mValBegin + "dword:".length(), paramLocation.mEnd));
    }

    public void setDwordParam(String str, String str2, Integer num) {
        KeyLocation keyLocation = getKeyLocation(str);
        if (keyLocation.mBegin == -1) {
            keyLocation = createKey(str);
        }
        ParamLocation paramLocation = getParamLocation(keyLocation, str2);
        if (paramLocation.mBegin == -1) {
            int i = keyLocation.mEnd;
            String str3 = "\n\"" + stringToInternal(str2) + "\"=";
            String str4 = str3 + "dword:00000000";
            this.mContents = insertString(this.mContents, keyLocation.mEnd, str4);
            paramLocation = new ParamLocation(i, str3.length() + i, str4.length() + i);
        }
        this.mContents = replaceString(this.mContents, paramLocation.mValBegin + "dword:".length(), paramLocation.mEnd, String.format("%08x", num));
        this.mChanged = true;
    }
}