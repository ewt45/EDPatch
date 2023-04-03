package com.eltechs.ed;

import android.content.Context;

/* loaded from: classes.dex */
public class Locales {
    public static final String DEFAULT_LOCALE = "zh_CN.utf8";
    public static final String[] SUPPORTED_LOCALES = {DEFAULT_LOCALE, "en_US.utf8", "fr_FR.utf8", "de_DE.utf8", "he_IL.utf8", "it_IT.utf8", "ru_RU.utf8", "pl_PL.utf8", "pt_PT.utf8", "es_ES.utf8"};

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getLocaleByDevice(Context context) {
        char c;
        String iSO3Language = context.getResources().getConfiguration().locale.getISO3Language();
        switch (iSO3Language.hashCode()) {
            case 98385:
                if (iSO3Language.equals("ces")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 99348:
                if (iSO3Language.equals("deu")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 101653:
                if (iSO3Language.equals("fra")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 103173:
                if (iSO3Language.equals("heb")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 104598:
                if (iSO3Language.equals("ita")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 111181:
                if (iSO3Language.equals("pol")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 111187:
                if (iSO3Language.equals("por")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 113296:
                if (iSO3Language.equals("rus")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 114084:
                if (iSO3Language.equals("spa")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 115868:
                if (iSO3Language.equals("chn")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "cs_CZ.utf8";
            case 1:
                return "de_DE.utf8";
            case 2:
                return "fr_FR.utf8";
            case 3:
                return "he_IL.utf8";
            case 4:
                return "it_IT.utf8";
            case 5:
                return "pl_PL.utf8";
            case 6:
                return "pt_PT.utf8";
            case 7:
            case '\b':
                return "ru_RU.utf8";
            case '\t':
                return "es_US.utf8";
            default:
                return DEFAULT_LOCALE;
        }
    }
}