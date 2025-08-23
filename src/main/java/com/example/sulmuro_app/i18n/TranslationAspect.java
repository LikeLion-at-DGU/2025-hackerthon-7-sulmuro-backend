package com.example.sulmuro_app.i18n;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class TranslationAspect {

    private final TranslationService translationService;

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object aroundGet(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        // (A) Accept-Language 헤더가 없으면 번역 스킵하고 원본 반환
        HttpServletRequest req = null;
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            req = sra.getRequest();
        }
        String accept = (req != null) ? req.getHeader("Accept-Language") : null;
        if (!StringUtils.hasText(accept)) {
            return result; // 헤더 없으면 번역하지 않음
        }

        // (B) 헤더가 있을 때만 번역 수행
        Language lang = Language.ofCurrentLocale();

        if (result instanceof ResponseEntity<?> re) {
            Object body = re.getBody();
            translateAny(body, lang);
            return ResponseEntity.status(re.getStatusCode()).headers(re.getHeaders()).body(body);
        }
        translateAny(result, lang);
        return result;
    }


    private void translateAny(Object obj, Language lang) {
        if (obj == null) return;

        Object unwrapped = tryUnwrapData(obj);          // ApiResponse<T> → data
        if (unwrapped != obj) {
            translateObjectFields(obj, lang);           // 래퍼의 @Trans (message 등)
            translateAny(unwrapped, lang);
            return;
        }

        if (obj instanceof Page<?> p) { p.getContent().forEach(it -> translateAny(it, lang)); return; }
        if (obj instanceof Collection<?> c) { c.forEach(it -> translateAny(it, lang)); return; }
        if (obj.getClass().isArray()) {
            int n = java.lang.reflect.Array.getLength(obj);
            for (int i=0;i<n;i++) translateAny(java.lang.reflect.Array.get(obj, i), lang);
            return;
        }
        if (obj instanceof Map<?,?> m) { m.values().forEach(v -> translateAny(v, lang)); return; }

        translateObjectFields(obj, lang);
    }

    private Object tryUnwrapData(Object obj) {
        try { var m = obj.getClass().getMethod("getData"); return m.invoke(obj); }
        catch (NoSuchMethodException ignored) {}
        catch (Exception e) { return obj; }
        try { var m = obj.getClass().getMethod("getResult"); return m.invoke(obj); }
        catch (NoSuchMethodException ignored) {}
        catch (Exception e) { return obj; }
        try { var m = obj.getClass().getMethod("getPayload"); return m.invoke(obj); }
        catch (NoSuchMethodException ignored) {}
        catch (Exception e) { return obj; }

        if (obj instanceof Map<?,?> map && map.containsKey("data")) return map.get("data");
        return obj;
    }

    private void translateObjectFields(Object dto, Language lang) {
        for (Field f : dto.getClass().getDeclaredFields()) {
            var trans = AnnotationUtils.getAnnotation(f, Trans.class);
            if (trans == null) continue;

            boolean acc = f.canAccess(dto);
            f.setAccessible(true);
            try {
                Object raw = f.get(dto);
                if (!(raw instanceof String s)) continue;

                String source = trans.src().isBlank() ? null : trans.src().toUpperCase();
                String translated = translationService.translate(s, lang, source);
                f.set(dto, translated);

            } catch (IllegalAccessException ignored) {
            } finally { f.setAccessible(acc); }
        }
    }
}
