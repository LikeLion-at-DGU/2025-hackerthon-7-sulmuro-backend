package com.example.sulmuro_app.i18n;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class TranslationAspect {

    private final TranslationService translationService;


    @Around(
            "@annotation(org.springframework.web.bind.annotation.GetMapping) " +
                    "|| @annotation(com.example.sulmuro_app.i18n.TranslateResponse)"
    )
    public Object aroundTranslatable(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        // 0) 정적 업로드 경로 요청이면 즉시 스킵
        if (isStaticUploadRequest()) return result;

        // 0-1) 바이너리/스트리밍 응답은 스킵
        if (isBinaryResponse(result)) return result;

        // 1) @TranslateResponse의 requireAcceptLanguage 결정
        boolean requireHeader = true; // 기본값
        TranslateResponse ann = getAnnotation(pjp, TranslateResponse.class);
        if (ann != null) {
            requireHeader = ann.requireAcceptLanguage();
        }

        // 2) Accept-Language 헤더가 필요하고 없으면 스킵
        String accept = currentAcceptLanguage();
        if (requireHeader && !StringUtils.hasText(accept)) {
            return result;
        }

        // 3) 번역 수행
        Language lang = Language.ofCurrentLocale();
        if (result instanceof ResponseEntity<?> re) {
            Object body = re.getBody();
            translateAny(body, lang);
            return ResponseEntity.status(re.getStatusCode())
                    .headers(re.getHeaders())
                    .body(body);
        }
        translateAny(result, lang);
        return result;
    }

    // ===== 번역 처리 =====

    private void translateAny(Object obj, Language lang) {
        if (obj == null) return;

        Object unwrapped = tryUnwrapData(obj);
        if (unwrapped != obj) {
            translateObjectFields(obj, lang);
            translateAny(unwrapped, lang);
            return;
        }

        if (obj instanceof Page<?> p) { p.getContent().forEach(it -> translateAny(it, lang)); return; }
        if (obj instanceof Collection<?> c) { c.forEach(it -> translateAny(it, lang)); return; }
        if (obj.getClass().isArray()) {
            int n = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < n; i++) translateAny(java.lang.reflect.Array.get(obj, i), lang);
            return;
        }
        if (obj instanceof Map<?, ?> m) { m.values().forEach(v -> translateAny(v, lang)); return; }

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

        if (obj instanceof Map<?, ?> map && map.containsKey("data")) return map.get("data");
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
            } finally {
                f.setAccessible(acc);
            }
        }
    }

    // ===== 유틸 =====

    private boolean isStaticUploadRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            String uri = sra.getRequest().getRequestURI();
            return uri != null && uri.startsWith("/uploads/");
        }
        return false;
    }

    private boolean isBinaryResponse(Object result) {
        if (result instanceof ResponseEntity<?> re) {
            Object body = re.getBody();
            return body instanceof byte[]
                    || body instanceof Resource
                    || body instanceof StreamingResponseBody;
        }
        return result instanceof byte[]
                || result instanceof Resource
                || result instanceof StreamingResponseBody;
    }

    private String currentAcceptLanguage() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest req = sra.getRequest();
            return req.getHeader("Accept-Language");
        }
        return null;
    }

    // 메서드/클래스에서 합성/메타 어노테이션까지 찾아 반환
    private <A extends Annotation> A getAnnotation(ProceedingJoinPoint pjp, Class<A> annType) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        A onMethod = AnnotatedElementUtils.findMergedAnnotation(method, annType);
        if (onMethod != null) return onMethod;
        return AnnotatedElementUtils.findMergedAnnotation(pjp.getTarget().getClass(), annType);
    }
}
