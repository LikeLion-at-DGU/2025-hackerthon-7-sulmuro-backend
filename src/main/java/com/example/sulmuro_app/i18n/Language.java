package com.example.sulmuro_app.i18n;

import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;

public enum Language {
    EN, KO, ZH; //영어 한국어 중국어

    public static Language ofCurrentLocale() {
        Locale lc = LocaleContextHolder.getLocale();
        if (lc == null) return EN;
        String lang = lc.getLanguage().toLowerCase();
        if ("ko".equals(lang)) return KO;
        if ("zh".equals(lang)) return ZH;
        return EN;
    }

    public String deeplCode() {
        return switch (this) {
            case EN -> "EN";
            case KO -> "KO";
            case ZH -> "ZH"; // 간체.
        };
    }
}
