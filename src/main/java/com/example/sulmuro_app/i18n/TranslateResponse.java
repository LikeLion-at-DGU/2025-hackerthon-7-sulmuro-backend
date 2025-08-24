package com.example.sulmuro_app.i18n;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateResponse {
    /** true면 Accept-Language 헤더 없을 때 번역 스킵 */
    boolean requireAcceptLanguage() default true;
}
