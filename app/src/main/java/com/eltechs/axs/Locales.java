package com.eltechs.axs;

/* loaded from: classes.dex */
public abstract class Locales {
    public static final String CHARSET_RUS = "ru_RU.UTF-8";
    public static final String CHARSET_POL = "pl_PL.UTF-8";
    public static final String CHARSET_DEU = "de_DE.UTF-8";
    public static final String CHARSET_SPA = "es_ES.UTF-8";
    public static final String CHARSET_FRA = "fr_FR.UTF-8";
    public static final String CHARSET_POR = "pt_PT.UTF-8";
    private static final String[] SUPPORTED_LOCALES = {
            "C", "en_US", "en_US.UTF8", "ru_RU.CP1251", "ru_RU.UTF-8", "pl_PL.CP1250", "pl_PL.UTF-8",
            "cs_CZ.CP1250", "cs_CZ.UTF-8", "de_DE.CP1252", "de_DE.UTF-8", "es_ES.CP1252", "es_ES.UTF-8",
            "fr_FR.CP1252", "fr_FR.UTF-8", "pt_PT.CP1252", "pt_PT.UTF-8", "pt_BR.CP1252", "pt_BR.UTF-8"};

    public static String[] getSupportedLocales() {
        return SUPPORTED_LOCALES.clone();
    }

    public static String guessLocale() {
        switch (Globals.getAppContext().getResources().getConfiguration().locale.getISO3Language()) {
            case "rus":
            case "ukr":
                return CHARSET_RUS;
            case "pol":
            case "ces":
                return CHARSET_POL;
            case "deu":
                return CHARSET_DEU;
            case "fra":
                return CHARSET_FRA;
            case "spa":
                return CHARSET_SPA;
            case "por":
                return CHARSET_POR;
            default:
                return "C";
        }
    }
}