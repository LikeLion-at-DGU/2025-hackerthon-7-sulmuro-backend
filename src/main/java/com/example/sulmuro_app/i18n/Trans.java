package com.example.sulmuro_app.i18n;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trans {
    String key() default "";     // 별도 저장키 필요하면(지금은 자동번역만 쓸 것이므로 비워도 OK)
    String prefix() default "";  // "place.name."
    String ref() default "";     // "id" -> place.name.{id}
    String src() default "";     // 원문 언어(예: "EN"). 비우면 DeepL 자동감지
}
